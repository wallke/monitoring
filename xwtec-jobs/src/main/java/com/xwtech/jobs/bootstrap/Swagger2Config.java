package com.xwtech.jobs.bootstrap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import org.springframework.core.env.Environment;
import static springfox.documentation.builders.PathSelectors.regex;

/**
 * Created by Jason on 16/7/23.
 */
@Configuration
@EnableSwagger2
public class Swagger2Config  {

    @Bean
    public Docket createRestApi(Environment environment) {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo(environment))
                .groupName("quartz-jobs-api")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xwtech.jobs.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo(Environment environment) {
       String dev = environment.getProperty("dev");

        Contact contact = new Contact(dev,"","jijunliang@xwtec.cn");
        return new ApiInfoBuilder()
                .title("欣网定时任务调度中心")
                .description("欣网定时任务调度中心")
                .termsOfServiceUrl("http://www.xwtec.cn")
                .contact(contact)
                .version("1.0")
                .build();
    }


}
