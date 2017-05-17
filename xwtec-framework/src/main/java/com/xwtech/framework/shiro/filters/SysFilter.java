package com.xwtech.framework.shiro.filters;

import com.xwtech.framework.shiro.Constants;
import com.xwtech.framework.shiro.SubjectSessionUtil;
import com.xwtech.framework.shiro.model.auth.App;
import com.xwtech.framework.shiro.model.auth.Menu;
import com.xwtech.framework.shiro.model.auth.Platform;
import com.xwtech.framework.shiro.properties.ClientProperties;
import com.xwtech.framework.shiro.util.ShiroAjaxAuthUtil;
import com.xwtech.framework.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zhangq on 2017/1/11.
 */
@WebFilter(filterName = "sysFilter", urlPatterns = "/*")
public class SysFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(SysFilter.class);


    @Autowired
    ClientProperties clientProperties;


    @Resource
    RedisTemplate redisTemplate;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String path = httpServletRequest.getRequestURI();

        //静态资源
        if (path.startsWith("/static")) {
            chain.doFilter(request, response);
            return;
        }

        logger.debug("加载用户信息到ServletRequest...");
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        //用户信息不存在
        if(StringUtils.isEmpty(username)){
            chain.doFilter(request, response);
            return;
        }

        // com.xwtech.admin.shiro.filters.ServerFormAuthenticationFilter 登录成功设置用户页面数据
        Object user = redisTemplate.opsForValue ().get(SubjectSessionUtil.USER_SESSION_ATTRIBUTE_KEY + "_" + username);
        logger.debug("SysUserFilter: 添加当前用户到request.setAttribute");
        request.setAttribute(Constants.CURRENT_USER, user);

        //ajax json请求
        if (ShiroAjaxAuthUtil.isAjax(request)) {
            chain.doFilter(request, response);
            return;
        }

        logger.debug("加载平台列表信息到ServletRequest...");
        List<Platform> platforms = (List<Platform>) redisTemplate.opsForValue ().get(SubjectSessionUtil.PLATFORM_SESSION_ATTRIBUTE_KEY);

        logger.debug("初始化平台信息ServletRequest...");
        String platformKey = clientProperties.getPlatformKey();
        String currentPlatformIdKey = SubjectSessionUtil.CURRENT_PLATFORM_ID_SESSION_ATTRIBUTE_KEY + "_"+ username;

        if (StringUtils.isNotEmpty(platformKey)) {
            if (platformKey.startsWith("default-")) {
                Object object = redisTemplate.opsForValue ().get(currentPlatformIdKey);
                if (object == null || "".equals(object)) {
                    redisTemplate.opsForValue ().set(currentPlatformIdKey, platformKey.replace("default-", ""));
                }
            } else {
                redisTemplate.opsForValue ().set(currentPlatformIdKey, platformKey);
            }
        }
        //缓存中 platformKey
        Object object = redisTemplate.opsForValue ().get(currentPlatformIdKey);

        logger.debug("平台code：{}", object);
        long currentPlatformId = 0;
        if (platforms != null) {
            for (Platform platform : platforms) {
                platform.setCurrent(platform.getCode().toString().equals(object));
                if (platform.isCurrent()) {
                    currentPlatformId = platform.getId();
                    //设置当前平台ID
                    request.setAttribute(Constants.SERVER_CURRENT_PLATFORM_ID, currentPlatformId);
                    //设置当前平台地址为首页
                    request.setAttribute(Constants.SERVER_CURRENT_PLATFORM_URL, platform.getUrl());
                }
            }
            request.setAttribute(Constants.SERVER_PLATFORMS_KEY, platforms);
        }

        List<App> apps = (List<App>) redisTemplate.opsForValue ().get(SubjectSessionUtil.APP_SESSION_ATTRIBUTE_KEY);
        String currentPlatformAppIds = "";
        if (apps != null) {
            for (App app : apps) {
                if (app.getPlatformId() == currentPlatformId) {
                    currentPlatformAppIds += app.getId() + ",";
                }

                if (app.getCode().equals(clientProperties.getAppKey())) {
                    request.setAttribute(Constants.SERVER_CURRENT_APP_URL, app.getUrl());
                }
            }
        }
        //logger.debug("加载平台子系统列表信息到ServletRequest...");
//        long currentAppId = 0;
//        if (apps != null) {
//            for (App app : apps) {
//                app.setCurrent(servletContext.getAttribute("appKey").equals(app.getCode()));
//                if (app.isCurrent()) {
//                    currentAppId = app.getId();
//                    request.setAttribute(Constants.SERVER_CURRENT_APP_ID, currentAppId);
//                }
//            }
//            request.setAttribute(Constants.SERVER_APPS_KEY, apps);
//        }


        logger.debug("初始化平台菜单信息ServletRequest...");
        String mid = httpServletRequest.getParameter("mid");
        if ("/".equals(path)) {
            mid = "0";
        }
        if (StringUtils.isNotEmpty(mid)) {
            CookieUtils.setCookie(httpServletResponse, Constants.SERVER_CURRENT_MENU_COOKIE_ID, mid, "/");
        } else {
            mid = CookieUtils.getCookie(httpServletRequest, Constants.SERVER_CURRENT_MENU_COOKIE_ID);
        }

        logger.info("当前请求菜单cookieid:{}", mid);
        //加载用户菜单
        List<Menu> resourceList = (List<Menu>) redisTemplate.opsForValue ().get(SubjectSessionUtil.MENU_SESSION_ATTRIBUTE_KEY + "_" + username);
        if (resourceList != null) {

            List<Menu> currentAppMenuList = new ArrayList<>();

            for (Menu menu : resourceList) {

                if (menu.getParentId() == 0 && currentPlatformAppIds.indexOf(menu.getAppId().toString()) > -1 && menu.getType().equals(Menu.ResourceType.menu)) {

                    menu.setCurrent(StringUtils.isNotEmpty(mid) && mid.equals(String.valueOf(menu.getId())));

                    menu.setUrl(filterApp(apps, menu.getAppId()) + menu.getUrl());

                    List<Menu> subs = resourceList.stream().filter(x -> x.getParentId() == menu.getId() && x.getType().equals(Menu.ResourceType.menu)).collect(Collectors.toList());
                    if (subs != null) {
                        for (Menu menu1 : subs) {
                            menu1.setUrl(filterApp(apps, menu1.getAppId()) + menu1.getUrl());
                            if (StringUtils.isNotEmpty(mid) && mid.equals(String.valueOf(menu1.getId()))) {
                                menu1.setCurrent(true);
                                menu.setCurrent(true);
                            }

                            List<Menu> subs1 = resourceList
                                    .stream()
                                    .filter(x -> x.getParentId() == menu1.getId() && x.getType().equals(Menu.ResourceType.menu)).collect(Collectors.toList());
                            if (subs1 != null) {

                                for (Menu menu2 : subs1) {
                                    menu2.setUrl(filterApp(apps, menu2.getAppId()) + menu2.getUrl());
                                    if (StringUtils.isNotEmpty(mid) && mid.equals(String.valueOf(menu2.getId()))) {
                                        menu2.setCurrent(true);
                                        menu1.setCurrent(true);
                                        menu.setCurrent(true);
                                    }
                                }
                                menu1.setSubMenus(subs1);//三级菜单
                            }
                        }
                        menu.setSubMenus(subs);//二级菜单
                    }
                    currentAppMenuList.add(menu);//一级菜单
                }

            }
            request.setAttribute(Constants.SERVER_MENUS_KEY, currentAppMenuList);
        }

        chain.doFilter(request, response);

    }

    String filterApp(List<App> apps, long appId) {

        if (apps != null) {

            List<App> list = apps.stream().filter(e -> e.getId() == appId).collect(Collectors.toList());
            if (list != null && list.size() == 1) {
                return list.get(0).getUrl();
            }

        }
        return "";

    }

    @Override
    public void destroy() {

    }
}
