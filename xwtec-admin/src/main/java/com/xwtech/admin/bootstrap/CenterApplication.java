package com.xwtech.admin.bootstrap;

import com.xwtech.admin.SpringUtils;
import com.xwtech.admin.shiro.ShiroConfiguration;
import com.xwtech.framework.web.bean.config.HttpConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.net.UnknownHostException;

@Configuration
@EnableAutoConfiguration
@EnableCaching
@EnableTransactionManagement
@Import({
        MybatisConfig.class,
        WebMvcConfiguration.class,
        ShiroConfiguration.class, HttpConfig.class

})
@ComponentScan(
        {
                "com.xwtech.admin.controller",
                "com.xwtech.admin.service"
        })
@ComponentScan(basePackageClasses = {SpringUtils.class})
public class CenterApplication extends SpringBootServletInitializer {


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CenterApplication.class);
    }


    public static void main(String[] args) {

        SpringApplication.run(CenterApplication.class, args);
    }


    @Bean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(
            RedisConnectionFactory redisConnectionFactory)
            throws UnknownHostException {
        RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        return template;
    }

}
