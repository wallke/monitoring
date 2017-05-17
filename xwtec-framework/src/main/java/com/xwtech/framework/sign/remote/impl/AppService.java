package com.xwtech.framework.sign.remote.impl;

import com.xwtech.framework.sign.properties.SignProperties;
import com.xwtech.framework.sign.remote.IAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zq on 16/8/30.
 */
@Service
public class AppService implements IAppService {

    final static Logger logger = LoggerFactory.getLogger(AppService.class);

    MultiValueMap<String, Object> headers;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    SignProperties signProperties;

    public AppService() {
        this.headers = new LinkedMultiValueMap<>();
        this.headers.add("Accept", "application/json");
        this.headers.add("Content-Type", "application/json");
    }

    @Override
    public String getSecret(String appKey) {
        logger.debug("client:getSecret:{}", appKey);
        Map param = new HashMap();
        param.put("appKey",appKey);
        HttpEntity<String> request = new HttpEntity(param, this.headers);
        try {
            ResponseEntity<Map> responseEntity =
                    restTemplate.postForEntity(signProperties.getGlobalAuthUrl() + "getAppSecret", request, Map.class);
            Object result= null;
            if(responseEntity.getBody() != null && (result = responseEntity.getBody().get("result")) != null){
                return result.toString();
            }
            return null;
        } catch (Exception ex) {
            logger.error("client:getSecret getAppSecret 请求异常 ", ex);
        }
        return null;

    }
}
