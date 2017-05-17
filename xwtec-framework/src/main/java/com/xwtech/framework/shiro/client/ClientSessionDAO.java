package com.xwtech.framework.shiro.client;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Created by zq on 16/8/15.
 */
public class ClientSessionDAO extends CachingSessionDAO {

    final Logger logger = LoggerFactory.getLogger(this.getClass());


    private RemoteServiceInterface remoteService;

    private String appKey;

    public void setRemoteService(RemoteServiceInterface remoteService) {

        this.remoteService = remoteService;
    }

    public void setAppKey(String appKey) {

        this.appKey = appKey;
    }


    @Override
    protected void doDelete(Session session) {

        remoteService.deleteSession(appKey, session);
    }

    @Override
    protected void doUpdate(Session session) {

        remoteService.updateSession(appKey, session);
    }


    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = remoteService.createSession(appKey,session);
        assignSessionId(session, sessionId);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {

        return remoteService.getSession(appKey, sessionId);

    }
}