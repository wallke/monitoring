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
import com.xwtech.omweb.service.IDictionariesService;
import com.xwtech.omweb.service.IGridService;
import com.xwtech.omweb.service.IMetricsService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/3/17 0017.
 */
@Controller
@RequestMapping("omweb/grid")
public class GridController {

    @Autowired
    private IGridService gridService;

    //网格
    @Autowired
    private IMetricsService metricsService;

    @Autowired
    private IDictionariesService dictionariesService;

    //ES推送
    @Autowired
    private AlarmService alarmService;

    @Autowired
    private ElasticConfig elasticConfig;

    /**
     * 查看预警网格列表
     * @param ps
     * @param pn
     * @return
     */
    @RequestMapping(value = "index",method ={ RequestMethod.GET, RequestMethod.POST})
    public ModelAndView index(@RequestParam(name = "ps", defaultValue = "10") int ps,
                              @RequestParam(name = "pn", defaultValue = "1") int pn){
        PageInfo<Grid> pageInfo = new PageInfo<Grid>();
        pageInfo.setPageSize(ps < 10 ? ps : 10);
        pageInfo.setPageNum(pn);
        List<Grid> gridList = gridService.queryGridList(pageInfo);
        return new ModelAndView("grid/index").addObject("gridList", gridList)
                .addObject("pageInfo",((Page<Grid>) gridList).toPageInfo());
    }

    /**
     * 跳转修改或新增预警网格
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ModelAndView create(String grid_code,@RequestParam(name = "ps", defaultValue = "10") int ps,
                               @RequestParam(name = "pn", defaultValue = "1") int pn){
        Grid grid = new Grid();
        List<Dictionaries> dictionariesList = dictionariesService.queryDictionariesList(null);
        List<Metrics> metricsList = metricsService.queryMetricsList(null);
        String actionType = "create";
        return new ModelAndView("grid/create").addObject("actionType",actionType)
                .addObject("grid",grid)
                .addObject("metricsList",metricsList);
    }

    @RequestMapping(value = "update", method = RequestMethod.GET)
    public ModelAndView update(String grid_code,@RequestParam(name = "ps", defaultValue = "10") int ps,
                               @RequestParam(name = "pn", defaultValue = "1") int pn){
        List<Dictionaries> dictionariesList = dictionariesService.queryDictionariesList(null);
        List<Metrics> metricsList = metricsService.queryMetricsList(null);
        Grid grid = gridService.queryDetailByCode(grid_code);
        List<Units>  unitsList = gridService.queryUnitsListByCode(grid_code);
        return new ModelAndView("grid/update").addObject("dictionariesList",dictionariesList)
                .addObject("grid",grid).addObject("unitsList",unitsList)
                .addObject("metricsList",metricsList).addObject("grid_code",grid_code);
    }



    @RequestMapping(value = "unitsDetail",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult unitsDetail(HttpServletRequest request){

        JSONResult jsonResult = new JSONResult();
        String units_id = request.getParameter("units_id");

        Units units = gridService.queryUnitsDetail(units_id);

        jsonResult.setData(units);

        return jsonResult;

    }


    /**
     * 新增修改预警网格
     * @param request
     * @return
     */
    @RequestMapping(value = "create",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult createSubmit(HttpServletRequest request, @ModelAttribute Grid grid){
        String actionType = request.getParameter("actionType");
        JSONResult jsonResult = new JSONResult();
        try
        {
            if ("create".equals(actionType))
            {
                //新增
                grid.setGrid_code(DateFormatUtils.format(new Date(), "yyyyMMddHHmmssS"));
                int count = gridService.addGrid(grid);
                if(count > 0)
                {
                    jsonResult.setSuccessInfo("新增预警网格成功！");
                }else
                {
                    jsonResult.setFailInfo("新增预警网格失败，请稍后再试！");
                }
            }else if ("modfiy".equals(actionType))
            {
                //修改
                int count = gridService.updateGrid(grid);
                if(count > 0 )
                {
                    jsonResult.setSuccessInfo("修改预警网格成功！");
                }else
                {
                    jsonResult.setFailInfo("修改预警网格失败，请稍后再试！");
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
     * 根据ID删除预警网格
     * @param request
     * @return
     */
    @RequestMapping(value = "deleteGrid",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult deleteGrid(HttpServletRequest request){
        JSONResult jsonResult = new JSONResult();
        String grid_code = request.getParameter("grid_code");
        if (grid_code !=null && !grid_code.equals(""))
        {
            try {
                int count = gridService.deleteGrid(grid_code);
                if(count > 0)
                {
                    jsonResult.setSuccessInfo("删除预警网格成功！");
                }else
                {
                    jsonResult.setFailInfo("删除预警网格失败，请稍后再试！");
                }
            }catch (Exception e)
            {
                e.printStackTrace();
                jsonResult.setFailInfo("删除预警网格异常，请稍后再试！");
            }
        }else
        {
            jsonResult.setFailInfo("删除预警网格参数异常，请稍后再试！");
        }

        return jsonResult;
    }



    /**
     * 新增修改预警单元格
     * @param request
     * @return
     */
    @RequestMapping(value = "createUnits",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult createUnits(HttpServletRequest request,@ModelAttribute Units units){
        JSONResult jsonResult = new JSONResult();
        try
        {
            String units_id = units.getUnits_id();
            if(StringUtils.isEmpty(units_id))
            {
                //新增
                units.setUnits_id(DateFormatUtils.format(new Date(), "yyyyMMddHHmmssS"));
                int count = gridService.addUnits(units);
                if(count > 0)
                {
                    jsonResult.setSuccessInfo("新增预警单元格成功！");
                }else
                {
                    jsonResult.setFailInfo("新增预警单元格失败，请稍后再试！");
                }
            }else
            {
                //修改
                int count = gridService.updateUnits(units);
                if(count > 0 )
                {
                    jsonResult.setSuccessInfo("修改预警单元格成功！");
                }else
                {
                    jsonResult.setFailInfo("修改预警单元格失败，请稍后再试！");
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
     * 根据ID删除预警网格
     * @param request
     * @return
     */
    @RequestMapping(value = "deleteUnits",method = RequestMethod.POST)
    @ResponseBody
    public JSONResult deleteUnits(HttpServletRequest request){
        JSONResult jsonResult = new JSONResult();
        String units_id = request.getParameter("units_id");
        if (units_id !=null && !units_id.equals(""))
        {
            try {
                int count = gridService.deleteUnits(units_id);
                if(count > 0)
                {
                    jsonResult.setSuccessInfo("删除预警单元格成功！");
                }else
                {
                    jsonResult.setFailInfo("删除预警单元格失败，请稍后再试！");
                }
            }catch (Exception e)
            {
                e.printStackTrace();
                jsonResult.setFailInfo("删除预警单元格异常，请稍后再试！");
            }
        }else
        {
            jsonResult.setFailInfo("删除预警单元格参数异常，请稍后再试！");
        }

        return jsonResult;
    }


    @RequestMapping("appEs")
    @ResponseBody
    public JSONResult appEs(String grid_code) throws JsonProcessingException {
        JSONResult jsonResult = new JSONResult();
        Grid grid = gridService.queryDetailByCode(grid_code);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String json = objectMapper.writeValueAsString(grid);
        int status = alarmService.postCommonEs(json,elasticConfig.getThresholdType(),grid.getGrid_code());
        if (status  >= 200 && status <300)
        {
            jsonResult.setSuccessInfo("应用成功");
        }else
        {
            jsonResult.setSuccessInfo("应用失败");
        }
        return jsonResult;
    }


}
