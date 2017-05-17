package com.xwtech.framework.sign.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by zq on 16/8/30.
 */
@ConfigurationProperties(prefix = "sign")
@Component
public class SignProperties {

    String globalAuthUrl;

    String[] includePatterns;


    String[] excludePatterns;


    public String getGlobalAuthUrl() {
        return globalAuthUrl;
    }

    public void setGlobalAuthUrl(String globalAuthUrl) {
        this.globalAuthUrl = globalAuthUrl;
    }

    public String[] getIncludePatterns() {
        return includePatterns;
    }

    public void setIncludePatterns(String[] includePatterns) {
        this.includePatterns = includePatterns;
    }

    public String[] getExcludePatterns() {
        return excludePatterns;
    }

    public void setExcludePatterns(String[] excludePatterns) {
        this.excludePatterns = excludePatterns;
    }
}
