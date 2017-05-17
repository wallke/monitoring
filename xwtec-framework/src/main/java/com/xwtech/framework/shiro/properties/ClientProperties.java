package com.xwtech.framework.shiro.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zq on 16/8/22.
 */
@ConfigurationProperties(prefix = "shiro.client")
public class ClientProperties {

    String platformKey;

    /**
     * 应用标识
     */
    String appKey;

    /**
     * 登录地址
     */
    String loginUrl = "/login";


    /**
     * 登录成功默认跳转地址
     */
    String successUrl = "/";


    /**
     * 未授权跳转地址
     */
    String unauthorizedUrl = "/unauthorized";



    /**
     * 远程权限验证路径
     */
    String globalAuthUrl;


    /**
     * shiro 过滤器匹配规则
     */
    Map<String,String> definitions = new LinkedHashMap<>();


    public String getPlatformKey() {
        return platformKey;
    }

    public void setPlatformKey(String platformKey) {
        this.platformKey = platformKey;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getGlobalAuthUrl() {
        return globalAuthUrl;
    }

    public void setGlobalAuthUrl(String globalAuthUrl) {
        this.globalAuthUrl = globalAuthUrl;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public String getUnauthorizedUrl() {
        return unauthorizedUrl;
    }

    public void setUnauthorizedUrl(String unauthorizedUrl) {
        this.unauthorizedUrl = unauthorizedUrl;
    }


    public Map<String, String> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Map<String, String> definitions) {
        this.definitions = definitions;
    }

}
