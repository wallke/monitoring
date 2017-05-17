package com.xwtech.admin.shiro.realm;

import com.xwtech.framework.shiro.model.auth.User;
import com.xwtech.admin.service.IAuthService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * @author zhangq
 * @ClassName: UserRealm
 * @Description: 用户权限登录
 * @date 2016年8月20日 下午2:00:32
 */
public class UserRealm extends AuthorizingRealm {


    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Resource(name = "authService")
    IAuthService authService;


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(
            PrincipalCollection principals) {

        logger.debug("AUTH:UserRealm:doGetAuthorizationInfo");
        logger.debug("AUTH:设置角色/权限");
        String username = (String) principals.getPrimaryPrincipal();

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        logger.debug("AUTH:设置角色");
        authorizationInfo.setRoles(authService.getRoles(username));
        logger.debug("AUTH:设置权限");

        authorizationInfo.setStringPermissions(authService.getPermissions(username));

        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken authcToken) {

        logger.debug("AUTH:UserRealm:doGetAuthenticationInfo");


        SimpleAuthenticationInfo authenticationInfo = null;

        UsernamePasswordCaptchaToken token = (UsernamePasswordCaptchaToken) authcToken;

        //String username = (String) token.getPrincipal();
        String username = token.getUsername();
        // 增加判断验证码逻辑
//        String captcha = token.getCaptcha();
//        Session session = SecurityUtils.getSubject().getSession();
//        String exitCode = (String) session.getAttribute(Constants.CAPTCHA_KEY);
//        if (null == captcha || !captcha.equalsIgnoreCase(exitCode)) {
//            throw new CaptchaException("验证码错误");
//        }
//        session.removeAttribute(Constants.CAPTCHA_KEY);

        if(StringUtils.isEmpty(username)){
            throw new UnknownAccountException();
        }

        // 查找用户
        User user = authService.getUserByLoginName(username);
        //没找到账户
        if (user == null) {
            throw new UnknownAccountException();
        }
        //帐号锁定
        if (user.getLocked()==1) {
            throw new LockedAccountException();
        }
        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配
        authenticationInfo = new SimpleAuthenticationInfo(
                user.getLoginName(), //用户名
                user.getPassword(), //密码
                ByteSource.Util.bytes(user.getCredentialsSalt()),//salt=username+salt
                getName()  //realm name
        );

        return authenticationInfo;
    }

    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

    public void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

    public void clearAllCachedAuthenticationInfo() {
        getAuthenticationCache().clear();
    }

    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }

}
