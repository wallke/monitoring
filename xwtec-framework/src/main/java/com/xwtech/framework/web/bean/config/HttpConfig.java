package com.xwtech.framework.web.bean.config;

import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by zhangq on 2017/1/18.
 */
@Configuration
public class HttpConfig {


    private static final Logger logger = LoggerFactory.getLogger(HttpConfig.class);

    @Bean
    @Primary
    RestTemplate restTemplate(
            HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory) {
        return new RestTemplate(httpComponentsClientHttpRequestFactory);
    }


    @Bean
    public Registry registry() {
        Registry registry = RegistryBuilder
                .create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", getSSLConnectionSocketFactory()).build();
        return registry;
    }


    @Bean
    @ConfigurationProperties(prefix = "httpclient.pool")
    public HttpClientConnectionManager httpClientConnectionManager(Registry registry) {
        return new PoolingHttpClientConnectionManager(registry);
    }


//    @Bean
//    @ConfigurationProperties(prefix = "httpclient")
//    public RequestConfig.Builder builder() {
//        return RequestConfig.custom();
//    }
//
//    @Bean
//    public RequestConfig requestConfig(RequestConfig.Builder builder) {
//        return builder.build();
//    }


    @Bean
    @Primary
    public HttpClientBuilder httpClientBuilder(HttpClientConnectionManager httpClientConnectionManager) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(httpClientConnectionManager);
        //httpClientBuilder.setDefaultRequestConfig();
        return httpClientBuilder;
    }


    @Bean
    @Primary
    public HttpClient httpClient(HttpClientBuilder httpClientBuilder) {
        return httpClientBuilder.build();
    }


    /**
     * spring http连接工厂
     * @param httpClient
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "httpclient")
    @Primary
    public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory(
            HttpClient httpClient) {
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }


    /**
     * SSLConnectionSocketFactory 信任所有
     * @return
     */
    SSLConnectionSocketFactory getSSLConnectionSocketFactory() {
        SSLContext sslContext = null;
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build();
        } catch (NoSuchAlgorithmException e) {
            logger.error("xwtec-framework get sslcontext ", e);
        } catch (KeyManagementException e) {
            logger.error("xwtec-framework get sslcontext ", e);
        } catch (KeyStoreException e) {
            logger.error("xwtec-framework get sslcontext ", e);
        }
        return new SSLConnectionSocketFactory(sslContext, new String[]{"TLSv1"}, null, NoopHostnameVerifier.INSTANCE);

    }

}
