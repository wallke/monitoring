package com.xwtech.admin.shiro.filters;

import com.xwtech.admin.service.IAuthService;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 
* @ClassName: SysUserFilter 
* @Description:
* @author zhangq
* @date 2015年3月20日 下午2:10:23 
*
 * */
public class SysUserFilter extends UserFilter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String appKey;

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    @Resource(name = "authService")
    IAuthService authService;


    @Override
    public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue)
            throws Exception {


        return super.onPreHandle(request,response,mappedValue);

    }





}
