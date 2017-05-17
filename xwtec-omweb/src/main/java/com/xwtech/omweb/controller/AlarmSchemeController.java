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
import com.xwtech.omweb.model.*;
import com.xwtech.omweb.service.IAlarmSchemeService;
import com.xwtech.omweb.service.IDictionariesService;
import com.xwtech.omweb.service.IFrequencyModeService;
import com.xwtech.omweb.service.INoticeStrategyService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2017/3/13 0013.
 */
@Controller
@RequestMapping("omweb/alarmScheme")
public class AlarmSchemeController {

    @Autowired
    private IAlarmSchemeService alarmSchemeService;

    @Autowired
    private IDictionariesService dictionariesService;

    @Autowired
    private INoticeStrategyService noticeStrategyService;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private ElasticConfig elasticConfig;

    /**
     * 查看告警方案列表
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
        PageInfo<AlarmScheme> pageInfo = new PageInfo<AlarmScheme>();
        pageInfo.setPageSize(ps < 10 ? ps : 10);
        pageInfo.setPageNum(pn);
        List<AlarmScheme> alarmSchemeList = alarmSchemeService.queryAlarmSchemeList(pageInfo);
        return new ModelAndView("alarmScheme/index").addObject("alarmSchemeList", alarmSchemeList)
                .addObject("pageInfo",((Page<AlarmScheme>) alarmSchemeList).toPageInfo());
    }

    /**
     * 跳转修改或新增告警方案
     * @param alert_scheme_code
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ModelAndView create(String alert_scheme_code){
        List<NoticeStrategy> noticeStrategyList = noticeStrategyService.queryNoticeStrategyList(null);
        String actionType = "create";
        List<Dictionaries> dictionarieList = dictionariesService.queryDictionariesList(null);
        List<AlarmSchemeUnits> alarmSchemeUnitsList = new ArrayList<AlarmSchemeUnits>();
        AlarmScheme alarmScheme = new AlarmScheme();
        if (StringUtils.isNotEmpty(alert_scheme_code)) {
            actionType= "update";
            alarmScheme = alarmSchemeService.queryAlarmSchemeDetail(alert_scheme_code);
            alarmSchemeUnitsList = alarmSchemeService.queryAlarmSchemeUnitsList(alert_scheme_code);
        }
        return new ModelAndView("alarmScheme/"+actionType)
                .addObject("noticeStrategyList",noticeStrategyList).addObject("alarmSchemeUnitsList",alarmSchemeUnitsList)
                .addObject("alarmScheme",alarmScheme).addObject("dictionarieList",dictionarieList);
    }

    /**
     * 新增修改告警方案
     * @param request
     * @return
     */
    @RequestMapping(value = "create",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult createSubmit(HttpServletRequest request, @ModelAttribute AlarmScheme alarmScheme){
        JSONResult jsonResult = new JSONResult();
        try
        {
            if (!StringUtils.isNotEmpty(alarmScheme.getAlert_scheme_code()))
            {
                //新增
                alarmScheme.setAlert_scheme_code(String.valueOf(System.currentTimeMillis()));
                int count = alarmSchemeService.addAlarmScheme(alarmScheme);

                if(count > 0)
                {
                    jsonResult.setSuccessInfo("新增告警方案成功！");
                }else
                {
                    jsonResult.setFailInfo("新增告警方案失败，请稍后再试！");
                }
            }else
            {
                //修改
                int count = alarmSchemeService.updateAlarmScheme(alarmScheme);
                if(count > 0 )
                {
                    jsonResult.setSuccessInfo("修改告警方案成功！");
                }else
                {
                    jsonResult.setFailInfo("修改告警方案失败，请稍后再试！");
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
     * 根据ID删除告警方案
     * @param request
     * @return
     */
    @RequestMapping(value = "deleteAlarmSchemeMode",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult deleteAlarmSchemeMode(HttpServletRequest request){
        JSONResult jsonResult = new JSONResult();
        String alert_scheme_code = request.getParameter("alert_scheme_code");
        if (alert_scheme_code !=null && !alert_scheme_code.equals(""))
        {
            try {
                int count = alarmSchemeService.deleteAlarmScheme(alert_scheme_code);
                if(count > 0)
                {
                    jsonResult.setSuccessInfo("删除告警方案成功！");
                }else
                {
                    jsonResult.setFailInfo("删除告警方案失败，请稍后再试！");
                }
            }catch (Exception e)
            {
                e.printStackTrace();
                jsonResult.setFailInfo("删除告警方案异常，请稍后再试！");
            }
        }else
        {
            jsonResult.setFailInfo("删除告警方案参数异常，请稍后再试！");
        }

        return jsonResult;
    }


    /**
     * 新增修改告警方案单元格
     * @param request
     * @return
     */
    @RequestMapping(value = "createUnits",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult createUnits(HttpServletRequest request, @ModelAttribute AlarmSchemeUnits alarmSchemeUnits){
        JSONResult jsonResult = new JSONResult();
        try
        {
            if (!StringUtils.isNotEmpty(alarmSchemeUnits.getUntis_id()))
            {
                //新增
                alarmSchemeUnits.setUntis_id(String.valueOf(System.currentTimeMillis()));
                int count = alarmSchemeService.addAlarmSchemeUnits(alarmSchemeUnits);

                if(count > 0)
                {
                    jsonResult.setSuccessInfo("新增告警方案单元格成功！");
                }else
                {
                    jsonResult.setFailInfo("新增告警方案单元格失败，请稍后再试！");
                }
            }else 
            {
                //修改
                int count = alarmSchemeService.updateAlarmSchemeUnits(alarmSchemeUnits);
                if(count > 0 )
                {
                    jsonResult.setSuccessInfo("修改告警方案单元格成功！");
                }else
                {
                    jsonResult.setFailInfo("修改告警方案单元格失败，请稍后再试！");
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            jsonResult.setFailInfo("系统异常，请稍后再试！");
        }

        return jsonResult;
    }



    @RequestMapping("unitsDetail")
    @ResponseBody
    public JSONResult unitsDetail(String untis_id){
        JSONResult jsonResult = new JSONResult();
        try {
            AlarmSchemeUnits alarmSchemeUnits = alarmSchemeService.queryAlarmSchemeUnitsDetail(untis_id);
            jsonResult.setData(alarmSchemeUnits);
        }catch (Exception e)
        {
            e.printStackTrace();
            jsonResult.setFailInfo("查询告警方案单元格详情异常");
        }

        return jsonResult;
    }

    @RequestMapping("deleteUnits")
    @ResponseBody
    public JSONResult deleteUnits(String untis_id){
        JSONResult jsonResult = new JSONResult();
        try {
            int i = alarmSchemeService.deleteAlarmSchemeUnits(untis_id);
            if (i >0)
            {
                jsonResult.setSuccessInfo("删除告警方案单元格成功");
            }else
            {
                    jsonResult.setFailInfo("删除告警方案单元格失败");
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            jsonResult.setFailInfo("查询告警方案单元格详情异常");
        }

        return jsonResult;
    }


    @RequestMapping(value = "appAlarmSchemeModeEs")
    @ResponseBody
    public JSONResult appAlarmSchemeModeEs(String alert_scheme_code) throws JsonProcessingException {
        JSONResult jsonResult = new JSONResult();
        AlarmScheme alarmScheme = alarmSchemeService.queryAlarmSchemeDetail(alert_scheme_code);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String json = objectMapper.writeValueAsString(alarmScheme);
        System.out.print(json);
        int status = alarmService.postCommonEs(json,elasticConfig.getSchemeType(),alarmScheme.getAlert_scheme_code());
        if (status  >= 200 && status <300)
        {
            jsonResult.setSuccessInfo("推送ES成功");
        }else
        {
            jsonResult.setFailInfo("推送ES失败");
        }
        return jsonResult;
    }





}
