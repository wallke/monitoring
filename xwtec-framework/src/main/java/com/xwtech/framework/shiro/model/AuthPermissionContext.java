package com.xwtech.framework.shiro.model;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by zq on 16/8/15.
 */
public class AuthPermissionContext implements Serializable {
    private Set<String> roles;
    private Set<String> permissions;

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }


    @Override
    public String toString() {
        return "AuthPermissionContext{" +
                ", roles=" + roles +
                ", permissions=" + permissions +
                '}';
    }
}