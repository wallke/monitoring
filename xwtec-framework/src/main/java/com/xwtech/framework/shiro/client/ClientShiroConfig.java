package com.xwtech.framework.shiro.client;

/**
 * Created by zq on 16/8/26.
 */

import com.xwtech.framework.shiro.ShiroConfig;
import com.xwtech.framework.shiro.properties.ClientProperties;
import com.xwtech.framework.web.bean.config.HttpConfig;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Import({ShiroConfig.class, HttpConfig.class})
@Configuration
@EnableConfigurationProperties(value = {
        ClientProperties.class
})
public class ClientShiroConfig {

//    @Bean
//    @ConditionalOnMissingBean(name = "restTemplate")
//    RestTemplate restTemplate() {
//        RestTemplate template = new RestTemplate();
//        SimpleClientHttpRequestFactory factory =
//                (SimpleClientHttpRequestFactory) template.getRequestFactory();
//        factory.setConnectTimeout(3000);
//        factory.setReadTimeout(30000);
//        return template;
//    }


    @Bean
    public RemoteServiceInterface getRemoteService(
            ClientProperties properties,
            RestTemplate restTemplate) {
        RemoteServiceInterface remoteServiceInterface =
                new RemoteService(properties.getGlobalAuthUrl(),restTemplate);
        return remoteServiceInterface;
    }


    @Bean(name = "clientRealm",initMethod = "init")
    public ClientRealm getClientRealm(
            RemoteServiceInterface remoteService,
            ClientProperties properties) {
        ClientRealm clientRealm = new ClientRealm();
        clientRealm.setAppKey(properties.getAppKey());
        clientRealm.setCachingEnabled(false);
        clientRealm.setRemoteService(remoteService);
        return clientRealm;

    }


    /**
     * @return
     */
    @Bean(name = "sessionDao")
    public ClientSessionDAO getClientSessionDAO(
            RemoteServiceInterface remoteServiceInterface,
            ClientProperties properties
    ) {
        ClientSessionDAO clientSessionDAO = new ClientSessionDAO();
        clientSessionDAO.setSessionIdGenerator(session -> UUID.randomUUID().toString().replaceAll("-", ""));
        clientSessionDAO.setAppKey(properties.getAppKey());
        clientSessionDAO.setRemoteService(remoteServiceInterface);
        return clientSessionDAO;

    }


    /**
     * 会话管理器
     *
     * @return
     */
    @Bean(name = "sessionManager",destroyMethod = "destroy")
    public DefaultWebSessionManager getDefaultWebSessionManager(
            ClientSessionDAO sessionDao,
            Cookie sessionIdCookie) {
        DefaultWebSessionManager defaultWebSessionManager =
                new DefaultWebSessionManager();

        defaultWebSessionManager.setDeleteInvalidSessions(false);
        defaultWebSessionManager.setSessionValidationSchedulerEnabled(false);

        defaultWebSessionManager.setSessionDAO(sessionDao);
        defaultWebSessionManager.setSessionIdCookieEnabled(true);
        defaultWebSessionManager.setSessionIdCookie(sessionIdCookie);
        return defaultWebSessionManager;
    }


    /**
     * 安全管理器
     *
     * @param clientRealm
     * @param sessionManager
     * @param rememberMeManager
     * @return
     */
    @Bean(name = "securityManager",destroyMethod = "destroy")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(
            ClientRealm clientRealm,
            DefaultWebSessionManager sessionManager,
            RememberMeManager rememberMeManager
    ) {

        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(clientRealm);
        defaultWebSecurityManager.setSessionManager(sessionManager);
        defaultWebSecurityManager.setRememberMeManager(rememberMeManager);

        return defaultWebSecurityManager;
    }


//    /**
//     * 相当于调用SecurityUtils.setSecurityManager(securityManager)
//     *
//     * @param securityManager
//     * @return
//     */
//    @Bean
//    public MethodInvokingFactoryBean methodInvokingFactoryBean(DefaultWebSecurityManager securityManager) {
//        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
//        methodInvokingFactoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
//        methodInvokingFactoryBean.setArguments(new DefaultWebSecurityManager[]{securityManager});
//        return methodInvokingFactoryBean;
//    }


    @Bean(name = "clientAuthenticationFilter")
    public ClientAuthenticationFilter getClientAuthenticationFilter() {

        return new ClientAuthenticationFilter();
    }


    /**
     * ShiroFilter<br/>
     * 注意这里参数中的 StudentService 和 IScoreDao 只是一个例子，因为我们在这里可以用这样的方式获取到相关访问数据库的对象，
     * 然后读取数据库相关配置，配置到 shiroFilterFactoryBean 的访问规则中。实际项目中，请使用自己的Service来处理业务逻辑。
     *
     * @return
     * @author SHANHY
     * @create 2016年1月14日
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(
            DefaultWebSecurityManager securityManager,
            ClientAuthenticationFilter clientAuthenticationFilter,
            ClientProperties properties
    ) {

        ShiroFilterFactoryBean shiroFilterFactoryBean =
                new ShiroFilterFactoryBean();
        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl(properties.getLoginUrl());
        // 登录成功后要跳转的连接
        shiroFilterFactoryBean.setSuccessUrl(properties.getSuccessUrl());
        // 登录不成功
        shiroFilterFactoryBean.setUnauthorizedUrl(properties.getUnauthorizedUrl());

        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("authc", clientAuthenticationFilter);

        shiroFilterFactoryBean.setFilters(filterMap);

//        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();

//        String definitions = properties.getDefinitions();
//
//        if (!StringUtils.isEmpty(definitions)) {
//            String[] array = definitions.trim().split(";");
//            for (String item : array) {
//                if (!StringUtils.isEmpty(item)) {
//                    String[] temp = item.split("=");
//                    if(!StringUtils.isEmpty(temp[0]) && !StringUtils.isEmpty(temp[1])){
//                        filterChainDefinitionMap.put(temp[0].trim(), temp[1].trim());
//                    }
//                }
//            }
//
//        }

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        filterChainDefinitionMap.putAll(properties.getDefinitions());
        //filterChainDefinitionMap.put("/**","anon");
        filterChainDefinitionMap.put("/logout","logout");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;
    }


    /**
     * 注册DelegatingFilterProxy（Shiro）
     * 集成Shiro有2种方法：
     * 1. 按这个方法自己组装一个FilterRegistrationBean（这种方法更为灵活，可以自己定义UrlPattern，
     * 在项目使用中你可能会因为一些很但疼的问题最后采用它， 想使用它你可能需要看官网或者已经很了解Shiro的处理原理了）
     * 2. 直接使用ShiroFilterFactoryBean（这种方法比较简单，其内部对ShiroFilter做了组装工作，无法自己定义UrlPattern，
     * 默认拦截 /*）
     *
     * @return
     * @author SHANHY
     * @create 2016年1月13日
     */
    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistration =
                new FilterRegistrationBean(new DelegatingFilterProxy("shiroFilter"));
        //  该值缺省为false,表示生命周期由SpringApplicationContext管理,设置为true则表示由ServletContainer管理
        filterRegistration.addInitParameter("targetFilterLifecycle", "true");
        filterRegistration.setEnabled(true);
        filterRegistration.addUrlPatterns("/*");// 可以自己灵活的定义很多，避免一些根本不需要被Shiro处理的请求被包含进来
        return filterRegistration;
    }


//    /**
//     * Shiro生命周期处理器
//     *
//     * @return
//     */
//    @ConditionalOnMissingBean
//    @Bean(name = "lifecycleBeanPostProcessor")
//    public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
//
//        return new LifecycleBeanPostProcessor();
//    }
//
//
//    @ConditionalOnMissingBean
//    @Bean(name = "defaultAdvisorAutoProxyCreator")
//    @DependsOn("lifecycleBeanPostProcessor")
//    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
//        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
//        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
//        return defaultAdvisorAutoProxyCreator;
//    }


    @Bean
    @ConditionalOnMissingBean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(
            DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor
                = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }


}
