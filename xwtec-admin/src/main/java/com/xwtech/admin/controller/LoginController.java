package com.xwtech.admin.controller;

import com.xwtech.admin.shiro.captcha.CaptchaException;
import com.xwtech.admin.shiro.filters.ServerFormAuthenticationFilter;
import com.xwtech.admin.shiro.realm.UsernamePasswordCaptchaToken;
import com.xwtech.framework.shiro.Constants;
import com.xwtech.framework.shiro.SubjectSessionUtil;
import com.xwtech.framework.shiro.model.auth.Menu;
import com.xwtech.framework.shiro.model.auth.Platform;
import com.xwtech.framework.shiro.model.auth.User;
import com.xwtech.admin.service.IAuthService;
import com.xwtech.admin.utils.CaptchaUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import xwtec.servercm.ServerCM;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Set;

/**
 * 用户登录/注销/验证码
 * Created by zq on 16/8/15.
 */
@Controller
public class LoginController {

    final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    IAuthService authService;


    @Resource
    RedisTemplate redisTemplate;

    /**
     * 登录页面
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/login")
    public String loginForm(Model model,HttpServletRequest request) {
        model.addAttribute("user", new User());
        return "login";
    }


    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello(Model model) {


        authService.getRoles("admin");
        model.addAttribute("hello", "测试hello");
        return "hello";
    }


    /**
     * 用户退出
     *
     * @return
     */
//    @RequestMapping(value = "/login", method = RequestMethod.POST)
//    public String login(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            UsernamePasswordCaptchaToken usernamePasswordCaptchaToken) {
//
//    //    try {
//            Subject subject = SecurityUtils.getSubject();
//            subject.login(usernamePasswordCaptchaToken);
//
////            String username = usernamePasswordCaptchaToken.getUsername();
////            //token
////            logger.info("保存用户信息到SHIRO会话...");
////            User user = authService.getUserByLoginName(username);
////
////            Set<String> roles = authService.getRoles(username);
////            user.setRoles(roles);
////            Set<String> permissions = authService.getPermissions(username);
////            user.setPermissions(permissions);
////
////            redisTemplate.opsForValue().set(
////                    SubjectSessionUtil.USER_SESSION_ATTRIBUTE_KEY + "_" + username,
////                    user);
////
////            logger.info("保存用户菜单信息到SHIRO会话...");
////            List<Menu> resources
////                    = authService.getResourcesByUserId(user.getId());
////
////
////            redisTemplate.opsForValue().set(
////                    SubjectSessionUtil.MENU_SESSION_ATTRIBUTE_KEY + "_" + username,
////                    resources);
////
////            logger.info("保存所有APP信息到SHIRO会话...");
////            redisTemplate.opsForValue().set(
////                    SubjectSessionUtil.APP_SESSION_ATTRIBUTE_KEY,
////                    authService.getApps());
////
////            logger.info("保存平台信息到SHIRO会话...");
////            List<Platform> platforms = authService.getPlatforms(1);
////            redisTemplate.opsForValue().set(
////                    SubjectSessionUtil.PLATFORM_SESSION_ATTRIBUTE_KEY,
////                    platforms);
////
////            String currentPlatformIdKey = SubjectSessionUtil.CURRENT_PLATFORM_ID_SESSION_ATTRIBUTE_KEY + "_"+ username;
////            //缓存中 platformKey
////            Object object = redisTemplate.opsForValue ().get(currentPlatformIdKey);
////            String successUrl = null;
////            if(object != null){
////                for (Platform platform : platforms) {
////                    if(platform.getCode().toString().equals(object)){
////                        successUrl = platform.getUrl();
////                    }
////                }
////            }
////            if(successUrl != null){
////                setSuccessUrl(successUrl);
////            }
////            ServerCM.exit();
////            return onLoginSuccess(token, subject, request, response);
//
////        }catch (CaptchaException ce) {
////            logger.info("对用户[" + token.getPrincipal() + "]进行登录验证..验证码未通过");
////            request.setAttribute("message","验证码错误");
////            ServerCM.exit(ce);
////            return onLoginFailure(token, ce, request, response);
////        } catch (UnknownAccountException uae) {
////            logger.info("对用户[" + token.getPrincipal() + "]进行登录验证..验证未通过,未知账户");
////            request.setAttribute("message","未知账户");
////            ServerCM.exit(uae);
////            return onLoginFailure(token, uae, request, response);
////        } catch (IncorrectCredentialsException ice) {
////            logger.info("对用户[" + token.getPrincipal() + "]进行登录验证..验证未通过,错误的凭证");
////            request.setAttribute("message","用户名或密码错误");
////            ServerCM.exit(ice);
////            return onLoginFailure(token, ice, request, response);
////        } catch (LockedAccountException lae) {
////            logger.info("对用户[" + token.getPrincipal() + "]进行登录验证..验证未通过,账户已锁定");
////            request.setAttribute("message","账户已锁定");
////            ServerCM.exit(lae);
////            return onLoginFailure(token, lae, request, response);
////        } catch (ExcessiveAttemptsException eae) {
////            logger.info("对用户[" + token.getPrincipal() + "]进行登录验证..验证未通过,错误次数过多");
////            request.setAttribute("message","用户名或密码错误次数过多");
////            ServerCM.exit(eae);
////            return onLoginFailure(token, eae, request, response);
////        } catch (AuthenticationException ae) {
////            //通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景
////            logger.info("对用户[" + token.getPrincipal() + "]进行登录验证..验证未通过,堆栈轨迹如下");
////            request.setAttribute("message","用户名或密码不正确");
////            ServerCM.exit(ae);
////            return onLoginFailure(token, ae, request, response);
////        }
//
//
//        return null;
//    }


    /**
     * 验证码 - 存储于shiro session
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    public void captcha(HttpServletRequest request, HttpServletResponse response) {
        // 设置相应类型,告诉浏览器输出的内容为图片
        response.setContentType("image/jpeg");
        // 不缓存此内容
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expire", 0);
        try {
            CaptchaUtil tool = new CaptchaUtil();
            StringBuffer code = new StringBuffer();
            BufferedImage image = tool.genRandomCodeImage(code);
            //Session 不存在默认创建新的Session
            Session session = SecurityUtils.getSubject().getSession();
            session.setAttribute(Constants.CAPTCHA_KEY, code.toString());
            // 将内存中的图片通过流动形式输出到客户端
            ImageIO.write(image, "JPEG", response.getOutputStream());

        } catch (Exception e) {
            logger.error("生成验证码异常", e);
        }

    }


}
