package com.xwtech.framework.sign.filters;

import com.xwtech.framework.sign.properties.SignProperties;
import com.xwtech.framework.sign.remote.IAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.text.MessageFormat;

/**
 * Created by zq on 16/8/4.
 */
public class SignFilter implements Filter {


    final Logger logger = LoggerFactory.getLogger(SignFilter.class);

    @Autowired
    IAppService appService;

    @Autowired
    SignProperties signProperties;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        String requestUri = httpServletRequest.getRequestURI();
        if(matches(requestUri)){
            logger.debug("signFilter 匹配签名认证路径,需签名校验:{}",requestUri);
            String appCode = httpServletRequest.getHeader("AppCode");

            String signature = httpServletRequest.getHeader("Sign");

            String sessionId = httpServletRequest.getHeader("sessionId");

            if (StringUtils.isEmpty(appCode) || StringUtils.isEmpty(signature)) {
                logger.warn("Unauthorized. 认证参数缺失.");
                unauthorized(httpServletRequest,(HttpServletResponse)response);
                return;
            } else {
                String secret = appService.getSecret(appCode);
                if(StringUtils.isEmpty(secret) ){
                    logger.warn("Unauthorized. 认证应用不合法.");
                    unauthorized(httpServletRequest,(HttpServletResponse)response);
                    return;
                }else{
                    RequestWrapper requestWrapper = new RequestWrapper(httpServletRequest);
                    String body = requestWrapper.getBody();
                    logger.debug("请求:{}{}body:{}",
                            httpServletRequest.getRequestURI(),
                            System.lineSeparator(),
                            body);
                    String temp = MessageFormat.format("{0}{1}{2}",
                            secret, body, secret);
                    logger.debug(temp);
                    //消息体签名
                    byte[] sha1Digest = getSHA1Digest(temp);
                    String sign = byte2hex(sha1Digest);
                    logger.debug("客户端sign:{}", signature);
                    logger.debug("服务端sign:{}", sign);
                    if(signature.equals(sign)){
                        logger.debug("Authorized. 签名校验通过.");
                        chain.doFilter(requestWrapper, response);
                    }else{
                        logger.warn("Unauthorized. 签名不匹配.");
                        unauthorized(httpServletRequest,(HttpServletResponse)response);
                        return;
                    }
                }
            }
        }else{
            logger.debug("signFilter 不匹配签名认证路径,不需签名校验:{}",requestUri);
            chain.doFilter(request,response);
        }


    }

    @Override
    public void destroy() {

    }


    /**
     * 签名认证不通过返回401
     * @param httpServletRequest
     * @param httpServletResponse
     * @throws IOException
     */
    void unauthorized( HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse) throws IOException {
        logger.warn("Unauthorized. 请求:{} ", httpServletRequest.getRequestURI());
        httpServletResponse.setStatus(401);
        httpServletResponse.getWriter().write("Unauthorized");
        httpServletResponse.getWriter().flush();
    }


    private static byte[] getSHA1Digest(String data) throws IOException {
        byte[] bytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            bytes = md.digest(data.getBytes("UTF-8"));
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse.getMessage());
        }
        return bytes;
    }


    /**
     * 二进制转十六进制字符串
     *
     * @param bytes
     * @return
     */
    private static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }



    /**
     * Returns {@code true} if the interceptor applies to the given request path.
     * @param lookupPath the current request path
     */
    public boolean matches(String lookupPath) {
        PathMatcher pathMatcherToUse = new AntPathMatcher();
        if (signProperties.getExcludePatterns() != null) {
            for (String pattern : signProperties.getExcludePatterns()) {
                if (pathMatcherToUse.match(pattern, lookupPath)) {
                    return false;
                }
            }
        }
        if (signProperties.getIncludePatterns() == null) {
            return true;
        }
        else {
            for (String pattern : signProperties.getIncludePatterns()) {
                if (pathMatcherToUse.match(pattern, lookupPath)) {
                    return true;
                }
            }
            return false;
        }
       // return true;
    }

}
