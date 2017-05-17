package com.xwtech.omweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Administrator on 2017/3/16 0016.
 * 监听目标
 */
public class MonitorTarget {


    //监听目标标识(主键)
    private String target_code;
    //监听目标名称
    private String target_name;
    //监听模板标识
    @JsonIgnore
    private String monitor_template_id;
    //监听模板名称
    private String template_name;
    //每次统计的间隔时间，单位是秒
    private int stat_interval;
    //应用程序名称
    private String application_name;
    //应用程序版本号
    private String application_ver;
    //上级应用程序名称
    private String last_application_name;
    //主机IP
    private String host_ip;
    //主机端口号
    private String host_port;
    //节点编码
    private String node_code;
    //上级节点编码
    private String last_node_code;
    //告警方案的标识
    private String alert_scheme_code;
    //执行状态
    private String status;
    //微环境编码
    private String envir_code;
    @JsonProperty("interval")
    private  int target_interval;
    //预警网格编码
    @JsonIgnore
    private String grid_code_list;
    @JsonIgnore
    private String[] grid_code;
    List<Grid> metrics;

    public String[] getGrid_code() {
        return grid_code;
    }

    public void setGrid_code(String[] grid_code) {
        this.grid_code = grid_code;
    }

    public List<Grid> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Grid> metrics) {
        this.metrics = metrics;
    }

    public String getGrid_code_list() {
        return grid_code_list;
    }

    public void setGrid_code_list(String grid_code_list) {
        this.grid_code_list = grid_code_list;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEnvir_code() {
        return envir_code;
    }

    public void setEnvir_code(String envir_code) {
        this.envir_code = envir_code;
    }

    public String getMonitor_template_id() {
        return monitor_template_id;
    }

    public void setMonitor_template_id(String monitor_template_id) {
        this.monitor_template_id = monitor_template_id;
    }

    public int getTarget_interval() {
        return target_interval;
    }

    public void setTarget_interval(int target_interval) {
        this.target_interval = target_interval;
    }

    public String getTarget_code() {
        return target_code;
    }

    public void setTarget_code(String target_code) {
        this.target_code = target_code;
    }

    public String getTarget_name() {
        return target_name;
    }

    public void setTarget_name(String target_name) {
        this.target_name = target_name;
    }

    public String getTemplate_name() {
        return template_name;
    }

    public void setTemplate_name(String template_name) {
        this.template_name = template_name;
    }

    public int getStat_interval() {
        return stat_interval;
    }

    public void setStat_interval(int stat_interval) {
        this.stat_interval = stat_interval;
    }

    public String getApplication_name() {
        return application_name;
    }

    public void setApplication_name(String application_name) {
        this.application_name = application_name;
    }

    public String getApplication_ver() {
        return application_ver;
    }

    public void setApplication_ver(String application_ver) {
        this.application_ver = application_ver;
    }

    public String getLast_application_name() {
        return last_application_name;
    }

    public void setLast_application_name(String last_application_name) {
        this.last_application_name = last_application_name;
    }

    public String getHost_ip() {
        return host_ip;
    }

    public void setHost_ip(String host_ip) {
        this.host_ip = host_ip;
    }

    public String getHost_port() {
        return host_port;
    }

    public void setHost_port(String host_port) {
        this.host_port = host_port;
    }

    public String getNode_code() {
        return node_code;
    }

    public void setNode_code(String node_code) {
        this.node_code = node_code;
    }

    public String getLast_node_code() {
        return last_node_code;
    }

    public void setLast_node_code(String last_node_code) {
        this.last_node_code = last_node_code;
    }

    public String getAlert_scheme_code() {
        return alert_scheme_code;
    }

    public void setAlert_scheme_code(String alert_scheme_code) {
        this.alert_scheme_code = alert_scheme_code;
    }
}
