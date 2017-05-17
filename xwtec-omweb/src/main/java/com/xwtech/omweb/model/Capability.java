package com.xwtech.omweb.model;

import java.util.List;

/**
 * Created by Administrator on 2017/2/22 0022.
 */
public class Capability {

    private  String update_interval;
    private  String sampling_rate;
    private  String logsBuffer_max_size;
    private  String buildLog_thread_size;

    List<SDKConfHeart> heart;

    public String getUpdate_interval() {
        return update_interval;
    }

    public void setUpdate_interval(String update_interval) {
        this.update_interval = update_interval;
    }

    public String getSampling_rate() {
        return sampling_rate;
    }

    public void setSampling_rate(String sampling_rate) {
        this.sampling_rate = sampling_rate;
    }

    public String getLogsBuffer_max_size() {
        return logsBuffer_max_size;
    }

    public void setLogsBuffer_max_size(String logsBuffer_max_size) {
        this.logsBuffer_max_size = logsBuffer_max_size;
    }

    public String getBuildLog_thread_size() {
        return buildLog_thread_size;
    }

    public void setBuildLog_thread_size(String buildLog_thread_size) {
        this.buildLog_thread_size = buildLog_thread_size;
    }

    public List<SDKConfHeart> getHeart() {
        return heart;
    }

    public void setHeart(List<SDKConfHeart> heart) {
        this.heart = heart;
    }
}
