package com.xwtech.omweb.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.model.FrequencyMode;
import com.xwtech.omweb.service.IFrequencyModeService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2017/3/13 0013.
 */
@Controller
@RequestMapping("/omweb/frequency")
public class FrequencyModeController {

    @Autowired
    private IFrequencyModeService frequencyModeService;

    /**
     * 查看变频规则列表
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
        PageInfo<FrequencyMode> pageInfo = new PageInfo<FrequencyMode>();
        pageInfo.setPageSize(ps < 10 ? ps : 10);
        pageInfo.setPageNum(pn);
        List<FrequencyMode> frequencyModeList = frequencyModeService.queryFrequencyMode(pageInfo);
        return new ModelAndView("frequency/index").addObject("frequencyModeList", frequencyModeList)
                .addObject("pageInfo",((Page<FrequencyMode>) frequencyModeList).toPageInfo());
    }

    /**
     * 跳转修改或新增变频规则
     * @param frequency_code
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ModelAndView create(String frequency_code){
        String actionType = "create";
        FrequencyMode frequencyMode = new FrequencyMode();
        if (StringUtils.isNotEmpty(frequency_code)) {
            actionType= "modfiy";
            frequencyMode = frequencyModeService.queryFrequencyModeDetail(frequency_code);
        }
        return new ModelAndView("frequency/create").addObject("actionType",actionType)
                    .addObject("frequencyMode",frequencyMode);
    }

    /**
     * 新增修改变频规则
     * @param request
     * @param frequencyMode
     * @return
     */
    @RequestMapping(value = "create",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult createSubmit(HttpServletRequest request, @ModelAttribute FrequencyMode frequencyMode){
        String actionType = request.getParameter("actionType");
        JSONResult jsonResult = new JSONResult();
        try
        {
            int i = frequencyModeService.validateFrequencyName(StringUtils.isNoneEmpty(frequencyMode.getFrequency_code()) ? frequencyMode.getFrequency_code() : null, frequencyMode.getFrequency_name());
            if (i > 0)
            {
                jsonResult.setFailInfo("变频名称已经存在,请修正");
                return jsonResult;
            }

            if ("create".equals(actionType))
            {
                //新增
                frequencyMode.setFrequency_code(String.valueOf(System.currentTimeMillis()));
                int count = frequencyModeService.addFrequencyMode(frequencyMode);

                if(count > 0)
                {
                    jsonResult.setSuccessInfo("新增变频规则成功！");
                }else
                {
                    jsonResult.setFailInfo("新增变频规则失败，请稍后再试！");
                }
            }else if ("modfiy".equals(actionType))
            {
                //修改
                int count = frequencyModeService.updateFrequencyMode(frequencyMode);
                if(count > 0 )
                {
                    jsonResult.setSuccessInfo("修改变频规则成功！");
                }else
                {
                    jsonResult.setFailInfo("修改变频规则失败，请稍后再试！");
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
     * 根据ID删除变频规则
     * @param request
     * @return
     */
    @RequestMapping(value = "deleteFrequencyMode",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult deleteFrequencyMode(HttpServletRequest request){
        JSONResult jsonResult = new JSONResult();
        String frequency_code = request.getParameter("frequency_code");
        if (frequency_code !=null && !frequency_code.equals(""))
        {
            try {
                int count = frequencyModeService.deleteFrequencyMode(frequency_code);
                if(count > 0)
                {
                    jsonResult.setSuccessInfo("删除变频规则成功！");
                }else
                {
                    jsonResult.setFailInfo("删除变频规则失败，请稍后再试！");
                }
            }catch (Exception e)
            {
                e.printStackTrace();
                jsonResult.setFailInfo("删除变频规则异常，请稍后再试！");
            }
        }else
        {
            jsonResult.setFailInfo("删除变频规则参数异常，请稍后再试！");
        }

        return jsonResult;
    }





}
