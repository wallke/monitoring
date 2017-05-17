package com.xwtech.admin.bootstrap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ServletComponentScan(value = {"com.xwtech.admin.shiro.filters","com.xwtech.framework.shiro.filters"})
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    private static final Logger logger = LoggerFactory.getLogger(WebMvcConfiguration.class);


    @Value("${web.staticUrl}")
    String webStaticUrl;


    @Value("${shiro.client.globalAuthUrl}")
    String globalAuthUrl;


    /**
     * SpringBoot默认已经将classpath:/META-INF/resources/和classpath:/META-INF/resources/webjars/映射
     * 所以该方法不需要重写，如果在SpringMVC中，可能需要重写定义（我没有尝试）
     * 重写该方法需要 extends WebMvcConfigurerAdapter
     *
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.setOrder(Integer.MIN_VALUE);
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("/")
                .setCachePeriod(0);

        super.addResourceHandlers(registry);
    }

//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//
//        logger.info("自定义json格式化");
//        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//        final ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//
//        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//        converter.setObjectMapper(objectMapper);
//        converters.add(converter);
//        super.configureMessageConverters(converters);
//    }

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        logger.info("自定义json格式化");
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        converter.setObjectMapper(objectMapper);
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        mediaTypes.add(MediaType.TEXT_HTML);
        mediaTypes.add(MediaType.TEXT_PLAIN);
        converter.setSupportedMediaTypes(mediaTypes);
        converters.add(converter);

        super.extendMessageConverters(converters);
    }


//    @Override
//    @Bean
//    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
//        RequestMappingHandlerMapping handlerMapping = new VersionRequestMappingHandlerMapping();
//        handlerMapping.setOrder(0);
//        handlerMapping.setInterceptors(getInterceptors());
//        return handlerMapping;
//    }



    @Override
    public void setServletContext(ServletContext servletContext) {

        servletContext.setAttribute("webStaticUrl", webStaticUrl);
        servletContext.setAttribute("globalAuthUrl", globalAuthUrl);

        super.setServletContext(servletContext);
    }


}
