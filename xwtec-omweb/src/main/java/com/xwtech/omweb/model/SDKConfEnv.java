package com.xwtech.omweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Administrator on 2017/2/20 0020.
 * 采集控制配置bean
 */
public class SDKConfEnv {
    //微环境控制ID
    private String envId;
    //配置ID
    private String confId;
    //微环境ID
    private String envirId;
    //微环境名称
    private String envirName;
    //采集开关
    private String collectEnvir;
    //环境下的所有节点名称
    @JsonIgnore
    private String nodeCodes;

    private boolean collect_envir;
    private String envir_ID;
    private String envir_name;
    private String[] node_codes;

    public String getEnvId() {
        return envId;
    }

    public void setEnvId(String envId) {
        this.envId = envId;
    }

    public String getConfId() {
        return confId;
    }

    public void setConfId(String confId) {
        this.confId = confId;
    }

    public String getEnvirId() {
        return envirId;
    }

    public void setEnvirId(String envirId) {
        this.envirId = envirId;
    }

    public String getEnvirName() {
        return envirName;
    }

    public void setEnvirName(String envirName) {
        this.envirName = envirName;
    }

    public String getCollectEnvir() {
        return collectEnvir;
    }

    public void setCollectEnvir(String collectEnvir) {
        this.collectEnvir = collectEnvir;
    }

    public String getNodeCodes() {
        return nodeCodes;
    }

    public void setNodeCodes(String nodeCodes) {
        this.nodeCodes = nodeCodes;
    }

    public boolean isCollect_envir() {
        return collect_envir;
    }

    public void setCollect_envir(boolean collect_envir) {
        this.collect_envir = collect_envir;
    }

    public String getEnvir_ID() {
        return envir_ID;
    }

    public void setEnvir_ID(String envir_ID) {
        this.envir_ID = envir_ID;
    }

    public String getEnvir_name() {
        return envir_name;
    }

    public void setEnvir_name(String envir_name) {
        this.envir_name = envir_name;
    }

    public String[] getNode_codes() {
        return node_codes;
    }

    public void setNode_codes(String[] node_codes) {
        this.node_codes = node_codes;
    }
}
