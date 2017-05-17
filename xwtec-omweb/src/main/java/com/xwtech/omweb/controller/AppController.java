package com.xwtech.omweb.controller;

import com.github.pagehelper.PageInfo;
import com.xwtech.es.model.ChainBean;
import com.xwtech.es.service.ApplicationService;
import com.xwtech.framework.shiro.Constants;
import com.xwtech.framework.shiro.model.auth.User;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.model.*;
import com.xwtech.omweb.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.github.pagehelper.Page;

import java.util.*;

/**
 * Created by Administrator on 2017/2/14 0014.
 */
@Controller
@RequestMapping("omweb/app")
public class AppController {
    private final static Logger logger = LoggerFactory.getLogger(AppController.class);

    //应用
    @Resource(name = "appService")
    private IAppService appService;
    //主机
    @Resource(name = "serverService")
    private IServerService serverService;
    //公司
    @Resource(name = "cropService")
    private ICropService cropService;
    //应用类别
    @Resource(name = "appCategoryService")
    private IAppCategoryService appCategoryService;
    //节点
    @Autowired
    private INodeService nodeService;

    @Autowired
    IAppGroupService appGroupService;

    @Autowired
    ApplicationService applicationService;


    @RequestMapping(value = "index", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView index(HttpServletRequest request, String appNum, String appName,
                              String categoryId, String cropId,
                              @RequestParam(name = "ps", defaultValue = "10") int ps,
                              @RequestParam(name = "pn", defaultValue = "1") int pn) {

        PageInfo<App> pageInfo = new PageInfo<App>();
        pageInfo.setPageSize(ps < 10 ? ps : 10);
        pageInfo.setPageNum(pn);

        //所有应用类别
        List<AppCategory> appCategoriesList = appCategoryService.getCategoryList();
        //所有公司
        List<Crop> cropList = cropService.getCorpList();
        //应用列表
        List<App> appList = appService.queryAppList(appNum, appName, categoryId, cropId, pageInfo);

        return new ModelAndView("app/index").addObject("appList", appList).addObject("appNum", appNum)
                .addObject("appCategoriesList", appCategoriesList).addObject("cropList", cropList)
                .addObject("appName", appName).addObject("categoryId", categoryId).addObject("cropId", cropId)
                .addObject("pageInfo", ((Page<App>) appList).toPageInfo());

    }

    /**
     * 跳转应用新增修改页面
     *
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ModelAndView create(String appId) {
        String actionType = "create";
        App app = new App();
        //所有应用类别
        List<AppCategory> appCategoriesList = appCategoryService.getCategoryList();
        //所有公司
        List<Crop> cropList = cropService.getCorpList();
        List<Node> nodeList = new ArrayList<Node>();
        if (StringUtils.isNotEmpty(appId)) {
            actionType = "modfiy";
            //根据id查看app应用信息
            app = appService.queryAppDeatil(appId);
            //根据应用ID查询所有节点
            nodeList = nodeService.getNodesByAppId(appId);
        }
        return new ModelAndView("app/create").addObject("actionType", actionType).addObject("app", app)
                .addObject("nodeList", nodeList)
                .addObject("appCategoriesList", appCategoriesList).addObject("cropList", cropList);
    }

    /**
     * 新增主机信息或修改主机信息
     *
     * @param request
     * @param app
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public JSONResult createSubmit(HttpServletRequest request, @ModelAttribute App app) {
        String actionType = request.getParameter("actionType");
        JSONResult jsonResult = new JSONResult();
        User user = (User) request.getAttribute(Constants.CURRENT_USER);
        if (user != null) {
            app.setCreateManCode(String.valueOf(user.getId()));
            app.setCreateManName(user.getLoginName());
            app.setLastManCode(String.valueOf(user.getId()));
            app.setLastManName(user.getLoginName());
            app.setCreateMemo("");
            app.setLastMemo("");
        } else {
            jsonResult.setFailInfo("用户信息未获取到,请稍后再试");
            return jsonResult;
        }
        int count = appService.queryAppNum(app.getAppNum(),StringUtils.isNoneEmpty(app.getAppId())?app.getAppId():null );
        if (count > 0) {
            jsonResult.setFailInfo("应用编码已经存在,请修正");
            return jsonResult;
        }
        count = appService.queryAppName(app.getAppName(), StringUtils.isNoneEmpty(app.getAppId())?app.getAppId():null);
        if (count > 0) {
            jsonResult.setFailInfo("应用名称已经存在,请修正");
            return jsonResult;
        }
        try {
            if ("create".equals(actionType)) {
                //新增
                count = appService.addApp(app);
                if (count > 0) {
                    jsonResult.setSuccessInfo("新增应用成功！");
                } else {
                    jsonResult.setFailInfo("新增应用失败，请稍后再试！");
                }
            } else if ("modfiy".equals(actionType)) {
                //修改
                count = appService.updateApp(app);
                if (count > 0) {
                    jsonResult.setSuccessInfo("修改应用成功！");
                } else {
                    jsonResult.setFailInfo("修改应用失败，请稍后再试！");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.setFailInfo("系统异常，请稍后再试！");
        }

        return jsonResult;
    }

    @RequestMapping(value = "appDetail", method = RequestMethod.GET)
    public ModelAndView appDetail(String appId) {
        App app = appService.queryAppDeatil(appId);
        //根据应用ID查询所有节点
        List<Node> nodeList = nodeService.getNodesByAppId(appId);
        return new ModelAndView("app/detail").addObject("app",app).addObject("nodeList",nodeList);

    }

    /**
     * 根据应用ID删除应用
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "deleteApp", method = RequestMethod.POST)
    @ResponseBody
    public JSONResult deleteServer(HttpServletRequest request) {
        JSONResult jsonResult = new JSONResult();
        String appId = request.getParameter("appId");
        if (appId !=null && !appId.equals(""))
        {
            List<Node> nodes = nodeService.getNodesByAppId(appId);
            if (nodes !=null && nodes.size() >0)
            {
                jsonResult.setFailInfo("该应用下面存在节点,删除应用失败！");
                return jsonResult;
            }
            try {
                int count = appService.deleteAppById(appId);
                if (count > 0) {
                    jsonResult.setSuccessInfo("删除应用成功！");
                } else {
                    jsonResult.setFailInfo("删除应用失败，请稍后再试！");
                }
            } catch (Exception e) {
                e.printStackTrace();
                jsonResult.setFailInfo("删除应用异常，请稍后再试！");
            }
        } else {
            jsonResult.setFailInfo("删除应用参数异常，请稍后再试！");
        }

        return jsonResult;
    }




}
