package com.xwtech.omweb.model;

import java.util.List;

/**
 * Created by Administrator on 2017/2/15 0015.
 * 软件公司信息
 */
public class Crop {

    //企业主键
    private String cropId;
    //企业名称
    private String cropName;
    //企业行业
    private String cropIndustry;
    //企业规模
    private String cropScale;
    //企业地址
    private String cropAddress;
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
    //软件公司联系人
    List<CropContacts> CropContactsList;

    public String getCropId() {
        return cropId;
    }

    public void setCropId(String cropId) {
        this.cropId = cropId;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getCropIndustry() {
        return cropIndustry;
    }

    public void setCropIndustry(String cropIndustry) {
        this.cropIndustry = cropIndustry;
    }

    public String getCropScale() {
        return cropScale;
    }

    public void setCropScale(String cropScale) {
        this.cropScale = cropScale;
    }

    public String getCropAddress() {
        return cropAddress;
    }

    public void setCropAddress(String cropAddress) {
        this.cropAddress = cropAddress;
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
