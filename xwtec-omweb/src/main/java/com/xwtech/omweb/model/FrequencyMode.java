package com.xwtech.omweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Administrator on 2017/3/13 0013.
 * 变频模式
 */
public class FrequencyMode {


    private String frequency_code;
    private String frequency_name;
    @JsonIgnore
    private String interval;
    //间隔时间 数组逗号分隔
    private  int[] interval_seq;
    //间隔时间队尾执行的次数，0代表任意多
    private int last_repeat;
    //最大等待时间
    private int wait_time;
    //最小等待时间
    private int pause_time;

    public int getPause_time() {
        return pause_time;
    }

    public void setPause_time(int pause_time) {
        this.pause_time = pause_time;
    }

    public int getWait_time() {
        return wait_time;
    }

    public void setWait_time(int wait_time) {
        this.wait_time = wait_time;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public int[] getInterval_seq() {
        return interval_seq;
    }

    public void setInterval_seq(int[] interval_seq) {
        this.interval_seq = interval_seq;
    }

    public String getFrequency_code() {
        return frequency_code;
    }

    public void setFrequency_code(String frequency_code) {
        this.frequency_code = frequency_code;
    }

    public String getFrequency_name() {
        return frequency_name;
    }

    public void setFrequency_name(String frequency_name) {
        this.frequency_name = frequency_name;
    }

    public int getLast_repeat() {
        return last_repeat;
    }

    public void setLast_repeat(int last_repeat) {
        this.last_repeat = last_repeat;
    }
}
