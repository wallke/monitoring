package com.xwtech.omweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/14 0014.
 * 软件公司联系人
 */
public class CropContacts {

    //联系人账户
    private String userName;
    //主键
    @JsonIgnore
    private String contactId;
    //公司ID（外键）
    @JsonIgnore
    private String cropId;
    //联系人姓名
    private String name;
    //联系人电话
    @JsonIgnore
    private String phone;
    //联系人手机
    @JsonIgnore
    private String mobile;
    //联系人QQ
    @JsonIgnore
    private String qq;
    //联系人微信
    @JsonIgnore
    private String wechat;
    //联系人邮箱
    @JsonIgnore
    private String email;
    //是否被逻辑删除（1-否;0-是）
    @JsonIgnore
    private String isUse;
    //是否启用（0-否;1-是）
    @JsonIgnore
    private String isEnabled;
    //说明
    @JsonIgnore
    private String desc;
    //备注
    @JsonIgnore
    private String memo;
    //创建人编码
    @JsonIgnore
    private String createManCode;
    //创建人姓名
    @JsonIgnore
    private String createManName;
    //创建时间
    @JsonIgnore
    private String createTime;
    //创建备注
    @JsonIgnore
    private String createMemo;
    //最后操作人编码
    @JsonIgnore
    private String lastManCode;
    //最后操作人名称
    @JsonIgnore
    private String lastManName;
    //最后操作时间
    @JsonIgnore
    private String lastTime;
    //最后操作备注
    @JsonIgnore
    private String lastMemo;
    //公司名称
    @JsonIgnore
    private String cropName;
    //应用集合
    @JsonIgnore
    List<App> appList;
    //联系人称谓
    @JsonIgnore
    private String title;
    //类型sms email
    @JsonIgnore
    private String type;
    @JsonIgnore
    private String frequency_name;

    //渠道列表
    List<ContactsChannel> channels;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<ContactsChannel> getChannels() {
        return channels;
    }

    public void setChannels(List<ContactsChannel> channels) {
        this.channels = channels;
    }

    public String getFrequency_name() {
        return frequency_name;
    }

    public void setFrequency_name(String frequency_name) {
        this.frequency_name = frequency_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<App> getAppList() {
        return appList;
    }

    public void setAppList(List<App> appList) {
        this.appList = appList;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getCropId() {
        return cropId;
    }

    public void setCropId(String cropId) {
        this.cropId = cropId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
