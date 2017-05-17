package com.xwtech.omweb.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.xwtech.es.ElasticConfig;
import com.xwtech.es.service.AlarmService;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.model.Metrics;
import com.xwtech.omweb.service.IMetricsService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
@Controller
@RequestMapping("omweb/metrics")
public class MetricsController {

    @Autowired
    private IMetricsService metricsService;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private ElasticConfig elasticConfig;

    /**
     * 查看统计指标列表
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
        PageInfo<Metrics> pageInfo = new PageInfo<Metrics>();
        pageInfo.setPageSize(ps < 10 ? ps : 10);
        pageInfo.setPageNum(pn);
        List<Metrics> metricsList = metricsService.queryMetricsList(pageInfo);
        return new ModelAndView("metrics/index").addObject("metricsList", metricsList)
                .addObject("pageInfo",((Page<Metrics>) metricsList).toPageInfo());
    }

    /**
     * 跳转修改或新增统计指标
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ModelAndView create(String metric_id){
        String actionType = "create";
        Metrics metrics = new Metrics();
        if (StringUtils.isNotEmpty(metric_id)) {
            actionType= "modfiy";
            metrics = metricsService.queryMetricsDetail(metric_id);
        }
        return new ModelAndView("metrics/create").addObject("actionType",actionType)
                .addObject("metrics",metrics);
    }

    /**
     * 新增修改统计指标
     * @param request
     * @return
     */
    @RequestMapping(value = "create",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult createSubmit(HttpServletRequest request, @ModelAttribute Metrics metrics){
        String actionType = request.getParameter("actionType");
        JSONResult jsonResult = new JSONResult();
        try
        {
            int i = metricsService.validateName(StringUtils.isNotEmpty(metrics.getMetric_id()) ? metrics.getMetric_id() : null, metrics.getMetric());
           if (i > 0)
           {
               jsonResult.setFailInfo("指标类型已经存在,请修正");
               return jsonResult;
           }
            if ("create".equals(actionType))
            {
                //新增
                metrics.setMetric_id(UUID.randomUUID().toString().replace("-",""));
                int count = metricsService.addMetrics(metrics);
                if(count > 0)
                {
                    jsonResult.setSuccessInfo("新增统计指标成功！");
                }else
                {
                    jsonResult.setFailInfo("新增统计指标失败，请稍后再试！");
                }
            }else if ("modfiy".equals(actionType))
            {
                //修改
                int count = metricsService.updateMetrics(metrics);
                if(count > 0 )
                {
                    jsonResult.setSuccessInfo("修改统计指标成功！");
                }else
                {
                    jsonResult.setFailInfo("修改统计指标失败，请稍后再试！");
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
     * 根据ID删除统计指标
     * @param request
     * @return
     */
    @RequestMapping(value = "deleteMetrics",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult deleteMetrics(HttpServletRequest request){
        JSONResult jsonResult = new JSONResult();
        String metric_id = request.getParameter("metric_id");
        if (metric_id !=null && !metric_id.equals(""))
        {
            try {
                int count = metricsService.deleteMetricsById(metric_id);
                if(count > 0)
                {
                    jsonResult.setSuccessInfo("删除统计指标成功！");
                }else
                {
                    jsonResult.setFailInfo("删除统计指标失败，请稍后再试！");
                }
            }catch (Exception e)
            {
                e.printStackTrace();
                jsonResult.setFailInfo("删除统计指标异常，请稍后再试！");
            }
        }else
        {
            jsonResult.setFailInfo("删除统计指标参数异常，请稍后再试！");
        }

        return jsonResult;
    }


    /**
     * 发布数据至ES
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "appMetricsEs")
    @ResponseBody
    public JSONResult appMetricsEs() throws JsonProcessingException {
        JSONResult jsonResult = new JSONResult();
        try {
            List<Metrics> metricses = metricsService.queryMetricsList(null);
            Map<String,Object> result =new HashMap<String,Object>();
            result.put("metrics",metricses);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = objectMapper.writeValueAsString(result);
            System.out.print(json);
            int status = alarmService.postCommonEs(json, elasticConfig.getDictType(), elasticConfig.getMetrics());
            if (status >= 200 && status< 300)
            {
                jsonResult.setSuccessInfo("发布成功");
            }else
            {
                jsonResult.setSuccessInfo("发布失败");
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            jsonResult.setSuccessInfo("发布异常");
        }

        return jsonResult;
    }

}
