package com.xwtech.omweb.model;

/**
 * Created by Administrator on 2017/2/14 0014.
 * 应用信息实体bean
 */
public class App {

    //主键
    private String appId;
    //外键（主机主键）
//    private String serverId;
    //主机名称
    private String serverName;
    //外键（机房主键）
//    private String roomId;
//    //机房名称
    private String roomName;
    //节点数量
    private  int nodeCount;
    //应用编码
    private String appNum;
    //应用名称
    private String appName;
    //外键（应用类别）
    private String categoryId;
    //应用类别名称
    private String categoryName;
    //外键（软件所属公司主键）
    private String cropId;
    //软件公司名称
    private String cropName;
    //是否被逻辑删除（1-否;0-是）
    private String isUse;
    //是否启用（0-否;1-是）
    private String isEnabled;
    //说明
    private String desc;
    //备注
    private String memo;
    //创建人编码
    private String createManCode;
    //创建人姓名
    private String createManName;
    //创建时间
    private String createTime;
    //创建备注
    private String createMemo;
    //最后操作人编码
    private String lastManCode;
    //最后操作人名称
    private String lastManName;
    //最后操作时间
    private String lastTime;
    //最后操作备注
    private String lastMemo;
    //应用组ID
    private  String groupId;
    //应用组名称
    private String groupName;

    public int getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCropId() {
        return cropId;
    }

    public void setCropId(String cropId) {
        this.cropId = cropId;
    }

    public String getIsUse() {
        return isUse;
    }

    public void setIsUse(String isUse) {
        this.isUse = isUse;
    }

    public String getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(String isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getCreateManCode() {
        return createManCode;
    }

    public void setCreateManCode(String createManCode) {
        this.createManCode = createManCode;
    }

    public String getCreateManName() {
        return createManName;
    }

    public void setCreateManName(String createManName) {
        this.createManName = createManName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateMemo() {
        return createMemo;
    }

    public void setCreateMemo(String createMemo) {
        this.createMemo = createMemo;
    }

    public String getLastManCode() {
        return lastManCode;
    }

    public void setLastManCode(String lastManCode) {
        this.lastManCode = lastManCode;
    }

    public String getLastManName() {
        return lastManName;
    }

    public void setLastManName(String lastManName) {
        this.lastManName = lastManName;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getLastMemo() {
        return lastMemo;
    }

    public void setLastMemo(String lastMemo) {
        this.lastMemo = lastMemo;
    }
}
