package com.xwtech.admin.controller;

import com.xwtech.admin.service.IAuthService;
import com.xwtech.framework.shiro.model.AuthPermissionContext;
import com.xwtech.framework.shiro.model.auth.App;
import com.xwtech.framework.shiro.util.ByteSerializer;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import xwtec.servercm.ServerCM;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.util.HashMap;
import java.util.Map;

/**
 * shiro 客户端会话管理中心 访问需白名单认证:如IP白名单
 * Created by zq on 16/8/17.
 */
@RestController
@RequestMapping("center/auth")
public class GlobalAuthController {

    final static Logger logger = LoggerFactory.getLogger(GlobalAuthController.class);

    /**
     * 权限
     */
    @Autowired
    private IAuthService authService;


    /**
     * shiro会话
     */
    @Resource(name = "sessionDao")
    private SessionDAO sessionDAO;


    /**
     * 获取shiro Session会话
     *
     * @param map appKey:应用标识;sessionId:会话Id
     * @return shiro Session
     */
    @RequestMapping(value = "get", method = {RequestMethod.POST}, consumes = {
            "application/json", "application/xml"}, produces = {"application/xml", "application/json"})
    public ResponseEntity<Map<String,Object>> getSession(
            @RequestBody Map<String,Object> map) {

        String appKey = map.containsKey("appKey") ? map.get("appKey").toString() : null;

        String sessionId = map.containsKey("sessionId") ? map.get("sessionId").toString() : null;
        logger.debug("method:getSession");
        logger.debug("client:{},sessionId:{}", appKey, sessionId);

        ByteSerializer byteSerializer = new ByteSerializer();
        Session session = null;
        Map<String,Object> param = new HashMap<String,Object>();
        try {
            if (!StringUtils.isEmpty(sessionId)) {
                session = sessionDAO.readSession(sessionId);
                if (session == null) {
                    return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
                } else {
                    param.put("result", byteSerializer.serialize(session));
                }
            }
        } catch (UnknownSessionException ex) {
            logger.error(ex.getMessage());
        }
        return new ResponseEntity<>(param, HttpStatus.OK);

    }


    /**
     * 创建shiro Session
     *
     * @param map appKey:应用标识;session:会话信息:二进制Base64
     * @return 会话标识
     */
    @RequestMapping(value = "create", method = {RequestMethod.POST}, consumes = {
            "application/json", "application/xml"}, produces = {"application/xml", "application/json"})
    public ResponseEntity<Map<String,Object>> createSession(@RequestBody Map<String,Object> map) {
        String appKey = map.containsKey("appKey") ? map.get("appKey").toString() : null;
        String sessionStr = map.containsKey("session") ? map.get("session").toString() : null;
        logger.debug("method:createSession");
        logger.debug("client:{}", appKey);
        Session session = null;
        if (!StringUtils.isEmpty(sessionStr)) {
            try {
                byte[] parseBase64Binary = DatatypeConverter.parseBase64Binary(sessionStr);

                ByteSerializer byteSerializer = new ByteSerializer();

                session = (Session) byteSerializer.deserialize(parseBase64Binary);

            } catch (Exception ex) {
                logger.error("server:createSession 反序列化session异常", ex);
            }
        }

        Map<String,Object> result = new HashMap<>();
        String serializable = null;
        if (session != null) {
            try {
                serializable = (String) sessionDAO.create(session);
            } catch (Exception ex) {
                logger.error("server:createSession 创建session异常", ex);
                return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }


        }
        result.put("result", serializable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    /**
     * 更新shiro Session
     *
     * @param map appKey:应用标识;session:会话信息:二进制Base64
     * @return
     */
    @RequestMapping(value = "update", method = {RequestMethod.POST}, consumes = {
            "application/json", "application/xml"}, produces = {"application/xml", "application/json"})
    public ResponseEntity<Map<String,Object>> updateSession(@RequestBody Map<String,Object> map) {
        String appKey = map.containsKey("appKey") ? map.get("appKey").toString() : null;
        String sessionStr = map.containsKey("session") ? map.get("session").toString() : null;

        logger.debug("method:updateSession");
        logger.debug("client:{}", appKey);
        if (!StringUtils.isEmpty(sessionStr)) {
            try {
                byte[] parseBase64Binary = DatatypeConverter.parseBase64Binary(sessionStr);

                ByteSerializer byteSerializer = new ByteSerializer();

                Session session = (Session) byteSerializer.deserialize(parseBase64Binary);

                sessionDAO.update(session);

            } catch (Exception ex) {
                logger.error("server:updateSession 反序列化session异常", ex);
                return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity(null, HttpStatus.OK);
    }


    /**
     * 删除shiro Session
     *
     * @param map appKey:应用标识;session:会话信息:二进制Base64
     * @return
     */
    @RequestMapping(value = "delete", method = {RequestMethod.POST}, consumes = {
            "application/json", "application/xml"}, produces = {"application/xml", "application/json"})
    public ResponseEntity<Map<String,Object>> deleteSession(@RequestBody Map<String,Object> map) {

        String appKey = map.containsKey("appKey") ? map.get("appKey").toString() : null;
        String sessionStr = map.containsKey("session") ? map.get("session").toString() : null;

        logger.debug("method:deleteSession");
        logger.debug("client:{}", appKey);

        if (!StringUtils.isEmpty(sessionStr)) {
            try {
                byte[] parseBase64Binary = DatatypeConverter.parseBase64Binary(sessionStr);

                ByteSerializer byteSerializer = new ByteSerializer();

                Session session = (Session) byteSerializer.deserialize(parseBase64Binary);

                sessionDAO.delete(session);

            } catch (Exception ex) {
                logger.error("server:deleteSession 反序列化session异常", ex);
                return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity(null, HttpStatus.OK);
    }


    /**
     * 获取用户角色/权限
     *
     * @param map appKey:应用标识;username:用户名(登录名)
     * @return AuthPermissionContext
     */
    @RequestMapping(value = "permissions", method = {RequestMethod.POST}, consumes = {
            "application/json", "application/xml"}, produces = {"application/xml", "application/json"})
    public ResponseEntity<AuthPermissionContext> getPermissions(@RequestBody Map<String,Object> map) {

        String appKey = map.containsKey("appKey") ? map.get("appKey").toString() : null;
        String userName = map.containsKey("username") ? map.get("username").toString() : null;
        logger.debug("method:getPermissions");
        logger.debug("client:{},username:{}", appKey, userName);
        AuthPermissionContext permissionContext = new AuthPermissionContext();
        if (!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(userName)) {
            try {
                permissionContext.setRoles(
                        authService.getRoles(userName));
                permissionContext.setPermissions(
                        authService.getPermissions(userName));

            } catch (Exception ex) {
                logger.error("server:getPermissions 获取用户权限异常", ex);
                return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<>(permissionContext, HttpStatus.OK);
    }


    /**
     * 获取应用信息
     *
     * @param map appKey:应用标识
     * @return SECRET
     */
    @RequestMapping(value = "getAppSecret", method = {RequestMethod.POST}, consumes = {
            "application/json", "application/xml"}, produces = {"application/xml", "application/json"})
    public ResponseEntity<Map<String,Object>> getAppSecret(@RequestBody Map<String,Object> map) {
        String appKey = map.containsKey("appKey") ? map.get("appKey").toString() : null;
        logger.debug("method:getAppSecret");
        logger.debug("client:{}", appKey);
        String secret = null;
        Map<String,Object> result = new HashMap<>();
        if (!StringUtils.isEmpty(appKey)) {
            try {
                App app = authService.getAppByCode(appKey);
                if(app != null){
                    secret =  app.getSecret();
                }
            } catch (Exception ex) {
                logger.error("server:getAppSecret 获取APP应用密钥信息异常", ex);
                return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }
        result.put("result", secret);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }



    /**
     * 获取应用信息
     *
     * @param map appKey:应用标识
     * @return SECRET
     */
    @RequestMapping(value = "get1", method = {RequestMethod.POST}, consumes = {
            "application/json", "application/xml"}, produces = {"application/xml", "application/json"})
    public ResponseEntity<Map<String,Object>> get1(@RequestBody Map<String,Object> map) {
        String appKey = map.containsKey("appKey") ? map.get("appKey").toString() : null;

        logger.debug("cm:{}",map.get("cm").toString());

        ServerCM.startRequest(map.get("cm").toString());
        Map<String,Object> result = new HashMap<>();

        try{
            logger.info("get1111");

            result.put("result", appKey);
            ServerCM.exit();

        }catch (Exception ex){
            ServerCM.exit(ex);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    /**
     * 获取应用信息
     *
     * @return SECRET
     */
    @RequestMapping(value = "get2")
    public Map<String,Object>  get1(HttpServletRequest request) {

        ServerCM.startRequest("get2","and",request.getRemoteHost(),String.valueOf(request.getRemotePort()) );
        Map<String,Object> result = new HashMap<>();

        try{
            logger.info("get1111");

            result.put("result", "xxxaa");
            ServerCM.exit();

        }catch (Exception ex){
            ServerCM.exit(ex);
        }

        return result;
    }

}
