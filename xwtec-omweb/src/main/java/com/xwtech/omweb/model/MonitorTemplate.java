package com.xwtech.omweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Administrator on 2017/3/16 0016.
 * 监听模板
 */
public class MonitorTemplate {


    //监听模板主键
    @JsonIgnore
    private String monitor_template_id;
    //监听目标模板的名称 (监听目标查询脚本在ES中的ID)
    private String template_name;
    //从那个日志中进行分析
    private String log_type;
    //监听目标查询脚本在ES中的索引
    @JsonProperty("index")
    private String es_index;
    //监听目标查询脚本在ES中的类型
    @JsonProperty("type")
    private String es_type;

    public String getMonitor_template_id() {
        return monitor_template_id;
    }

    public void setMonitor_template_id(String monitor_template_id) {
        this.monitor_template_id = monitor_template_id;
    }

    public String getTemplate_name() {
        return template_name;
    }

    public void setTemplate_name(String template_name) {
        this.template_name = template_name;
    }

    public String getLog_type() {
        return log_type;
    }

    public void setLog_type(String log_type) {
        this.log_type = log_type;
    }

    public String getEs_index() {
        return es_index;
    }

    public void setEs_index(String es_index) {
        this.es_index = es_index;
    }

    public String getEs_type() {
        return es_type;
    }

    public void setEs_type(String es_type) {
        this.es_type = es_type;
    }
}
