package com.xwtech.omweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/20 0020.
 * 采集主机控制信息
 */
public class SDKConfHost {
    //主机控制ID

    private  String hostId;
    //配置ID
    private  String confId;
    //主机IP
    private  String ip;
    //主机端口
    private  String port;
    //主机名称
    private  String hostName;
    //IP开关
    private  String collectIp;
    //端口开关
    private  String collectPort;

    private String collect_ip;
    private String collect_port;
    private String host_name;
    @JsonIgnore
    private String host_id;

    private List<SDKConfHostNode> nodes;


    public List<SDKConfHostNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<SDKConfHostNode> nodes) {
        this.nodes = nodes;
    }

    public String getCollect_ip() {
        return collect_ip;
    }

    public void setCollect_ip(String collect_ip) {
        this.collect_ip = collect_ip;
    }

    public String getCollect_port() {
        return collect_port;
    }

    public void setCollect_port(String collect_port) {
        this.collect_port = collect_port;
    }

    public String getHost_name() {
        return host_name;
    }

    public void setHost_name(String host_name) {
        this.host_name = host_name;
    }

    public String getHost_id() {
        return host_id;
    }

    public void setHost_id(String host_id) {
        this.host_id = host_id;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getConfId() {
        return confId;
    }

    public void setConfId(String confId) {
        this.confId = confId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getCollectIp() {
        return collectIp;
    }

    public void setCollectIp(String collectIp) {
        this.collectIp = collectIp;
    }

    public String getCollectPort() {
        return collectPort;
    }

    public void setCollectPort(String collectPort) {
        this.collectPort = collectPort;
    }
}
