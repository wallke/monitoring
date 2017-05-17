package com.xwtech.framework.shiro.client;

import com.xwtech.framework.shiro.model.AuthPermissionContext;
import org.apache.shiro.session.Session;

import java.io.Serializable;

/**
 * Created by zq on 16/8/15.
 */
public interface RemoteServiceInterface {

    Session getSession(String appKey, Serializable sessionId);

    Serializable createSession(String appKey, Session session);

    void updateSession(String appKey, Session session);

    void deleteSession(String appKey, Session session);

    AuthPermissionContext getPermissions(String appKey, String username);

}
