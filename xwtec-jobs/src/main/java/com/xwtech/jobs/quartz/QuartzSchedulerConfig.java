package com.xwtech.jobs.quartz;

import com.xwtech.jobs.WebApp;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by zhangq on 2017/1/9.
 */
@Configuration
@EnableScheduling
@EnableAutoConfiguration
@ComponentScan
public class QuartzSchedulerConfig {


    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    @Bean
    @Primary
    public HikariDataSource dataSource(DataSourceProperties properties) {
        return (HikariDataSource) properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(
            JobFactory jobFactory,
            HikariDataSource dataSource) throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();

        factory.setDataSource(dataSource);
        //factory.setNonTransactionalDataSource();
        factory.setAutoStartup(true);
        factory.setWaitForJobsToCompleteOnShutdown(true);
        //factory.setApplicationContext(SpringUtils.getApplicationContext());
        factory.setApplicationContextSchedulerContextKey("applicationContextKey");

        factory.setOverwriteExistingJobs(true);

        // 延时启动
        //factory.setStartupDelay(20);

        // 加载quartz数据源配置
        factory.setQuartzProperties(quartzProperties());

        // 自定义Job Factory，用于Spring注入
        factory.setJobFactory(jobFactory);

        factory.setSchedulerName(WebApp.SchedulerName);



        return factory;
    }

//
//    @Bean
//    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean) {
//
//        return schedulerFactoryBean.getScheduler();
//    }

    /**
     * 加载quartz数据源配置
     *
     * @return
     * @throws IOException
     */
    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

}
