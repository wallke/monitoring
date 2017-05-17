package com.xwtech.omweb.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.model.*;
import com.xwtech.omweb.service.*;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
@Controller
@RequestMapping("omweb/monitorTarget")
public class MonitorTargetController {


    @Autowired
    private IMonitorTargetService monitorTargetService;

    //主机
    @Autowired
    private IServerService serverService;
    //应用
    @Autowired
    private IAppService appService;
    //监听目标
    @Autowired
    private IMonitorTemplateService monitorTemplateService;
    //告警方案
    @Autowired
    private IAlarmSchemeService alarmSchemeService;
    //预警单元格
    @Autowired
    private IMetricsService metricsService;
    //预警网格
    @Autowired
    private IGridService gridService;


    /**
     * 监听目标集合
     * @param request
     * @param cropName
     * @param ps
     * @param pn
     * @return
     */
    @RequestMapping(value = "index",method ={ RequestMethod.GET, RequestMethod.POST})
    public ModelAndView index(HttpServletRequest request,
                              String cropName, @RequestParam(name = "ps", defaultValue = "10") int ps,
                              @RequestParam(name = "pn", defaultValue = "1") int pn){
        PageInfo<MonitorTarget> pageInfo = new PageInfo<MonitorTarget>();
        pageInfo.setPageSize(ps < 10 ? ps : 10);
        pageInfo.setPageNum(pn);
        List<MonitorTarget> monitorTargetList = monitorTargetService.queryMonitoringTargetList(pageInfo);
        return new ModelAndView("monitorTarget/index").addObject("monitorTargetList", monitorTargetList)
                .addObject("pageInfo",((Page<MonitorTarget>) monitorTargetList).toPageInfo());
    }

    /**
     * 新增监听目标
     * @param target_code
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ModelAndView create(String target_code){

        //主机列表
        List<Server> serverList = serverService.queryServerList();
        //应用列表
        List<App> appList = appService.getAppList();
        //监听目标
        List<MonitorTemplate> monitorTemplateList = monitorTemplateService.queryMonitorTemplateList(null);
        //告警方案列表
        List<AlarmScheme> alarmSchemeList = alarmSchemeService.queryAlarmSchemeList(null);
        //预警网格列表
        List<Grid> gridList = gridService.queryGridList(null);

        String actionType = "create";
        MonitorTarget monitorTarget = new MonitorTarget();
        if (StringUtils.isNotEmpty(target_code)) {
            actionType= "modfiy";
             monitorTarget = monitorTargetService.queryMonitoringTargetDetail(target_code);
        }
        return new ModelAndView("monitorTarget/create").addObject("actionType",actionType)
                .addObject("monitorTarget",monitorTarget).addObject("serverList",serverList)
                .addObject("appList",appList).addObject("monitorTemplateList",monitorTemplateList)
                .addObject("gridList",gridList)
                .addObject("alarmSchemeList",alarmSchemeList);
    }


    /**
     * 新增修改监听目标
     * @param request
     * @param monitorTarget
     * @return
     */
    @RequestMapping(value = "create",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult createSubmit(HttpServletRequest request, @ModelAttribute MonitorTarget monitorTarget){
        String actionType = request.getParameter("actionType");
        JSONResult jsonResult = new JSONResult();
        try
        {
            if ("create".equals(actionType))
            {
                //新增
                monitorTarget.setTarget_code(DateFormatUtils.format(new Date(), "yyyyMMddHHmmssS"));
                monitorTarget.setTarget_code(String.valueOf(System.currentTimeMillis()));
                int count = monitorTargetService.addMonitoringTarget(monitorTarget);
                if(count > 0)
                {
                    jsonResult.setSuccessInfo("新增监听目标成功！");
                }else
                {
                    jsonResult.setFailInfo("新增监听目标失败，请稍后再试！");
                }
            }else if ("modfiy".equals(actionType))
            {
                //修改
                int count = monitorTargetService.updateMonitoringTarget(monitorTarget);
                if(count > 0 )
                {
                    jsonResult.setSuccessInfo("修改监听目标成功！");
                }else
                {
                    jsonResult.setFailInfo("修改监听目标失败，请稍后再试！");
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            jsonResult.setFailInfo("系统异常，请稍后再试！");
        }
        return jsonResult;
    }


    /**
     * 根据ID删除监听指标
     * @param request
     * @return
     */
    @RequestMapping(value = "deleteMonitorTarget",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult deleteMonitorTarget(HttpServletRequest request){
        JSONResult jsonResult = new JSONResult();
        String target_code = request.getParameter("target_code");
        String target_name = request.getParameter("target_name");
        if (target_code !=null && !target_code.equals(""))
        {
            try {
                int count = monitorTargetService.deleteMonitoringTarget(target_code,target_name);
                if(count > 0)
                {
                    jsonResult.setSuccessInfo("删除监听目标成功！");
                }else
                {
                    jsonResult.setFailInfo("删除监听目标失败，请稍后再试！");
                }
            }catch (Exception e)
            {
                e.printStackTrace();
                jsonResult.setFailInfo("删除监听目标异常，请稍后再试！");
            }
        }else
        {
            jsonResult.setFailInfo("删除监听目标参数异常，请稍后再试！");
        }

        return jsonResult;
    }


}
