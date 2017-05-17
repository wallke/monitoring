package com.xwtech.framework.shiro.client;

import com.xwtech.framework.shiro.model.ClientSavedRequest;
import com.xwtech.framework.shiro.util.ShiroAjaxAuthUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by zq on 16/8/15.
 */
public class ClientAuthenticationFilter extends AuthenticationFilter {


    @Override
    protected boolean isAccessAllowed(
            ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject subject = getSubject(request, response);
        boolean isAccessAllowed = subject.isAuthenticated();

        return isAccessAllowed;
    }

    @Override
    protected boolean onAccessDenied(
            ServletRequest request, ServletResponse response) throws Exception {
        String backUrl = request.getParameter("backUrl");
        saveRequest(request, backUrl, getDefaultBackUrl(WebUtils.toHttp(request)));
        redirectToLogin(request, response);
        return false;
    }


    /**
     * 保存登录前访问页面信息
     * @param request
     * @param backUrl
     * @param fallbackUrl
     */
    protected void saveRequest(ServletRequest request, String backUrl, String fallbackUrl) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        session.setAttribute("authc.fallbackUrl", fallbackUrl);
        SavedRequest savedRequest = new ClientSavedRequest(httpRequest, backUrl);
        session.setAttribute(WebUtils.SAVED_REQUEST_KEY, savedRequest);
    }


    /**
     * 设置登录成功后跳转地址
     * @param request
     * @return
     */
    private String getDefaultBackUrl(HttpServletRequest request) {
//        String scheme = request.getScheme();
//        String domain = request.getServerName();
//        int port = request.getServerPort();
//        String contextPath = request.getContextPath();
//        StringBuilder backUrl = new StringBuilder();
//        backUrl.append("://");
//        backUrl.append(domain);
//        if ("http".equalsIgnoreCase(scheme) && port != 80) {
//            backUrl.append(":").append(String.valueOf(port));
//        } else if ("https".equalsIgnoreCase(scheme) && port != 443) {
//            backUrl.append(":").append(String.valueOf(port));
//        }
//        backUrl.append(contextPath);
//        backUrl.append(getSuccessUrl());
        return getSuccessUrl();
    }

    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        //json请求无会话 返回
        if (ShiroAjaxAuthUtil.isAjax(request)) {
            ShiroAjaxAuthUtil.out(response, getLoginUrl());
        }else{
            super.redirectToLogin(request, response);
        }
    }
}
