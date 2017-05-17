package com.xwtech.omweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/22 0022.
 */
public class SdkJson {

    @JsonIgnore
    private String conf_id;

    private boolean collect_all;

    private String log_ver;

    private String appServer_name;

    private String appServer_ver;

    private Separator separator;

    private  Capability capability;

    private List<SDKConfHost> hosts = new ArrayList<SDKConfHost>();

    private List<SDKConfEnv> envirs;

    public String getConf_id() {
        return conf_id;
    }

    public void setConf_id(String conf_id) {
        this.conf_id = conf_id;
    }

    public boolean isCollect_all() {
        return collect_all;
    }

    public void setCollect_all(boolean collect_all) {
        this.collect_all = collect_all;
    }

    public String getLog_ver() {
        return log_ver;
    }

    public void setLog_ver(String log_ver) {
        this.log_ver = log_ver;
    }

    public String getAppServer_name() {
        return appServer_name;
    }

    public void setAppServer_name(String appServer_name) {
        this.appServer_name = appServer_name;
    }

    public String getAppServer_ver() {
        return appServer_ver;
    }

    public void setAppServer_ver(String appServer_ver) {
        this.appServer_ver = appServer_ver;
    }

    public Separator getSeparator() {
        return separator;
    }

    public void setSeparator(Separator separator) {
        this.separator = separator;
    }

    public Capability getCapability() {
        return capability;
    }

    public void setCapability(Capability capability) {
        this.capability = capability;
    }

    public List<SDKConfHost> getHosts() {
        return hosts;
    }

    public void setHosts(List<SDKConfHost> hosts) {
        this.hosts = hosts;
    }

    public List<SDKConfEnv> getEnvirs() {
        return envirs;
    }

    public void setEnvirs(List<SDKConfEnv> envirs) {
        this.envirs = envirs;
    }


}


