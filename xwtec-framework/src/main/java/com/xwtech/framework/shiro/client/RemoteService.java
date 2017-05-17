package com.xwtech.framework.shiro.client;

import com.xwtech.framework.shiro.model.AuthPermissionContext;
import com.xwtech.framework.shiro.util.ByteSerializer;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.xml.bind.DatatypeConverter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zq on 16/8/17.
 */

public class RemoteService implements RemoteServiceInterface {


    final static Logger logger = LoggerFactory.getLogger(RemoteService.class);

    MultiValueMap<String, Object> headers;

    public RemoteService(String globalAuthUrl,RestTemplate restTemplate) {
        this.globalAuthUrl = globalAuthUrl + "/center/auth/";

        this.headers = new LinkedMultiValueMap<>();
        this.headers.add("Accept", "application/json");
        this.headers.add("Content-Type", "application/json");
        this.restTemplate = restTemplate;
    }


    RestTemplate restTemplate;



    String globalAuthUrl;


    @Override
    public Session getSession(String appKey, Serializable sessionId) {

        //logger.debug("client:getSession:{}", appKey);

        ByteSerializer byteSerializer = new ByteSerializer();
        Map param = new HashMap();
        param.put("sessionId", sessionId);
        param.put("appKey", appKey);

        HttpEntity<Map> request = new HttpEntity(param, this.headers);
        try {
            ResponseEntity<Map> responseEntity =
                    restTemplate.postForEntity(globalAuthUrl + "get", request, Map.class);

            Object result = responseEntity.getBody().get("result");
            if (!StringUtils.isEmpty(result)) {
                try {
                    byte[] parseBase64Binary = DatatypeConverter.parseBase64Binary(result.toString());
                    Session session = (Session) byteSerializer.deserialize(parseBase64Binary);
                    return session;
                } catch (Exception ex) {
                    logger.error("client:getSession 反序列化Session异常 ", ex);
                }
            }
        } catch (Exception ex) {
            logger.error("client:getSession 获取Session 请求异常 ", ex);

        }
        return null;

    }

    @Override
    public Serializable createSession(String appKey, Session session) {

        logger.debug("client:createSession:{}", appKey);

        ByteSerializer byteSerializer = new ByteSerializer();
        Map param = new HashMap();
        byte[] sessionBytes = byteSerializer.serialize(session);
        param.put("session", sessionBytes);
        param.put("appKey", appKey);

        HttpEntity<Map> request = new HttpEntity(param, this.headers);

        try {
            ResponseEntity<Map> responseEntity =
                    restTemplate.postForEntity(globalAuthUrl + "create", request, Map.class);

            Object result = responseEntity.getBody().get("result");

            return (Serializable) result;
        } catch (Exception ex) {
            logger.error("client:createSession 创建Session 请求异常 ", ex);

        }
        return null;
    }

    @Override
    public void updateSession(String appKey, Session session) {

        //logger.debug("client:updateSession:{}", appKey);

        ByteSerializer byteSerializer = new ByteSerializer();

        Map param = new HashMap();
        byte[] sessionBytes = byteSerializer.serialize(session);
        param.put("session", sessionBytes);
        param.put("appKey", appKey);

        HttpEntity<Map> request = new HttpEntity(param, this.headers);

        try {
            restTemplate.postForEntity(globalAuthUrl + "update", request, String.class);
        } catch (Exception ex) {
            logger.error("client:updateSession 更新Session 请求异常 ", ex);

        }

    }

    @Override
    public void deleteSession(String appKey, Session session) {

        logger.debug("client:deleteSession:{}", appKey);

        ByteSerializer byteSerializer = new ByteSerializer();
        Map param = new HashMap();
        byte[] sessionBytes = byteSerializer.serialize(session);
        param.put("session", sessionBytes);
        param.put("appKey", appKey);


        HttpEntity<Map> request = new HttpEntity(param, this.headers);

        try {
            restTemplate.postForEntity(globalAuthUrl + "delete", request, String.class);
        } catch (Exception ex) {
            logger.error("client:deleteSession 删除Session 请求异常 ", ex);

        }
    }

    @Override
    public AuthPermissionContext getPermissions(String appKey, String username) {

        logger.debug("client:getPermissions:{}", appKey);

        Map param = new HashMap();
        param.put("appKey", appKey);
        param.put("username", username);

        HttpEntity<Map> request = new HttpEntity(param, this.headers);
        try {
            ResponseEntity<AuthPermissionContext> responseEntity =
                    restTemplate.postForEntity(globalAuthUrl + "permissions", request, AuthPermissionContext.class);
            return responseEntity.getBody();
        } catch (Exception ex) {
            logger.error("client:getPermissions 获取用户权限 请求异常 ", ex);

        }
        return null;

    }


}
