package com.xwtech.omweb.model;

import java.math.BigDecimal;

/**
 * Created by zhangq on 2017/2/21.
 */
public class AppCanvasLinks {

    private String appId;

    //应用编码
    private String appNum;
    //应用名称
    private String appName;

    //应用组ID
    private  String groupId;

    private String source;

    private String target;

    private String sourceNum;

    private String sourceName;

    private String targetNum;

    private String targetName;


    private long requestCount;


    private long requestErrorCount;


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppNum() {
        return appNum;
    }

    public void setAppNum(String appNum) {
        this.appNum = appNum;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getSourceNum() {
        return sourceNum;
    }

    public void setSourceNum(String sourceNum) {
        this.sourceNum = sourceNum;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getTargetNum() {
        return targetNum;
    }

    public void setTargetNum(String targetNum) {
        this.targetNum = targetNum;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public long getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(long requestCount) {
        this.requestCount = requestCount;
    }

    public long getRequestErrorCount() {
        return requestErrorCount;
    }

    public void setRequestErrorCount(long requestErrorCount) {
        this.requestErrorCount = requestErrorCount;
    }
}
