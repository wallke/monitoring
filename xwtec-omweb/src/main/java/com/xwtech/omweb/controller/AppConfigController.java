package com.xwtech.omweb.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xwtech.es.ElasticConfig;
import com.xwtech.es.service.AlarmService;
import com.xwtech.framework.web.result.JSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Administrator on 2017/3/31 0031.
 * 配置应用
 */
@Controller
@RequestMapping("omweb/appConfig")
public class AppConfigController {

    @Autowired
    private AlarmService alarmService;


    @Autowired
    ElasticConfig elasticConfig;

    public static List<String> listString = new ArrayList<String>();


    @RequestMapping("index")
    public ModelAndView index(){
        return new ModelAndView("appConfig/index");
    }

    @RequestMapping("appEs")
    @ResponseBody
    public JSONResult appEs() throws ExecutionException, InterruptedException {
        JSONResult jsonResult = new JSONResult();
        listString.add("capture");
        listString.add("notifier");
        listString.add("trigger");
        listString.add("parliament");
        listString.add("deliver");
        try {
            for (String model : listString) {
                this.getUpdateTime(model);
            }
            jsonResult.setSuccessInfo("应用成功");
        }catch (Exception e)
        {
            e.printStackTrace();
            jsonResult.setSuccessInfo("应用异常");
        }
        return  jsonResult;

    }

    /**
     * 封装JSON串
     * @return
     */
    public void getUpdateTime(String model) throws ExecutionException, InterruptedException {
        JSONObject jsonObject = new JSONObject();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        String format = df.format(new Date());
        jsonObject.put("update_interval",10);
        jsonObject.put("update_time",format);
        jsonObject.put("module",model);
        String s = jsonObject.toString();
        alarmService.updateDate(s,elasticConfig.getUpdateType(),model);
    }
}
