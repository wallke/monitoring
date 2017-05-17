package com.xwtech.framework.shiro;

import com.xwtech.framework.shiro.properties.ClientProperties;
import com.xwtech.framework.shiro.properties.RememberMeCookieIdProperties;
import com.xwtech.framework.shiro.properties.SessionIdCookieProperties;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * Created by zq on 16/8/22.
 */
@Configuration
@EnableConfigurationProperties(value = {
        SessionIdCookieProperties.class,
        RememberMeCookieIdProperties.class,
        ClientProperties.class
})
public class ShiroConfig {


    /**
     * 会话Cookie模板
     *
     * @return
     */
    @Bean(name = "sessionIdCookie")
    public SimpleCookie getSimpleCookie(SessionIdCookieProperties sessionIdCookieProperties) {
        SimpleCookie simpleCookie =
                new SimpleCookie(sessionIdCookieProperties.getName());
        simpleCookie.setHttpOnly(sessionIdCookieProperties.isHttpOnly());
        simpleCookie.setMaxAge(sessionIdCookieProperties.getMaxAge());
        simpleCookie.setDomain(sessionIdCookieProperties.getDomain());
        simpleCookie.setPath(sessionIdCookieProperties.getPath());
        return simpleCookie;
    }


    /**
     * rememberMeCookie
     *
     * @return
     */
    @Bean(name = "rememberMeCookie")
    public SimpleCookie getRememberMeCookie(
            RememberMeCookieIdProperties rememberMeCookieIdProperties) {
        SimpleCookie rememberMeCookie =
                new SimpleCookie(rememberMeCookieIdProperties.getName());
        rememberMeCookie.setHttpOnly(rememberMeCookieIdProperties.isHttpOnly());
        rememberMeCookie.setMaxAge(rememberMeCookieIdProperties.getMaxAge());
        rememberMeCookie.setDomain(rememberMeCookieIdProperties.getDomain());
        rememberMeCookie.setPath(rememberMeCookieIdProperties.getPath());
        return rememberMeCookie;
    }


    /**
     * rememberMe管理器
     *
     * @return
     */
    @Bean(name = "rememberMeManager")
    @DependsOn(value = {"rememberMeCookie"})
    public CookieRememberMeManager getCookieRememberMeManager(
            @Autowired
            @Qualifier(value = "rememberMeCookie") SimpleCookie rememberMeCookie) {
        CookieRememberMeManager cookieRememberMeManager =
                new CookieRememberMeManager();
        cookieRememberMeManager.setCipherKey(Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
        cookieRememberMeManager.setCookie(rememberMeCookie);
        return cookieRememberMeManager;

    }


}
