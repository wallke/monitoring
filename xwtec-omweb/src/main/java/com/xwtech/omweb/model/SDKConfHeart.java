package com.xwtech.omweb.model;

/**
 * Created by Administrator on 2017/2/20 0020.
 * 采集心跳控制信息
 */
public class SDKConfHeart {
    //心跳控制ID
    private String heartId;
    //配置ID
    private String confId;
    //心跳日志采集类型
    private String heartType;
    //心跳间隔秒数
    private String heartInterval;
    //心跳状态
    private String collectHeart;

    private  boolean collect_heart;
    private String heart_type;
    private String heart_interval;

    public boolean isCollect_heart() {
        return collect_heart;
    }

    public void setCollect_heart(boolean collect_heart) {
        this.collect_heart = collect_heart;
    }

    public String getHeart_type() {
        return heart_type;
    }

    public void setHeart_type(String heart_type) {
        this.heart_type = heart_type;
    }

    public String getHeart_interval() {
        return heart_interval;
    }

    public void setHeart_interval(String heart_interval) {
        this.heart_interval = heart_interval;
    }

    public String getHeartId() {
        return heartId;
    }

    public void setHeartId(String heartId) {
        this.heartId = heartId;
    }

    public String getConfId() {
        return confId;
    }

    public void setConfId(String confId) {
        this.confId = confId;
    }

    public String getHeartType() {
        return heartType;
    }

    public void setHeartType(String heartType) {
        this.heartType = heartType;
    }

    public String getHeartInterval() {
        return heartInterval;
    }

    public void setHeartInterval(String heartInterval) {
        this.heartInterval = heartInterval;
    }

    public String getCollectHeart() {
        return collectHeart;
    }

    public void setCollectHeart(String collectHeart) {
        this.collectHeart = collectHeart;
    }
}
