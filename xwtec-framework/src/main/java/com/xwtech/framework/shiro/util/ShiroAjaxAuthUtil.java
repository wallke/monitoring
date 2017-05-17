package com.xwtech.framework.shiro.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xwtech.framework.web.result.JSONResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by zhangq on 2017/3/27.
 */
public class ShiroAjaxAuthUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(ShiroAjaxAuthUtil.class);

    /**
     * 是否是Ajax请求
     * @param request
     * @return
     */
    public static   boolean isAjax(ServletRequest request){
        HttpServletRequest httpServletRequest =  ((HttpServletRequest) request);
        String accept  = httpServletRequest.getHeader("Accept");
        String xRequestedWith = httpServletRequest.getHeader("X-Requested-With");
        if("XMLHttpRequest".equalsIgnoreCase(xRequestedWith) || accept.contains("application/json")){
            LOGGER.debug("当前请求为Ajax请求,X-Requested-With：{},接收类型：{}",xRequestedWith,accept);
            return Boolean.TRUE;
        }
        LOGGER.debug("当前请求非Ajax请求");
        return Boolean.FALSE;
    }


    /**
     *  未授权的ajax请求，返回json串
     * @param response
     * @throws IOException
     */
   public static void out(ServletResponse response,String loginUrl){
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");//设置编码
            response.setContentType("application/json");//设置返回类型
            out = response.getWriter();
            JSONResult jsonResult = new JSONResult();
            jsonResult.setData(loginUrl);
            jsonResult.setErrorInfo("当前会话已失效,跳转重新登录...","unauthorized");
            out.println(new ObjectMapper().writeValueAsString(jsonResult));//输出
        } catch (Exception e) {
            LOGGER.error("ajax 请求。",e);
        }finally{
            if(null != out){
                out.flush();
                out.close();
            }
        }
    }


}
