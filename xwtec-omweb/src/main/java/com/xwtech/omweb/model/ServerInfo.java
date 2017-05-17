package com.xwtech.omweb.model;

/**
 * Created by zhongxueye on 17-2-10.
 * 主机信息实体BEAN
 */
public class ServerInfo {
    //主机主键
    private String serverId;
    //外键（机房主键）
    private String roomId;
    //CPU信息
    private String CPUInfo;
    //内存信息
    private String memInfo;
    //磁盘信息
    private String diskInfo;
    //操作系统信息
    private String osInfo;
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

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getCPUInfo() {
        return CPUInfo;
    }

    public void setCPUInfo(String CPUInfo) {
        this.CPUInfo = CPUInfo;
    }

    public String getMemInfo() {
        return memInfo;
    }

    public void setMemInfo(String memInfo) {
        this.memInfo = memInfo;
    }

    public String getDiskInfo() {
        return diskInfo;
    }

    public void setDiskInfo(String diskInfo) {
        this.diskInfo = diskInfo;
    }

    public String getOsInfo() {
        return osInfo;
    }

    public void setOsInfo(String osInfo) {
        this.osInfo = osInfo;
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
