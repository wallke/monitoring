package com.xwtech.admin.shiro;


import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.xwtech.admin.service.IAuthService;
import com.xwtech.admin.shiro.cache.ShiroRedisCacheManager;
import com.xwtech.admin.shiro.credentials.RetryLimitHashedCredentialsMatcher;
import com.xwtech.admin.shiro.filters.ServerFormAuthenticationFilter;
import com.xwtech.admin.shiro.quartz.QuartzSessionValidationScheduler;
import com.xwtech.admin.shiro.realm.UserRealm;
import com.xwtech.framework.shiro.ShiroConfig;
import com.xwtech.framework.shiro.properties.ClientProperties;
import com.xwtech.framework.shiro.properties.ServerProperties;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * shiro 权限配置
 * Created by zq on 16/8/11.
 */
@Configuration
@Import({
    ShiroConfig.class//,MybatisConfig.class
})
@EnableConfigurationProperties(value = {
        ServerProperties.class
})
@ComponentScan(
        {
                "com.xwtech.admin.service"
        })
public class ShiroConfiguration {


    private static final Logger logger = LoggerFactory.getLogger(ShiroConfiguration.class);




    /**
     * Redis缓存管理器
     *
     * @return
     */
    @Bean
    public CacheManager getRedisCacheManager() {
        return new ShiroRedisCacheManager();
    }

    /**
     * @return
     */
    @Bean(name = "sessionDao")
    public CachingSessionDAO getRedisShiroSessionDao(
            CacheManager cacheManager
    ) {
        EnterpriseCacheSessionDAO enterpriseCacheSessionDAO = new EnterpriseCacheSessionDAO();
        enterpriseCacheSessionDAO.setCacheManager(cacheManager);
        enterpriseCacheSessionDAO.setActiveSessionsCacheName("shiro::active");
        enterpriseCacheSessionDAO.setSessionIdGenerator(
                session -> UUID.randomUUID().toString().replaceAll("-", ""));
        return enterpriseCacheSessionDAO;

    }


    /**
     * 凭证匹配器
     *
     * @param redisCacheManager
     * @return
     */
    @Bean(name = "credentialsMatcher")
    public RetryLimitHashedCredentialsMatcher getRetryLimitHashedCredentialsMatcher(
            CacheManager redisCacheManager, ServerProperties serverProperties) {
        RetryLimitHashedCredentialsMatcher retryLimitHashedCredentialsMatcher =
                new RetryLimitHashedCredentialsMatcher(redisCacheManager);
        retryLimitHashedCredentialsMatcher.setHashAlgorithmName(
                serverProperties.getHashAlgorithmName());
        retryLimitHashedCredentialsMatcher.setHashIterations(
                serverProperties.getHashIterations());
        retryLimitHashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        return retryLimitHashedCredentialsMatcher;

    }


    /**
     * Realm实现
     *
     * @param credentialsMatcher
     * @return
     */
    @Bean(name = "userRealm",initMethod = "init")
    public UserRealm getUserRealm(RetryLimitHashedCredentialsMatcher credentialsMatcher) {
        UserRealm userRealm = new UserRealm();
        userRealm.setCredentialsMatcher(credentialsMatcher);
        userRealm.setCachingEnabled(true); //设置cacheManager 开启权限缓存,根据用户登录名
        userRealm.setAuthorizationCacheName("shiro::auth"); //缓存前缀
        //userRealm.setName();
        return userRealm;
    }


    /**
     * 会话管理器
     *
     * @return
     */
    @Bean(name = "sessionManager",destroyMethod = "destroy")
    public DefaultWebSessionManager getDefaultWebSessionManager(
            CachingSessionDAO sessionDao,
            Cookie sessionIdCookie,
            CacheManager cacheManager,
            ServerProperties serverProperties) {
        DefaultWebSessionManager defaultWebSessionManager =
                new DefaultWebSessionManager();
        //会话过期时间
        defaultWebSessionManager.setGlobalSessionTimeout(serverProperties.getGlobalSessionTimeout());
        defaultWebSessionManager.setCacheManager(cacheManager);
        defaultWebSessionManager.setSessionDAO(sessionDao);
        defaultWebSessionManager.setSessionIdCookieEnabled(true);
        defaultWebSessionManager.setSessionIdCookie(sessionIdCookie);

        //禁止URL 带jsessionid ,需客户端支持cookie
        defaultWebSessionManager.setSessionIdUrlRewritingEnabled(false);

        //默认开启  无效Session 清理job  ,SessionManager 设置定时时间无效
        //defaultWebSessionManager.setSessionValidationInterval(AbstractValidatingSessionManager.DEFAULT_SESSION_VALIDATION_INTERVAL);
        defaultWebSessionManager.setSessionValidationSchedulerEnabled(true);
        //设置 无效Session 清理Scheduler
        QuartzSessionValidationScheduler quartzSessionValidationScheduler = new QuartzSessionValidationScheduler();
        quartzSessionValidationScheduler.setSessionManager(defaultWebSessionManager);

        //设置定时 时间 默认1小时
        //quartzSessionValidationScheduler.setSessionValidationInterval(30*1000);
        defaultWebSessionManager.setSessionValidationScheduler(quartzSessionValidationScheduler);
        return defaultWebSessionManager;
    }


    /**
     * 安全管理器
     *
     * @param userRealm
     * @param sessionManager
     * @param rememberMeManager
     * @param cacheManager
     * @return
     */
    @Bean(name = "securityManager",destroyMethod = "destroy")
    @ConditionalOnBean(value = {CacheManager.class,Realm.class,DefaultWebSessionManager.class,RememberMeManager.class})
    public DefaultWebSecurityManager getDefaultWebSecurityManager(
            Realm userRealm,
            DefaultWebSessionManager sessionManager,
            RememberMeManager rememberMeManager,
            CacheManager cacheManager
    ) {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();

        defaultWebSecurityManager.setRealm(userRealm);
        defaultWebSecurityManager.setSessionManager(sessionManager);
        defaultWebSecurityManager.setRememberMeManager(rememberMeManager);
        defaultWebSecurityManager.setCacheManager(cacheManager);
        return defaultWebSecurityManager;
    }


    /**
     * 相当于调用SecurityUtils.setSecurityManager(securityManager)
     *
     * @param securityManager
     * @return
     */
//    @Bean
//    public MethodInvokingFactoryBean methodInvokingFactoryBean(DefaultWebSecurityManager securityManager) {
//
//        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
//        methodInvokingFactoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
//        methodInvokingFactoryBean.setArguments(new DefaultWebSecurityManager[]{securityManager});
//
//        return methodInvokingFactoryBean;
//    }


    /**
     * 基于Form表单的身份验证过滤器
     *
     * @return
     */
    @Bean(name = "authc")
    public ServerFormAuthenticationFilter getServerFormAuthenticationFilter(ClientProperties properties) {
        ServerFormAuthenticationFilter serverFormAuthenticationFilter =
                new ServerFormAuthenticationFilter();
        serverFormAuthenticationFilter.setLogin(properties.getLoginUrl());
        //使用默认值
//        serverFormAuthenticationFilter.setUsernameParam("username");
//        serverFormAuthenticationFilter.setPasswordParam("password");
//        serverFormAuthenticationFilter.setRememberMeParam("rememberMe");
//        serverFormAuthenticationFilter.setCaptchaParam("captcha");
        return serverFormAuthenticationFilter;
    }


//    @Bean(name = "sysUserFilter")
//    public SysUserFilter getSysUserFilter(ServerProperties properties) {
//        SysUserFilter sysUserFilter = new SysUserFilter();
//        sysUserFilter.setAppKey(properties.getAppKey());
//        return sysUserFilter;
//    }


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
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilter"));
        //  该值缺省为false,表示生命周期由SpringApplicationContext管理,设置为true则表示由ServletContainer管理
        filterRegistration.addInitParameter("targetFilterLifecycle", "true");
        filterRegistration.setEnabled(true);
        filterRegistration.addUrlPatterns("/*");// 可以自己灵活的定义很多，避免一些根本不需要被Shiro处理的请求被包含进来
        return filterRegistration;
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
            ClientProperties properties,
            IAuthService authService
    ) {

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl("/login");
        // 登录成功后要跳转的连接
        shiroFilterFactoryBean.setSuccessUrl(properties.getSuccessUrl());

        shiroFilterFactoryBean.setUnauthorizedUrl(properties.getUnauthorizedUrl());

        Map<String, Filter> filterMap = new HashMap<>();
//        filterMap.put("authc", new ServerFormAuthenticationFilter());
//        filterMap.put("user", sysUserFilter);

        LogoutFilter logoutFilter = new LogoutFilter();
        logoutFilter.setRedirectUrl(properties.getLoginUrl());
        filterMap.put("logout", logoutFilter);
        shiroFilterFactoryBean.setFilters(filterMap);

//        Map<String, String> filterChainDefinitionMap =
//        String definitions = properties.getDefinitions();
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
        filterChainDefinitionMap.put("/logout","logout");
        //filterChainDefinitionMap.put("/**","anon");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

//    @Bean
//    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
//        DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
//        daap.setProxyTargetClass(true);
//        return daap;
//    }


//    @Bean
//    public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
//        LifecycleBeanPostProcessor lifecycleBeanPostProcessor =
//                new LifecycleBeanPostProcessor();
//
//        return lifecycleBeanPostProcessor;
//    }

    @Bean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(
            DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor
                = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);

        return authorizationAttributeSourceAdvisor;
    }



    @Bean(name = "shiroDialect")
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

}
