package com.xwtech.omweb.controller.monitoring;

import com.xwtech.es.DateFormatUtil;
import com.xwtech.es.model.*;
import com.xwtech.es.service.ApplicationService;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.model.*;
import com.xwtech.omweb.service.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.*;

/**
 * Created by Administrator on 2017/2/14 0014.
 */
@Controller
@RequestMapping("omweb/env")
public class EnvController {

    private final static Logger logger = LoggerFactory.getLogger(EnvController.class);

    //应用
    @Resource(name = "appService")
    private IAppService appService;
    //主机
    @Resource(name = "serverService")
    private IServerService serverService;

    @Resource(name = "sdkService")
    private SDKService sdkService;

    //节点
    @Autowired
    private INodeService nodeService;

    @Autowired
    IAppGroupService appGroupService;

    @Autowired
    ApplicationService applicationService;


    @RequestMapping(value = {"", "/", "index"})
    public ModelAndView index(HttpServletRequest request) {

        ModelAndView modelAndView = new ModelAndView("monitoring/env/index");
        List<AppGroup> appGroups = appGroupService.getAllAppGroups();
        for (AppGroup appGroup : appGroups) {
            logger.debug("appgroup:{}-{}-{}", appGroup.getName(), appGroup.getNum(), appGroup.getId());
        }
        modelAndView.addObject("appGroups", appGroups);


        return modelAndView;

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

    @RequestMapping(value = {"ports"})
    @ResponseBody
    public JSONResult ports(String appId, String serverId) {

        JSONResult jsonResult = new JSONResult();
        List<Node> apps = nodeService.queryNodeList(appId, serverId);
        jsonResult.setData(apps);
        return jsonResult;

    }
    @RequestMapping(value = {"portsByServerId"})
    @ResponseBody
    public JSONResult portsByServerId(String serverId) {
        JSONResult jsonResult = new JSONResult();
         List<Node> apps = nodeService.queryNodeListByServerId(serverId);
         jsonResult.setData(apps);
        return jsonResult;

    }

    /**
     * @param date
     * @param host
     * @param port
     * @param metrics
     * @param envName
     * @return
     */
    @RequestMapping(value = {"env_metrics"})
    @ResponseBody
    public JSONResult env_metrics(
            String dateType,
            String date,
            String appCode,
            String appId,
            String host,
            String port,
            String metrics,
            String envName) {

        JSONResult jsonResult = new JSONResult();

        DateRange dateRange = parseDateRange(dateType, date);

        //查询统计信息
        List<EnvironmentBean> list =
                applicationService.getEnvironmentAggregations(dateRange, appCode, host, port);

        //查询应用所有环境名
        List<SDKConfEnv> envs = sdkService.querySDKConfEnvListByAppId(appId);

        List<EnvironmentBean> environmentBeans = new ArrayList<>();
        if (envs != null && envs.size() > 0) {
            for (SDKConfEnv sdkConfEnv : envs) {
                if (StringUtils.isNotEmpty(envName) && !sdkConfEnv.getEnvirName().toLowerCase().contains(envName.toLowerCase())) {
                    continue;
                }

                if (list != null) {
                    for (EnvironmentBean item : list) {
                        if (item.getEnvironmentId().equals(sdkConfEnv.getEnvirId())) {
                            item.setEnvironmentName(sdkConfEnv.getEnvirName());
                            environmentBeans.add(item);
                            break;
                        }
                    }
                }

            }
        }

        jsonResult.setData(environmentBeans);

        return jsonResult;

    }


    @RequestMapping(value = {"env_service_proportion"})
    @ResponseBody
    public JSONResult env_service_proportion(
            String dateType,
            String date,
            String appCode,
            String host,
            String port,
            String envId

    ) {

        JSONResult jsonResult = new JSONResult();

        DateRange dateRange = parseDateRange(dateType, date);

        List<NodeBean> list = applicationService.getNodeAggregations(dateRange, envId, appCode, host, port);

        jsonResult.setData(list);

        return jsonResult;
    }


    @RequestMapping(value = {"service"})
    public ModelAndView service(HttpServletRequest request) {

        ModelAndView modelAndView = new ModelAndView("monitoring/env/service");
        List<AppGroup> appGroups = appGroupService.getAllAppGroups();
        for (AppGroup appGroup : appGroups) {
            logger.debug("appgroup:{}-{}-{}", appGroup.getName(), appGroup.getNum(), appGroup.getId());
        }
        modelAndView.addObject("appGroups", appGroups);


        return modelAndView;

    }


    /**
     * @param dateType
     * @param date
     * @param appCode
     * @param serviceName
     * @param host
     * @param port
     * @return
     */
    @RequestMapping(value = {"env_service_metrics"})
    @ResponseBody
    public JSONResult env_service_metrics(
            String dateType,
            String date,
            String appCode,
            String serviceName,
            String host,
            String port) {

        JSONResult jsonResult = new JSONResult();

        DateRange dateRange = parseDateRange(dateType, date);

        //查询统计信息
        List<NodeBean> list =
                applicationService.getNodeAggregations(dateRange, null, appCode, host, port);

        List<NodeBean> nodeBeans = new ArrayList<>();
        if (StringUtils.isNotEmpty(serviceName)) {
            if (list != null && list.size() > 0) {
                for (NodeBean item : list) {
                    if (item.getNodeCode().toLowerCase().contains(serviceName.toLowerCase())) {
                        nodeBeans.add(item);
                        break;
                    }
                }
            }
        } else {
            nodeBeans = list;

        }


        jsonResult.setData(nodeBeans);

        return jsonResult;

    }


    @RequestMapping(value = {"exception_status"})
    public ModelAndView exception_status(HttpServletRequest request) {

        ModelAndView modelAndView = new ModelAndView("monitoring/env/exception_status");
        List<AppGroup> appGroups = appGroupService.getAllAppGroups();
        for (AppGroup appGroup : appGroups) {
            logger.debug("appgroup:{}-{}-{}", appGroup.getName(), appGroup.getNum(), appGroup.getId());
        }
        modelAndView.addObject("appGroups", appGroups);

        return modelAndView;

    }


    @RequestMapping(value = {"exception_status_metrics"})
    @ResponseBody
    public JSONResult exception_status_metrics(String appGroupId) {

        JSONResult jsonResult = new JSONResult();

        List<App> apps = appService.queryAppListByGroupId(appGroupId, null);

        List<AppErrorBean> appErrorBeans = new ArrayList<>();

        if (apps != null && apps.size() > 0) {
           // List<String> appCodes = new ArrayList<>();
            for (App app : apps) {

                AppErrorBean    appErrorBean =  applicationService.getAppError(app.getAppNum());
                appErrorBean.setAppName(app.getAppName());

                appErrorBeans.add(appErrorBean);
            }

//
//
//            for (AppErrorBean appErrorBean : appErrorBeans) {
//
//                for (App app : apps) {
//                    if (app.getAppNum().equals(appErrorBean.getAppCode())) {
//                        appErrorBean.setAppName(app.getAppName());
//                        break;
//                    }
//                }
//
//            }

        }

        jsonResult.setData(appErrorBeans);

        return jsonResult;

    }


    @RequestMapping(value = {"exception_status_node"})
    @ResponseBody
    public JSONResult exception_status_node(String appCode) {

        JSONResult jsonResult = new JSONResult();


        List<NodeErrorBean> nodeErrorBeans = applicationService.getNodeError( appCode);

        List<NodeErrorBean> nodeErrorBeans1 = applicationService.getHostError( appCode);

        Map<String, List<NodeErrorBean>> map = new HashMap<>();
        map.put("host", nodeErrorBeans1);
        map.put("method", nodeErrorBeans);

        jsonResult.setData(map);

        return jsonResult;

    }


    @RequestMapping(value = {"exceptions"})
    public ModelAndView exceptions(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("monitoring/env/exceptions");
        List<AppGroup> appGroups = appGroupService.getAllAppGroups();
        modelAndView.addObject("appGroups", appGroups);
        return modelAndView;
    }


    @RequestMapping(value = {"exceptions_type"})
    @ResponseBody
    public JSONResult exceptions_type(String dateType,
                                      String date,
                                      String appCode,
                                      String appId,
                                      String host,
                                      String port,
                                      String key) {

        JSONResult jsonResult = new JSONResult();

        DateRange dateRange = parseDateRange(dateType, date);

        List<ErrorsBean> nodeErrorBeans = applicationService.getErrorsAgg(dateRange, appCode, host, port, key);


        jsonResult.setData(nodeErrorBeans);

        return jsonResult;

    }


    @RequestMapping(value = {"exceptions_metrics"})
    public ModelAndView exceptions_metrics(String dateType,
                                           String date,
                                           String appCode,
                                           String appId,
                                           String host,
                                           String port,
                                           String key,
                                           String type) {
        ModelAndView modelAndView = new ModelAndView("monitoring/env/exceptions_metrics");
        modelAndView.addObject("dateType", dateType);
        modelAndView.addObject("date", date);
        modelAndView.addObject("appCode", appCode);
        modelAndView.addObject("appId", appId);
        modelAndView.addObject("host", host);
        modelAndView.addObject("port", port);
        modelAndView.addObject("key", key);
        modelAndView.addObject("type", type);
        return modelAndView;
    }


    @RequestMapping(value = {"exceptions_date_metrics"})
    @ResponseBody
    public JSONResult exceptions_date_metrics(String dateType,
                                              String date,
                                              String appCode,
                                              String appId,
                                              String host,
                                              String port,
                                              String key,
                                              String type) {

        JSONResult jsonResult = new JSONResult();

        DateRange dateRange = parseDateRange(dateType, date);

        List<DateHistogramBean> dateHistogramBeans
                = applicationService.getErrorsDateAgg(dateRange, appCode, host, port, type, key);


        jsonResult.setData(dateHistogramBeans);

        return jsonResult;

    }


    @RequestMapping(value = {"exceptions_date_metrics_detail"})
    @ResponseBody
    public JSONResult exceptions_date_metrics_detail(String dateType,
                                                     String date,
                                                     String currentDate,
                                                     String appCode,
                                                     String appId,
                                                     String host,
                                                     String port,
                                                     String key,
                                                     String type,
                                                     @RequestParam(name = "ps", defaultValue = "10") int ps,
                                                     @RequestParam(name = "pn", defaultValue = "1") int pn) {

        JSONResult jsonResult = new JSONResult();

        DateRange dateRange = parseDateRange(dateType, date);
        Date start = null, end = null;
        try {
            start = DateUtils.parseDate(currentDate, dateRange.getFormat());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);
            calendar.add(Calendar.MILLISECOND, (int) dateRange.getInterval());
            end = calendar.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Map<String, Object> map
                = applicationService.getErrorDetails(start, end, appCode, host, port, type, key,(pn - 1) * ps, ps);


        Map<String, Object> page = new HashMap<>();

        Long total = Long.parseLong(map.get("total").toString());
        Long pages = total / ps + ((total % ps == 0) ? 0 : 1);

        page.put("total", total);
        page.put("pn", pn);
        page.put("ps", ps);
        page.put("pages", pages);

        Map<String, Object> result = new HashMap<>();

        List<LogBean> list = (List<LogBean>) map.get("list");

        if(list != null && list.size() > 0){
            //应用
            //App app = appService.queryAppDeatil(appId);
            List<App> apps = appService.getAppList();

            //查询应用所有环境名
            List<SDKConfEnv> envs = sdkService.querySDKConfEnvListByAppId(appId);

            list.forEach(logBean -> {

                if (apps != null) {
                    for (App app : apps) {
                        if (app != null && app.getAppNum().equals(logBean.getSystem())) {
                            logBean.setSystem_name(app.getAppName());
                            break;
                        }
                    }
                }
                if (envs != null) {
                    for (SDKConfEnv sdkConfEnv : envs) {
                        if (sdkConfEnv != null && sdkConfEnv.getEnvir_ID().equals(logBean.getEnvir_ID())) {
                            logBean.setEnvir_name(sdkConfEnv.getEnvir_name());
                            break;
                        }
                    }

                }
            });
        }



        result.put("page", page);
        result.put("data", list);
        jsonResult.setData(result);

        return jsonResult;

    }


    @RequestMapping(value = "relation", method = RequestMethod.GET)
    private ModelAndView relation() {


        List<AppGroup> list = appGroupService.getAllAppGroups();

        ModelAndView modelAndView =
                new ModelAndView("monitoring/env/relation").
                        addObject("appGroups", list).addObject("groupId", list.get(0).getId());
        return modelAndView;
    }

    @RequestMapping(value = "getCanvasData", method = RequestMethod.GET)
    @ResponseBody
    private JSONResult getCanvasData(String dateType,
                                     String date,
                                     String groupId) {
        JSONResult jsonResult = new JSONResult();
        try {

            DateRange dateRange = parseDateRange(dateType, date);

            List<AppCanvasPosition> appList = appGroupService.getAppsByGroupId(groupId);
            List<AppCanvasLinks> links = appGroupService.getAppLinksByGroupId(groupId);

            if (links != null) {

                for (AppCanvasLinks appCanvasLinks : links) {

                    Map<String, Long> result =
                            applicationService.getChainCount(
                                    dateRange,
                                    appCanvasLinks.getSourceNum(),
                                    appCanvasLinks.getTargetNum());

                    appCanvasLinks.setRequestCount(result.get("count"));
                    appCanvasLinks.setRequestErrorCount(result.get("error"));
                }

            }

            Map map = new HashMap();
            map.put("locations", appList);
            map.put("connections", links);
            jsonResult.setData(map);

        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.setFailInfo("系统异常，请稍后再试！");
        }
        return jsonResult;
    }


    @RequestMapping(value = "relationRequestInfo", method = RequestMethod.GET)
    @ResponseBody
    private JSONResult relationRequestInfo(String dateType,
                                           String date,
                                           String sourceAppCode,
                                           String targetAppCode) {

        JSONResult jsonResult = new JSONResult();

        try {
            DateRange dateRange = parseDateRange(dateType, date);

            //List<ChainBean> list = applicationService.getChainAggregations(dateRange, sourceAppCode, targetAppCode);

            List<Map<String, String>> list1 = applicationService.getChains(dateRange, sourceAppCode, targetAppCode,false);
            List<Map<String, String>> list3 = applicationService.getChains(dateRange, sourceAppCode, targetAppCode,true);
            if(list1 != null && list3 != null){
                list1.addAll(list3);
            }
            List<Map> list2 = applicationService.getChainsAggByTime(dateRange, sourceAppCode, targetAppCode);

            Map map = new HashMap();

            map.put("list1", list1);
            map.put("list2", list2);

            jsonResult.setData(map);

        } catch (Exception ex) {
            jsonResult.setErrorInfo("系统异常，请稍后再试！");
        }

        return jsonResult;
    }


    /**
     * 时间选择
     *
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


    /**
     * 应用日志调用链
     *
     * @return
     */
    @RequestMapping(value = "trans", method = RequestMethod.GET)
    private ModelAndView trans() {

        List<AppGroup> list  = appGroupService.getAllAppGroups();

        ModelAndView modelAndView =
                new ModelAndView("monitoring/env/trans").
                        addObject("appGroups", list).addObject("groupId", list.get(0).getId());
        return modelAndView;
    }


    /**
     * 分页检索调用链日志 起始日志 last_node_id 不存在的transaction日志
     *
     * @param dateType
     * @param date
     * @param appCode
     * @param appId
     * @param host
     * @param port
     * @param key
     * @param ps
     * @param pn
     * @return
     */
    @RequestMapping(value = "trans_data", method = RequestMethod.GET)
    @ResponseBody
    private JSONResult trans_data(String dateType,
                                  String date,
                                  String appCode,
                                  String appId,
                                  String host,
                                  String port,
                                  String key,
                                  @RequestParam(name = "ps", defaultValue = "10") int ps,
                                  @RequestParam(name = "pn", defaultValue = "1") int pn) {

        JSONResult jsonResult = new JSONResult();
        try {
            DateRange dateRange = parseDateRange(dateType, date);

            Map<String, Object> map = applicationService.getLogs(
                    dateRange, appCode, host, port, key, (pn - 1) * ps, ps);

            Map<String, Object> page = new HashMap<>();

            Long total = Long.parseLong(map.get("total").toString());
            Long pages = total / ps + ((total % ps == 0) ? 0 : 1);

            page.put("total", total);
            page.put("pn", pn);
            page.put("ps", ps);
            page.put("pages", pages);

            Map<String, Object> result = new HashMap<>();

            List<LogBean> list = (List<LogBean>) map.get("list");

            //应用
            //App app = appService.queryAppDeatil(appId);
            List<App> apps = appService.getAppList();
            //查询应用所有环境名
            List<SDKConfEnv> envs = sdkService.querySDKConfEnvListByAppId(appId);

            list.forEach(logBean -> {

                if (apps != null) {
                    for (App app : apps) {
                        if (app != null && app.getAppNum().equals(logBean.getSystem())) {
                            logBean.setSystem_name(app.getAppName());
                            break;
                        }
                    }
                }
                if (envs != null) {
                    for (SDKConfEnv sdkConfEnv : envs) {
                        if (sdkConfEnv != null && sdkConfEnv.getEnvir_ID().equals(logBean.getEnvir_ID())) {
                            logBean.setEnvir_name(sdkConfEnv.getEnvir_name());
                            break;
                        }
                    }

                }
            });

            result.put("page", page);
            result.put("data", list);

            jsonResult.setData(result);

        } catch (Exception ex) {
            jsonResult.setErrorInfo("系统异常，请稍后再试！");
        }

        return jsonResult;
    }


    /**
     * 应用日志调用链
     *
     * @return
     */
    @RequestMapping(value = "trans_all", method = RequestMethod.GET)
    private ModelAndView trans_all(String serviceId) {
        ModelAndView modelAndView =
                new ModelAndView("monitoring/env/trans_all");

        List<LogBean> list = applicationService.getLogs(serviceId);

        List<LogBean> temp = filterChildren(null, list);
        List<LogBean> result = new ArrayList<>();
        if (temp.size() > 0) {
            result.addAll(filter(temp, list));
        }

        if (result != null && result.size() > 0) {
            List<App> apps = appService.getAppList();

            //查询应用所有环境名
            //List<SDKConfEnv> envs = sdkService.querySDKConfEnvListByAppId(appId);

            result.forEach(logBean -> {
                if (apps != null) {
                    for (App app : apps) {
                        if (app != null && app.getAppNum().equals(logBean.getSystem())) {
                            logBean.setSystem_name(app.getAppName());
                            break;
                        }
                    }
                }
//                if (envs != null) {
//                    for (SDKConfEnv sdkConfEnv : envs) {
//                        if (sdkConfEnv != null && sdkConfEnv.getEnvir_ID().equals(logBean.getEnvir_ID())) {
//                            logBean.setEnvir_name(sdkConfEnv.getEnvir_name());
//                            break;
//                        }
//                    }
//
//                }
            });
        }

        modelAndView.addObject("logs", result);

        return modelAndView;
    }


    /**
     * 查询一个调用链所有日志
     *
     * @param serviceId
     * @param appId
     * @return
     */
    @RequestMapping(value = "trans_all_logs", method = RequestMethod.GET)
    @ResponseBody
    private JSONResult trans_all_logs(String serviceId, String appId) {

        JSONResult jsonResult = new JSONResult();
        try {


            List<LogBean> list = applicationService.getLogs(serviceId);

            List<LogBean> temp = filterChildren(null, list);
            List<LogBean> result = new ArrayList<>();
            if (temp.size() > 0) {
                result.addAll(filter(temp, list));
            }
            //应用
            //App app = appService.queryAppDeatil(appId);

            List<App> apps = appService.getAppList();

            //查询应用所有环境名
            List<SDKConfEnv> envs = sdkService.querySDKConfEnvListByAppId(appId);

            result.forEach(logBean -> {

                if (apps != null) {
                    for (App app : apps) {
                        if (app != null && app.getAppNum().equals(logBean.getSystem())) {
                            logBean.setSystem_name(app.getAppName());
                            break;
                        }
                    }
                }
                if (envs != null) {
                    for (SDKConfEnv sdkConfEnv : envs) {
                        if (sdkConfEnv != null && sdkConfEnv.getEnvir_ID().equals(logBean.getEnvir_ID())) {
                            logBean.setEnvir_name(sdkConfEnv.getEnvir_name());
                            break;
                        }
                    }

                }
            });

            jsonResult.setData(result);

        } catch (Exception ex) {
            logger.error("", ex);
            jsonResult.setErrorInfo("系统异常，请稍后再试！");
        }

        return jsonResult;
    }

    /**
     * 查询调用链日志的下一节点日志
     *
     * @param prev
     * @param list
     * @return
     */
    List<LogBean> filterChildren(LogBean prev, List<LogBean> list) {

        List<LogBean> result = new ArrayList<>();

        for (LogBean logBean : list) {
            //prevId 为空 查询起点日志 及不存在上一节点日志
            if (prev == null) {
                if (StringUtils.isEmpty(logBean.getLast_node_ID())) {
                    result.add(logBean);
                }
            } else {
                if ("chain".equals(prev.getLog_type()) && "transaction".equals(logBean.getLog_type())) {
                    if (prev.getInvoke_node_ID().equals(logBean.getInvoke_node_ID())
                            && prev.getSystem().equals(logBean.getLast_system())
                            && prev.getNode_code().equals(logBean.getNode_code())
                            ) {
                        logBean.setLast_node_code(prev.getNode_code());
                        logBean.setLast_node_ID(prev.getNode_ID());
                        result.add(logBean);
                    }

                } else{
                    if (StringUtils.isEmpty(logBean.getInvoke_node_ID())
                            && prev.getNode_ID().equals(logBean.getLast_node_ID())
                            && prev.getSystem().equals(logBean.getSystem())) {
                        result.add(logBean);
                    }//调用链日志
                    else if (
                            "chain".equals(logBean.getLog_type()) &&
                                    StringUtils.isNotEmpty(logBean.getInvoke_node_ID())
                                    && prev.getNode_ID().equals(logBean.getInvoke_node_ID())

                            ) {
                        logBean.setLast_node_code(logBean.getInvoke_node_code());
                        logBean.setLast_node_ID(logBean.getInvoke_node_ID());
                        logBean.setNode_ID(UUID.randomUUID().toString().replaceAll("-", ""));
                        result.add(logBean);
                    }
                }

            }
        }

        return result;

    }

    /**
     * 递归调用链 节点关系梳理
     *
     * @param prevList
     * @param list
     * @return
     */
    List<LogBean> filter(List<LogBean> prevList, List<LogBean> list) {

        List<LogBean> result = new ArrayList<>();
        for (LogBean logBean : prevList) {
            result.add(logBean);
            if (StringUtils.isNotEmpty(logBean.getNode_ID())) {
                List<LogBean> temp = filterChildren(logBean, list);
                if (temp.size() > 0) {
                    result.addAll(filter(temp, list));
                }
            } else {
                logger.debug("{}", logBean);
            }

        }
        return result;

    }


}
