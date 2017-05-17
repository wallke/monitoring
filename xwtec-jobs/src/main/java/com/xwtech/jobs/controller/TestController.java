package com.xwtech.jobs.controller;

import com.xwtech.es.service.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import xwtec.servercm.ServerCM;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangq on 2017/2/4.
 */
@RestController

public class TestController {


    final static Logger logger = LoggerFactory.getLogger(TestController.class);



    @Autowired
    RestTemplate restTemplate;

    /**
     * 获取应用信息
     *
     * @return SECRET
     */
    @RequestMapping(value = "get1", method = {RequestMethod.GET})
    public ResponseEntity<Map<String,Object>> get1(HttpServletRequest request) {


        ServerCM.startRequest("get1",
                "IOS",
                request.getRemoteHost(),
                String.valueOf(request.getRemotePort()) );
        Map<String,Object> result = new HashMap<>();
        try{
            logger.info("get1");
            result.put("result", "111");
            ServerCM.exit();
        }catch (Exception ex){
            ServerCM.exit(ex);
        }


        return new ResponseEntity<>(result, HttpStatus.OK);
    }



    /**
     * 获取应用信息
     *
     * @return SECRET
     */
    @RequestMapping(value = "get2", method = {RequestMethod.GET})
    public ResponseEntity<Map<String,Object>> get2(
            HttpServletRequest request) {

        logger.info("get2");
        ServerCM.startRequest("get2","IOS",request.getRemoteHost(),String.valueOf(request.getRemotePort()) );
        Map<String,Object> result = new HashMap<>();
        try{
            Map param = new HashMap();
            param.put("phone", "13688909090");
            param.put("aaa", "1368890aa");
            param.put("aabba", "1368890abba");
            ServerCM.setCustmInfos(param);

            getInfo();
            getUrl();
            result.put("result", "12345");
            String a = null;
            if(isThrowException(30)){
                a.toString();
            }
            ServerCM.exit();

        }catch (Exception ex){
            ServerCM.exit(ex);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    void getInfo(){

        ServerCM.startLocal("getInfo");
        getInfo1();
        getRemote();

        ServerCM.exit();

    }


    void getInfo1(){

        ServerCM.startLocal("getInfo1");

        //getRemote();

        ServerCM.exit();

    }


    void getRemote(){
        ServerCM.request("app_xwtec_admin","checkUser");

        ServerCM.response("200");
    }


    void getUrl(){
        ServerCM.startLocal("getUrl");
        //TODO 埋点测试



        try {
            ServerCM.request("app_xwtec_admin","get1");

            Map param = new HashMap();
            param.put("appKey", "xxxsss");
            param.put("cm", ServerCM.httpRequestParam());

            MultiValueMap<String, Object> headers;
            headers = new LinkedMultiValueMap<>();
            headers.add("Accept", "application/json");
            headers.add("Content-Type", "application/json");

            HttpEntity<Map> request1 = new HttpEntity(param, headers);

            String a = null;
            if(isThrowException(30)){
                a.toString();
            }
            ResponseEntity<Map> responseEntity =
                    restTemplate.postForEntity( "http://127.0.0.1:8080/center/auth/get1", request1, Map.class);

            Object result = responseEntity.getBody().get("result");

            ServerCM.response(String.valueOf(responseEntity.getStatusCodeValue()));



        } catch (Exception ex) {
            ServerCM.response(ex);
            //ServerCM.exit(ex);
        }

        ServerCM.exit();

    }


    @Autowired
    ApplicationService testService;


    /**
     * 获取应用信息
     *
     * @return SECRET
     */
    @RequestMapping(value = "get3", method = {RequestMethod.GET})
    public ResponseEntity<Map<String,Object>> get3(
            HttpServletRequest request) {
        Calendar calendar = Calendar.getInstance();

        Date end = calendar.getTime();

        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date start = calendar.getTime();

        logger.info("get3");
        Map<String,Object> result = new HashMap<>();
        result.put("get3", testService.getLogs("6120b979414f49918965a5aec7d5b6a6"));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    boolean isThrowException(int rate){
        return (Math.random() * 100) <= rate;

    }


}
