package com.xwtech.admin.bootstrap;

import com.github.pagehelper.PageHelper;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@EnableAutoConfiguration
@MapperScan({"com.xwtech.admin.dao"})
public class MybatisConfig {

    private static final Logger logger = LoggerFactory.getLogger(MybatisConfig.class);

    @Bean
    public PageHelper pageHelper() {
        logger.info("注册MyBatis分页插件PageHelper");
        PageHelper pageHelper = new PageHelper();
        Properties p = new Properties();
        p.setProperty("offsetAsPageNum", "true");
        p.setProperty("rowBoundsWithCount", "true");
        p.setProperty("reasonable", "true");

        //p.setProperty("supportMethodsArguments", "true");

        pageHelper.setProperties(p);
        return pageHelper;
    }


}
