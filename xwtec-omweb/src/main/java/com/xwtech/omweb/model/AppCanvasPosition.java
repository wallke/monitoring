package com.xwtech.omweb.model;

import java.math.BigDecimal;

/**
 * Created by zhangq on 2017/2/21.
 */
public class AppCanvasPosition {

    private String appId;

    //应用编码
    private String appNum;
    //应用名称
    private String appName;

    private BigDecimal hAxis;

    private BigDecimal vAxis;

    //应用组ID
    private  String groupId;

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

    public BigDecimal gethAxis() {
        return hAxis;
    }

    public void sethAxis(BigDecimal hAxis) {
        this.hAxis = hAxis;
    }

    public BigDecimal getvAxis() {
        return vAxis;
    }

    public void setvAxis(BigDecimal vAxis) {
        this.vAxis = vAxis;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
