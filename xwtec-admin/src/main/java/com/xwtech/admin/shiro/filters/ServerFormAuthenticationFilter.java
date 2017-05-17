package com.xwtech.admin.shiro.filters;

import com.xwtech.admin.service.IAuthService;
import com.xwtech.admin.shiro.captcha.CaptchaException;
import com.xwtech.admin.shiro.realm.UsernamePasswordCaptchaToken;
import com.xwtech.framework.shiro.SubjectSessionUtil;
import com.xwtech.framework.shiro.model.auth.Menu;
import com.xwtech.framework.shiro.model.auth.Platform;
import com.xwtech.framework.shiro.model.auth.User;
import com.xwtech.framework.shiro.util.ShiroAjaxAuthUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import xwtec.servercm.ServerCM;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author zhangq
 * @ClassName: ServerFormAuthenticationFilter
 * @Description:
 * @date 2016年8月20日 下午2:10:18
 */
public class ServerFormAuthenticationFilter extends FormAuthenticationFilter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Resource(name = "authService")
    IAuthService authService;


    @Resource
    RedisTemplate redisTemplate;


    /**
     * 分布式登录地址 鉴权中心完整登录地址  来源yml配置文件
     */
    private String login;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        ServerCM.startRequest("loginForm", "IOS", request.getRemoteHost(), String.valueOf(request.getRemotePort()));

        AuthenticationToken token = createToken(request, response);
        if (token == null) {
            String msg = "createToken method implementation returned null. A valid non-null AuthenticationToken " +
                    "must be created in order to execute mysql login attempt.";
            throw new IllegalStateException(msg);
        }

        try {
            Subject subject = getSubject(request, response);
            subject.login(token);
            String username = (String) subject.getPrincipal();
            //token
            logger.info("保存用户信息到SHIRO会话...");
            User user = authService.getUserByLoginName(username);

            Set<String> roles = authService.getRoles(username);
            user.setRoles(roles);
            Set<String> permissions = authService.getPermissions(username);
            user.setPermissions(permissions);

            redisTemplate.opsForValue().set(
                    SubjectSessionUtil.USER_SESSION_ATTRIBUTE_KEY + "_" + username,
                    user);

            logger.info("保存用户菜单信息到SHIRO会话...");
            List<Menu> resources
                    = authService.getResourcesByUserId(user.getId());


            redisTemplate.opsForValue().set(
                    SubjectSessionUtil.MENU_SESSION_ATTRIBUTE_KEY + "_" + username,
                    resources);

            logger.info("保存所有APP信息到SHIRO会话...");
            redisTemplate.opsForValue().set(
                    SubjectSessionUtil.APP_SESSION_ATTRIBUTE_KEY,
                    authService.getApps());

            logger.info("保存平台信息到SHIRO会话...");
            List<Platform> platforms = authService.getPlatforms(1);
            redisTemplate.opsForValue().set(
                    SubjectSessionUtil.PLATFORM_SESSION_ATTRIBUTE_KEY,
                    platforms);

            String currentPlatformIdKey = SubjectSessionUtil.CURRENT_PLATFORM_ID_SESSION_ATTRIBUTE_KEY + "_" + username;
            //缓存中 platformKey
            Object object = redisTemplate.opsForValue().get(currentPlatformIdKey);
            String successUrl = null;
            if (object != null) {
                for (Platform platform : platforms) {
                    if (platform.getCode().toString().equals(object)) {
                        successUrl = platform.getUrl();
                    }
                }
            }
            if (successUrl != null) {
                setSuccessUrl(successUrl);
            }
            ServerCM.exit();
            return onLoginSuccess(token, subject, request, response);

        } catch (CaptchaException ce) {
            logger.info("对用户[" + token.getPrincipal() + "]进行登录验证..验证码未通过");
            request.setAttribute("message", "验证码错误");
            ServerCM.exit(ce);
            return onLoginFailure(token, ce, request, response);
        } catch (UnknownAccountException uae) {
            logger.info("对用户[" + token.getPrincipal() + "]进行登录验证..验证未通过,未知账户");
            request.setAttribute("message", "未知账户");
            ServerCM.exit(uae);
            return onLoginFailure(token, uae, request, response);
        } catch (IncorrectCredentialsException ice) {
            logger.info("对用户[" + token.getPrincipal() + "]进行登录验证..验证未通过,错误的凭证");
            request.setAttribute("message", "用户名或密码错误");
            ServerCM.exit(ice);
            return onLoginFailure(token, ice, request, response);
        } catch (LockedAccountException lae) {
            logger.info("对用户[" + token.getPrincipal() + "]进行登录验证..验证未通过,账户已锁定");
            request.setAttribute("message", "账户已锁定");
            ServerCM.exit(lae);
            return onLoginFailure(token, lae, request, response);
        } catch (ExcessiveAttemptsException eae) {
            logger.info("对用户[" + token.getPrincipal() + "]进行登录验证..验证未通过,错误次数过多");
            request.setAttribute("message", "用户名或密码错误次数过多");
            ServerCM.exit(eae);
            return onLoginFailure(token, eae, request, response);
        } catch (AuthenticationException ae) {
            //通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景
            logger.info("对用户[" + token.getPrincipal() + "]进行登录验证..验证未通过,堆栈轨迹如下");
            request.setAttribute("message", "用户名或密码不正确");
            ServerCM.exit(ae);
            return onLoginFailure(token, ae, request, response);
        }


    }


    @Override
    protected void issueSuccessRedirect(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        Session session = subject.getSession();
        // clear session if session id is not null and in URL
        if (session.getId() != null) {
//            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE,ShiroHttpServletRequest.COOKIE_SESSION_ID_SOURCE);
//            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, session.getId());
//            //automatically mark it valid here.  If it is invalid, the
//            //onUnknownSession method below will be invoked and we'll remove the attribute at that time.
//            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            String fallbackUrl = (String) session.getAttribute("authc.fallbackUrl");
            if (!StringUtils.isEmpty(fallbackUrl) && !fallbackUrl.contains("login")) {
                setSuccessUrl(fallbackUrl);
            }
        }
        logger.debug("登录成功跳转:{}", getSuccessUrl());
        WebUtils.issueRedirect(request, response, getSuccessUrl(), null, false);
        //super.issueSuccessRedirect(request, response);
    }


    public static final String DEFAULT_CAPTCHA_PARAM = "captcha";

    private String captchaParam = DEFAULT_CAPTCHA_PARAM;

    public String getCaptchaParam() {

        return captchaParam;

    }

    public void setCaptchaParam(String captchaParam) {
        this.captchaParam = captchaParam;
    }

    protected String getCaptcha(ServletRequest request) {

        return WebUtils.getCleanParam(request, getCaptchaParam());

    }


    @Override
    protected AuthenticationToken createToken(
            ServletRequest request, ServletResponse response) {

        String username = getUsername(request);

        String password = getPassword(request);

        String captcha = getCaptcha(request);

        boolean rememberMe = isRememberMe(request);

        String host = getHost(request);

        return new UsernamePasswordCaptchaToken(username,
                password, rememberMe, host, captcha);

    }

//    @Override
//    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
//        if (isLoginRequest(request, response)) {
//            if (isLoginSubmission(request, response)) {
//                if (logger.isTraceEnabled()) {
//                    logger.trace("Login submission detected.  Attempting to execute login.");
//                }
//                return executeLogin(request, response);
//            } else {
//                if (logger.isTraceEnabled()) {
//                    logger.trace("Login page view.");
//                }
//                //allow them to see the login page ;)
//                return true;
//            }
//        } else {
//            if (logger.isTraceEnabled()) {
//                logger.trace("Attempting to access a path which requires authentication.  Forwarding to the " +
//                        "Authentication url [" + getLoginUrl() + "]");
//            }
//
//            saveRequest(request);
//            WebUtils.issueRedirect(request, response, login, null, false);
//
//            return false;
//        }
//    }

    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        logger.debug("跳转到登录页面...{}", login);
        //json请求无会话 返回
        if(ShiroAjaxAuthUtil.isAjax(request)){
            ShiroAjaxAuthUtil.out(response,getLogin());
        }else{
            WebUtils.issueRedirect(request, response, login, null, false);

        }
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        boolean isAccessAllowed = super.isAccessAllowed(request, response, mappedValue);

        //当前访问登录提交地址时，已经拥有有效会话信息，返回false强制进行身份验证
        if (isAccessAllowed && isLoginRequest(request, response) && isLoginSubmission(request, response)) {
            isAccessAllowed = false;
        }
        return isAccessAllowed;
    }




}
