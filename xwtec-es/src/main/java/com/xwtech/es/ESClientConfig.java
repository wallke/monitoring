package com.xwtech.es;

import org.elasticsearch.client.transport.TransportClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by zhangq on 2017/2/14.
 */
@Configuration
@ComponentScan(basePackages = {"com.xwtech.es.service"})
@Import( {
        ElasticConfig.class})
public class ESClientConfig {


    @Bean
    @ConfigurationProperties(prefix = "elasticsearch")
    public ESTransportClient getESClientFactory(){
        return new ESTransportClient();
    }


    @Bean
    @ConfigurationProperties(prefix = "elasticsearch")
    public ESConfig getESConfig(){

        return new ESConfig();
    }

}
