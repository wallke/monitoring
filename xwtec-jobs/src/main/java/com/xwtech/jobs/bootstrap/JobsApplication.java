package com.xwtech.jobs.bootstrap;

import com.xwtech.es.ESClientConfig;
import com.xwtech.es.ElasticConfig;
import com.xwtech.framework.shiro.client.ClientShiroConfig;
import com.xwtech.jobs.quartz.QuartzSchedulerConfig;

import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Import({
    Swagger2Config.class,
        MybatisConfig.class,
        ClientShiroConfig.class,
        //SignConfig.class,
        WebMvcConfiguration.class,
        QuartzSchedulerConfig.class,
        ESClientConfig.class,
        ElasticConfig.class,
        ZabbixConfig.class,
        KPIEsConfig.class
})
@ComponentScan({
        "com.xwtech.jobs.controller",
        "com.xwtech.jobs.service"
})
@EnableAutoConfiguration
@EnableTransactionManagement
public class JobsApplication  extends SpringBootServletInitializer {


    public static SpringApplication SPRING_APPLICATION;

    private static final Logger logger = LoggerFactory.getLogger(JobsApplication.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(JobsApplication.class);
    }

    public static void main(String[] args) {
        SPRING_APPLICATION = new SpringApplication(JobsApplication.class);
        SPRING_APPLICATION.run(args);
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


//http请求自动跳转https
//    @Bean
//    public EmbeddedServletContainerFactory servletContainer(
//            @Value("${server.port}") Integer port,
//            @Value("${server.http.port}") Integer httpPort) {
//        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {
//            @Override
//            protected void postProcessContext(Context context) {
//                SecurityConstraint securityConstraint = new SecurityConstraint();
//                securityConstraint.setUserConstraint("CONFIDENTIAL");
//                SecurityCollection collection = new SecurityCollection();
//                collection.addPattern("/*");
//                securityConstraint.addCollection(collection);
//                context.addConstraint(securityConstraint);
//            }
//        };
//
//
//        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//        connector.setScheme("http");
//        connector.setPort(httpPort);
//        connector.setSecure(false);
//        connector.setRedirectPort(port );
//
//        tomcat.addAdditionalTomcatConnectors(connector);
//
//        return tomcat;
//    }


//    配置文件默认https  代码手工配置http
//    @Bean
//    public Integer port() {
//        return SocketUtils.findAvailableTcpPort();
//    }
//
//    @Bean
//    public EmbeddedServletContainerFactory servletContainer() {
//        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
//        tomcat.addAdditionalTomcatConnectors(createStandardConnector());
//        return tomcat;
//    }
//
//    private Connector createStandardConnector() {
//        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//        connector.setPort(port());
//        return connector;
//    }


}
