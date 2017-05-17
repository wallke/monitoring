package com.xwtech.omweb.controller.monitoring;

import com.xwtech.es.DateFormatUtil;
import com.xwtech.es.model.DateEnum;
import com.xwtech.es.model.DateHistogramBean;
import com.xwtech.es.model.DateRange;
import com.xwtech.es.service.ApplicationService;
import com.xwtech.framework.web.result.JSONResult;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.*;

/**
 * Created by zhangq on 2017/3/25.
 */
@Controller
@RequestMapping("omweb/crash")
public class CrashController {

    private final static Logger logger = LoggerFactory.getLogger(CrashController.class);


    @Autowired
    ApplicationService applicationService;


    @RequestMapping(value = {"", "/", "index"})
    public ModelAndView index(HttpServletRequest request) {

        ModelAndView modelAndView = new ModelAndView("monitoring/crash/index");

        return modelAndView;

    }


    /**
     * @param dateType
     * @param date
     * @param techType
     * @return
     */
    @RequestMapping(value = {"count"})
    @ResponseBody
    public JSONResult count(
            String dateType,
            String date,
            String techType) {

        JSONResult jsonResult = new JSONResult();

        DateRange dateRange = parseDateRange(dateType, date);

        //查询统计信息
        List<DateHistogramBean> list = applicationService.getCrashDateHistogram(dateRange, techType);

        jsonResult.setData(list);

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



    @RequestMapping(value = {"vers"})
    @ResponseBody
    public JSONResult vers(String system) {

        JSONResult jsonResult = new JSONResult();
        //DateRange dateRange = parseDateRange(dateType, date);
        //查询统计信息
        List<String> list = applicationService.getCrashAppVers(system);

        jsonResult.setData(list);

        return jsonResult;

    }



    @RequestMapping(value = {"distribution"})
    @ResponseBody
    public JSONResult distribution(
            String dateType,
            String date,
            String techType,
            String type) {

        JSONResult jsonResult = new JSONResult();

        DateRange dateRange = parseDateRange(dateType, date);

        List<Map<String,Object>> list = new ArrayList<>();

        if("count".equals(type)){
            List<DateHistogramBean> list1 = applicationService.getCrashDateHistogram(dateRange, techType);
            Map<String,Object> map = new HashMap<>();
            map.put("count",list1);
            list.add(map);
        }else {
            //查询统计信息
            list = applicationService.getCrashDateHistogramByType(dateRange, techType,type);
        }

        jsonResult.setData(list);
        return jsonResult;

    }


    @RequestMapping(value = {"details"})
    @ResponseBody
    public JSONResult details(
            String dateType,
            String date,
            String techType,
            String ver,
            String key,
            String type,
            @RequestParam(name = "ps", defaultValue = "10") int ps,
            @RequestParam(name = "pn", defaultValue = "1") int pn) {

        JSONResult jsonResult = new JSONResult();

        DateRange dateRange = parseDateRange(dateType, date);

        //查询统计信息
        Map<String, Object> map = applicationService.getCrashDetails(dateRange, techType,ver,key ,(pn - 1) * ps, ps);

        Map<String, Object> page = new HashMap<>();

        Long total = Long.parseLong(map.get("total").toString());
        Long pages = total / ps + ((total % ps == 0) ? 0 : 1);

        page.put("total", total);
        page.put("pn", pn);
        page.put("ps", ps);
        page.put("pages", pages);

        Map<String, Object> result = new HashMap<>();
        result.put("page", page);
        result.put("data", map.get("list"));
        jsonResult.setData(result);

        return jsonResult;

    }



    @RequestMapping(value = {"all"})
    public ModelAndView all(HttpServletRequest request) {

        ModelAndView modelAndView = new ModelAndView("monitoring/crash/all");

        return modelAndView;

    }


}
