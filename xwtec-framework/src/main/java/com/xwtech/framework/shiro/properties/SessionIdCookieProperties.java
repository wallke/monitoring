package com.xwtech.framework.shiro.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by zq on 16/8/22.
 */
@ConfigurationProperties(prefix = "shiro.session")
public class SessionIdCookieProperties {

    String name = "sid";

    String domain = "";

    String path = "/";

    boolean httpOnly = true;

    int MaxAge = -1;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public int getMaxAge() {
        return MaxAge;
    }

    public void setMaxAge(int maxAge) {
        MaxAge = maxAge;
    }
}
