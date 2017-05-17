package com.xwtech.framework.sign;

import com.xwtech.framework.sign.filters.SignFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * Created by zq on 16/8/30.
 */
@Configuration
public class SignConfig {

    @Bean(name = "signFilter")
    public Filter signFilter() {
        return new SignFilter();
    }


    @Bean
    public FilterRegistrationBean filterRegistrationBean(Filter signFilter) {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(signFilter);
        filterRegistration.setEnabled(true);
        filterRegistration.addUrlPatterns("/*");
        filterRegistration.setName("signFilter");
        filterRegistration.setOrder(1);
        return filterRegistration;
    }


}
