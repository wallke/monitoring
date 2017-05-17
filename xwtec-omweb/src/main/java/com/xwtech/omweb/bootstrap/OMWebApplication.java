package com.xwtech.omweb.bootstrap;

import com.xwtech.es.ESClientConfig;
import com.xwtech.framework.shiro.client.ClientShiroConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
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

@Import({
        MybatisConfig.class,
        ClientShiroConfig.class,
        //SignConfig.class,
        WebMvcConfiguration.class,
        ESClientConfig.class
})
@ComponentScan({
        "com.xwtech.omweb.controller",
        "com.xwtech.omweb.service"})
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableCaching
@Configuration
public class OMWebApplication {


    private static final Logger logger = LoggerFactory.getLogger(OMWebApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(OMWebApplication.class, args);
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
