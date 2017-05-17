package com.xwtech.omweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Administrator on 2017/3/10 0010.
 * 统计指标
 */
public class Metrics {

    //主键
    @JsonIgnore
    private  String metric_id;

    //指标的标识
    private String metric;

    //指标的显示名称
    private String name;

    public String getMetric_id() {
        return metric_id;
    }

    public void setMetric_id(String metric_id) {
        this.metric_id = metric_id;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
