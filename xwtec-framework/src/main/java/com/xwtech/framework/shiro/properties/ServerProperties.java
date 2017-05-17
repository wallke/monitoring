package com.xwtech.framework.shiro.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zq on 16/8/26.
 */
@ConfigurationProperties(prefix = "shiro.server")
public class ServerProperties {



    /**
     * shiro 过滤器匹配规则
     */
    Map<String,String> definitions = new LinkedHashMap<>();


    /**
     * 密码加密次数
     */
    int hashIterations = 2;


    /**
     * 密码加密算法 MD5
     */
    String hashAlgorithmName = "md5";


    /**
     * session 超时时间 毫秒
     */
    long globalSessionTimeout = 1800000l;

    /**
     * 是否开启 shiro session 清理
     */
    boolean sessionValidationSchedulerEnabled = true;


    String appKey ="";


    public long getGlobalSessionTimeout() {
        return globalSessionTimeout;
    }

    public void setGlobalSessionTimeout(long globalSessionTimeout) {
        this.globalSessionTimeout = globalSessionTimeout;
    }

    public Map<String, String> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Map<String, String> definitions) {
        this.definitions = definitions;
    }

    public int getHashIterations() {
        return hashIterations;
    }

    public void setHashIterations(int hashIterations) {
        this.hashIterations = hashIterations;
    }

    public String getHashAlgorithmName() {
        return hashAlgorithmName;
    }

    public void setHashAlgorithmName(String hashAlgorithmName) {
        this.hashAlgorithmName = hashAlgorithmName;
    }

    public boolean isSessionValidationSchedulerEnabled() {
        return sessionValidationSchedulerEnabled;
    }

    public void setSessionValidationSchedulerEnabled(boolean sessionValidationSchedulerEnabled) {
        this.sessionValidationSchedulerEnabled = sessionValidationSchedulerEnabled;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
}
