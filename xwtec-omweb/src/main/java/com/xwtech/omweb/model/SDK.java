package com.xwtech.omweb.model;

/**
 * Created by Administrator on 2017/2/20 0020.
 * SDK基础modelBean
 */
public class SDK {
    //配置ID
    private  String confId;
    //应用ID
    private  String appId;
    //版本号
    private  String logVer;
    //服务端名称
    private  String appServerName;
    //服务端版本
    private  String appServerVer;
    //是否全部采集（0-否;1-是）
    private  String collectAll;
    //一级分隔符
    private  String levelOne;
    //二级分隔符
    private  String levelTwo;
    //配置更新间隔
    private  String updateInterval;
    //采样率
    private  String samplingRate;
    //日志缓存最大值
    private  String logsBufferMaxSize;
    //构造日志线程数
    private  String buildLogThreadSize;
    //是否启用（0-否;1-是）
    private  String flag;
    //创建人ID
    private  String createId;
    //创建人名称
    private  String createName;
    //创建时间
    private  String createTime;
    //更新人ID
    private  String updateId;
    //更新人名称
    private  String updateName;
    //更新时间
    private  String updateTime;

    private String conf_id;
    private boolean collect_all;
    private String log_ver;
    private String appServer_name;
    private String appServer_ver;
    private  String update_interval;
    private  String sampling_rate;
    private  String logsBuffer_max_size;
    private  String buildLog_thread_size;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getConfId() {
        return confId;
    }

    public void setConfId(String confId) {
        this.confId = confId;
    }

    public String getLogVer() {
        return logVer;
    }

    public void setLogVer(String logVer) {
        this.logVer = logVer;
    }

    public String getAppServerName() {
        return appServerName;
    }

    public void setAppServerName(String appServerName) {
        this.appServerName = appServerName;
    }

    public String getAppServerVer() {
        return appServerVer;
    }

    public void setAppServerVer(String appServerVer) {
        this.appServerVer = appServerVer;
    }

    public String getCollectAll() {
        return collectAll;
    }

    public void setCollectAll(String collectAll) {
        this.collectAll = collectAll;
    }

    public String getLevelOne() {
        return levelOne;
    }

    public void setLevelOne(String levelOne) {
        this.levelOne = levelOne;
    }

    public String getLevelTwo() {
        return levelTwo;
    }

    public void setLevelTwo(String levelTwo) {
        this.levelTwo = levelTwo;
    }

    public String getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(String updateInterval) {
        this.updateInterval = updateInterval;
    }

    public String getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(String samplingRate) {
        this.samplingRate = samplingRate;
    }

    public String getLogsBufferMaxSize() {
        return logsBufferMaxSize;
    }

    public void setLogsBufferMaxSize(String logsBufferMaxSize) {
        this.logsBufferMaxSize = logsBufferMaxSize;
    }

    public String getBuildLogThreadSize() {
        return buildLogThreadSize;
    }

    public void setBuildLogThreadSize(String buildLogThreadSize) {
        this.buildLogThreadSize = buildLogThreadSize;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateId() {
        return updateId;
    }

    public void setUpdateId(String updateId) {
        this.updateId = updateId;
    }

    public String getUpdateName() {
        return updateName;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
