package com.xwtech.omweb.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.model.MonitorTemplate;
import com.xwtech.omweb.service.IMonitorTemplateService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
@Controller
@RequestMapping("omweb/monitorTemplate")
public class MonitorTemplateController {

    @Autowired
    private IMonitorTemplateService monitorTemplateService;

    /**
     * 查看监听模板列表
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
        PageInfo<MonitorTemplate> pageInfo = new PageInfo<MonitorTemplate>();
        pageInfo.setPageSize(ps < 10 ? ps : 10);
        pageInfo.setPageNum(pn);
        List<MonitorTemplate> monitorTemplateList = monitorTemplateService.queryMonitorTemplateList(pageInfo);
        return new ModelAndView("monitorTemplate/index").addObject("monitorTemplateList", monitorTemplateList)
                .addObject("pageInfo",((Page<MonitorTemplate>) monitorTemplateList).toPageInfo());
    }

    /**
     * 跳转修改或新增监听模板
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ModelAndView create(String monitor_template_id){
        String actionType = "create";
        MonitorTemplate monitorTemplate = new MonitorTemplate();
        if (StringUtils.isNotEmpty(monitor_template_id)) {
            actionType= "modfiy";
            monitorTemplate = monitorTemplateService.queryMonitorTemplateDetail(monitor_template_id);
        }
        return new ModelAndView("monitorTemplate/create").addObject("actionType",actionType)
                .addObject("monitorTemplate",monitorTemplate);
    }

    /**
     * 新增修改监听模板
     * @param request
     * @return
     */
    @RequestMapping(value = "create",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult createSubmit(HttpServletRequest request, @ModelAttribute MonitorTemplate monitorTemplate){
        String actionType = request.getParameter("actionType");
        JSONResult jsonResult = new JSONResult();
        try
        {
            int i = monitorTemplateService.validateTemplateName(StringUtils.isNoneEmpty(monitorTemplate.getMonitor_template_id()) ? monitorTemplate.getMonitor_template_id() : null, monitorTemplate.getTemplate_name());
           if (i > 0)
           {
               jsonResult.setFailInfo("监听目标已经存在,请重新修正");
               return jsonResult;
           }
            if ("create".equals(actionType))
            {
                //新增
                monitorTemplate.setMonitor_template_id(UUID.randomUUID().toString().replace("-",""));
                int count = monitorTemplateService.addMonitorTemplate(monitorTemplate);
                if(count > 0)
                {
                    jsonResult.setSuccessInfo("新增监听模板成功！");
                }else
                {
                    jsonResult.setFailInfo("新增监听模板失败，请稍后再试！");
                }
            }else if ("modfiy".equals(actionType))
            {
                //修改
                int count = monitorTemplateService.updateMonitorTemplate(monitorTemplate);
                if(count > 0 )
                {
                    jsonResult.setSuccessInfo("修改监听模板成功！");
                }else
                {
                    jsonResult.setFailInfo("修改监听模板失败，请稍后再试！");
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
     * 根据ID删除监听模板
     * @param request
     * @return
     */
    @RequestMapping(value = "deleteMonitorTemplate",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult deleteMonitorTemplate(HttpServletRequest request){
        JSONResult jsonResult = new JSONResult();
        String monitor_template_id = request.getParameter("monitor_template_id");
        String monitor_template_Name = request.getParameter("monitor_template_Name");
        if (monitor_template_id !=null && !monitor_template_id.equals(""))
        {
            try {
                int i = monitorTemplateService.queryTemplateNextTarget(monitor_template_id);
                if (i >0)
                {
                    jsonResult.setFailInfo("该模板下存在监听目标,清先删除挂载目标！");
                    return jsonResult;
                }

                int count = monitorTemplateService.deleteMonitorTemplateById(monitor_template_id,monitor_template_Name);
                if(count > 0)
                {
                    jsonResult.setSuccessInfo("删除监听模板成功！");
                }else
                {
                    jsonResult.setFailInfo("删除监听模板失败，请稍后再试！");
                }
            }catch (Exception e)
            {
                e.printStackTrace();
                jsonResult.setFailInfo("删除监听模板异常，请稍后再试！");
            }
        }else
        {
            jsonResult.setFailInfo("删除监听模板参数异常，请稍后再试！");
        }

        return jsonResult;
    }


}
