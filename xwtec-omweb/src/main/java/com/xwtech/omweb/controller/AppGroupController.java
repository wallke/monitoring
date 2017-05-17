package com.xwtech.omweb.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.xwtech.framework.shiro.Constants;
import com.xwtech.framework.shiro.model.auth.User;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.model.*;
import com.xwtech.omweb.service.IAppGroupService;
import com.xwtech.omweb.service.IAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by wangyu on 2017/2/16.
 */
@RestController
@RequestMapping("omweb/appGroup")
public class AppGroupController {

    private final static Logger logger = LoggerFactory.getLogger(AppGroupController.class);


    @Autowired
    IAppGroupService appGroupService;

    @Autowired
    IAppService appService;

    @RequestMapping("index")
    public ModelAndView index(@RequestParam(name = "ps", defaultValue = "10") int ps,
                              @RequestParam(name = "pn", defaultValue = "1") int pn,
                              String num, String name) {
        PageInfo<AppGroup> pageInfo = new PageInfo<AppGroup>();
        pageInfo.setPageSize(ps < 10 ? ps : 10);
        pageInfo.setPageNum(pn);

        AppGroup group = new AppGroup();
        group.setNum(num);
        group.setName(name);

        List<AppGroup> list = appGroupService.getAppGroups(group, pageInfo);

        ModelAndView modelAndView = new ModelAndView("appGroup/index").
                addObject("appGroup", group).
                addObject("appGroupList", list).
                addObject("pageInfo", ((Page<AppGroup>) list).toPageInfo());
        return modelAndView;
    }


    @RequestMapping(value = "create", method = RequestMethod.GET)
    private ModelAndView create(@RequestParam(name = "ps", defaultValue = "10") int ps,
                                @RequestParam(name = "pn", defaultValue = "1") int pn,
                                String id) {
        PageInfo<AppGroup> pageInfo = new PageInfo<AppGroup>();
        pageInfo.setPageSize(ps < 10 ? ps : 10);
        pageInfo.setPageNum(pn);

        AppGroup group = new AppGroup();
        if (id != null) {
            group = appGroupService.getAppGroupById(id);
        }

        List<App> appList = appService.queryAppListByGroupId(id, pageInfo);
        List<App> otherAppList = appService.queryAppListNotGroup();

        ModelAndView modelAndView = new ModelAndView("appGroup/create")
                .addObject("appGroup", group)
                .addObject("appList", appList)
                .addObject("otherAppList", otherAppList)
                .addObject("pageInfo", ((Page<App>) appList).toPageInfo());
        return modelAndView;
    }


    @RequestMapping(value = "create", method = RequestMethod.POST)
    private JSONResult createSubmit(HttpServletRequest request, String id, String num, String name, int enabled,
                                      String desc, String memo) {
        JSONResult jsonResult = new JSONResult();
        try {
            User user = (User) request.getAttribute(Constants.CURRENT_USER);

            //新增
            if (id == null || id.equals("")) {
                AppGroup group = appGroupService.getAppGroupByNum(num);
                if (group != null) {
                    jsonResult.setFailInfo("应用组编码已存在，无法新增！");
                    return jsonResult;
                }
                group = appGroupService.getAppGroupByName(name);
                if (group != null) {
                    jsonResult.setFailInfo("应用组名称已存在，无法新增！");
                    return jsonResult;
                }

                group = new AppGroup();
                group.setId(UUID.randomUUID().toString().replaceAll("-",""));
                group.setNum(UUID.randomUUID().toString().replaceAll("-",""));
                group.setName(name);
                group.setIsEnabled(enabled);
                group.setDesc(desc);
                group.setMemo(memo);
                group.setCreateManCode(String.valueOf(user.getId()));
                group.setCreateManName(user.getName());
                group.setCreateTime(user.getCreateTime());
                group.setCreateMemo("");
                group.setLastManCode(String.valueOf(user.getId()));
                group.setLastManName(user.getName());
                group.setLastTime(user.getCreateTime());
                group.setLastMemo("");
                appGroupService.createAppGroup(group);
                jsonResult.setSuccessInfo("新增应用组成功！");
            }
            //修改
            else {
                AppGroup paramGroup = new AppGroup();
                paramGroup.setId(id);
                paramGroup.setNum(num);
                paramGroup.setName(name);
                //查询除本身id以外，是否存在相同编码的应用组
                AppGroup group = appGroupService.getAppGroupByNumExceptSelf(paramGroup);
                if (group != null) {
                    jsonResult.setFailInfo("应用组编码已存在，修改失败！");
                    return jsonResult;
                }
                //查询除本身id以外，是否存在相同名称的应用组
                group = appGroupService.getAppGroupByNameExceptSelf(paramGroup);
                if (group != null) {
                    jsonResult.setFailInfo("应用组名称已存在，修改失败！");
                    return jsonResult;
                }

                group = appGroupService.getAppGroupById(id);
                if (group == null) {
                    jsonResult.setFailInfo("应用组不存在，修改失败！");
                    return jsonResult;
                }

                group.setNum(num);
                group.setName(name);
                group.setIsEnabled(enabled);
                group.setDesc(desc);
                group.setMemo(memo);
                group.setLastManCode(String.valueOf(user.getId()));
                group.setLastManName(user.getName());
                group.setLastTime(user.getCreateTime());
                appGroupService.updateAppGroup(group);
                jsonResult.setSuccessInfo("修改应用组成功！");
            }
            return jsonResult;

        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.setFailInfo("系统异常，请稍后再试！");
            return jsonResult;
        }
    }


    @RequestMapping(value = "maintain", method = RequestMethod.GET)
    private ModelAndView maintain(@RequestParam(name = "ps", defaultValue = "10") int ps,
                                  @RequestParam(name = "pn", defaultValue = "1") int pn,
                                  String groupId) {
        PageInfo<App> pageInfo = new PageInfo<App>();
        pageInfo.setPageSize(ps < 10 ? ps : 10);
        pageInfo.setPageNum(pn);

        List<App> appList = appService.queryAppListByGroupId(groupId, pageInfo);
        List<App> otherAppList = appService.queryAppListNotGroup();

        ModelAndView modelAndView = new ModelAndView("appGroup/maintain")
                .addObject("groupId", groupId)
                .addObject("appList", appList)
                .addObject("otherAppList", otherAppList)
                .addObject("pageInfo", ((Page<App>) appList).toPageInfo());
        return modelAndView;
    }


    @RequestMapping(value = "addApp", method = RequestMethod.POST)
    private JSONResult addApp(HttpServletRequest request, String appId, String groupId) {

        JSONResult jsonResult = new JSONResult();
        try {
            Map map = new HashMap();
            map.put("appId", appId);
            map.put("groupId", groupId);
            appGroupService.addAppToGroup(map);
            jsonResult.setSuccessInfo("添加应用成功！");

        } catch (Exception e) {
            jsonResult.setFailInfo("添加应用失败！");
            return jsonResult;
        }

        return jsonResult;
    }


    @RequestMapping(value = "deleteApp", method = RequestMethod.POST)
    private JSONResult deleteApp(HttpServletRequest request, String appId, String groupId) {

        JSONResult jsonResult = new JSONResult();
        try {
            appGroupService.deleteAppFromGroup(appId);
            jsonResult.setSuccessInfo("删除应用成功！");

        } catch (Exception e) {
            jsonResult.setFailInfo("系统异常，请稍后再试！");
            return jsonResult;
        }

        return jsonResult;
    }



    @RequestMapping(value = "delete", method = RequestMethod.POST)
    private JSONResult deleteSubmit(String id) {
        JSONResult jsonResult = new JSONResult();
        try {
            //查看应用组下面是否有应用，如有，不允许删除
            List<App> appList = appService.queryAppListByGroupId(id, null);
            if (appList != null) {
                jsonResult.setFailInfo("应用组下存在应用，不允许删除！");
                return jsonResult;
            }

            appGroupService.deleteAppGroup(id);
            jsonResult.setSuccessInfo("删除应用组成功！");

        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.setFailInfo("系统异常，请稍后再试！");
        }
        return jsonResult;
    }






















    @RequestMapping(value = "app_connector", method = RequestMethod.GET)
    private ModelAndView appConnector(String groupId) {



        ModelAndView modelAndView = new ModelAndView("appGroup/app_connector")
                .addObject("groupId", groupId);
        return modelAndView;
    }



    @RequestMapping(value = "getGroupApps", method = RequestMethod.POST)
    private JSONResult getGroupApps(String groupId) {
        JSONResult jsonResult = new JSONResult();
        try {
            //查看应用组下面是否有应用，如有，不允许删除
            List<AppCanvasPosition> appList = appGroupService.getAppsByGroupId(groupId);
            List<AppCanvasLinks> links =  appGroupService.getAppLinksByGroupId(groupId);
            Map map = new HashMap();
            map.put("locations",appList);
            map.put("connections",links);
            jsonResult.setData(map);

        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.setFailInfo("系统异常，请稍后再试！");
        }
        return jsonResult;
    }



    @RequestMapping(value = "saveGroupAppsCanvas", method = RequestMethod.POST)
    private JSONResult saveGroupAppsCanvas(@RequestBody AppCanvas appCanvas) {
        JSONResult jsonResult = new JSONResult();
        try {

            if(appGroupService.saveAppCanvas(appCanvas)){

            }else{
                jsonResult.setFailInfo("保存失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.setErrorInfo("系统异常，请稍后再试！");
        }
        return jsonResult;
    }




























}
