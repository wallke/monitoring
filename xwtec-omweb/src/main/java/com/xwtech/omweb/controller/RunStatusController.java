package com.xwtech.omweb.controller;

import com.xwtech.es.DateFormatUtil;
import com.xwtech.es.model.DateEnum;
import com.xwtech.es.model.DateRange;
import com.xwtech.es.model.HealthBean;
import com.xwtech.es.service.AlarmService;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.model.App;
import com.xwtech.omweb.model.AppGroup;
import com.xwtech.omweb.model.Server;
import com.xwtech.omweb.service.IAppGroupService;
import com.xwtech.omweb.service.IAppService;
import com.xwtech.omweb.service.IServerService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/3/21 0021.
 * 运行概述
 */

@Controller
@RequestMapping("omweb/runStatus")
public class RunStatusController {

    private final static Logger logger = LoggerFactory.getLogger(RunStatusController.class);

    //应用
    @Autowired
    private IAppService appService;
    //主机
    @Autowired
    private IServerService serverService;

    @Autowired
    private AlarmService alarmService;

    //应用组
    @Autowired
    IAppGroupService appGroupService;

    @RequestMapping(value = "index")
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("runStatus/index");
        List<AppGroup> appGroups = appGroupService.getAllAppGroups();
        for (AppGroup appGroup : appGroups) {
            logger.debug("appgroup:{}-{}-{}", appGroup.getName(), appGroup.getNum(), appGroup.getId());
        }

        modelAndView.addObject("appGroups", appGroups);
        return modelAndView;

    }

    @RequestMapping("getApp")
    @ResponseBody
    public JSONResult getAppList(String dateType,String date,String app,String host){
        JSONResult jsonResult = new JSONResult();
        try{
            DateRange dateRange = null;
            if (StringUtils.isNotEmpty(dateType) && StringUtils.isNotEmpty(date))
            {
                dateRange = parseDateRange(dateType, date);
            }
            List<HealthBean.AppBean> appList = alarmService.queryHealths(dateRange,app, host);
            jsonResult.setData(appList);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return jsonResult;

    }

    @RequestMapping("queryPortDetail")
    @ResponseBody
    public JSONResult  queryPortDetail(String app,String host){
        JSONResult jsonResult = new JSONResult();
        List<HealthBean> healthBeen = alarmService.queryPortDetail(app, host);
        jsonResult.setData(healthBeen);
        return jsonResult;
    }


    /**
     * 时间选择
     * @param dateType
     * @param date
     * @return
     */
    DateRange parseDateRange(String dateType, String date) {
        DateEnum dateEnum = DateEnum.values()[Integer.parseInt(dateType)];
        DateRange dateRange = null;
        if ("0".equals(dateType)) {
            String[] dates = date.split(" - ", -1);
            Date start = null, end = null;
            try {
                start = DateUtils.parseDate(dates[0], DateFormatUtil.DateFormatMil);
                end = DateUtils.parseDate(dates[1], DateFormatUtil.DateFormatMil);
            } catch (ParseException e) {
                logger.error("开始时间：{} 结束时间：{}", dates[0], dates[1]);
                logger.error("转换时间异常，时间格式不正确", e);
            }
            dateRange = new DateRange(start, end);
        } else {
            dateRange = new DateRange(dateEnum);
        }
        return dateRange;
    }


    @RequestMapping(value = {"apps"})
    @ResponseBody
    public JSONResult apps(String appGroupId) {

        JSONResult jsonResult = new JSONResult();

        List<App> apps = appService.queryAppListByGroupId(appGroupId, null);

        jsonResult.setData(apps);

        return jsonResult;

    }

    @RequestMapping(value = {"hosts"})
    @ResponseBody
    public JSONResult hosts(String appId) {

        JSONResult jsonResult = new JSONResult();

        List<Server> apps = serverService.queryServerListByAppId(appId);

        jsonResult.setData(apps);

        return jsonResult;

    }

}

