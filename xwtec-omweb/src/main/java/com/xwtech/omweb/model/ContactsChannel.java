package com.xwtech.omweb.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Administrator on 2017/3/27 0027.
 * 联系人渠道表
 */
public class ContactsChannel {
    //主键
    private String contactId;
    //渠道编码
    private String channelId;
    private String type;
    private String address;
    //通知策略编码
    private String frequency_code;
    //联系人账户
    private String userName;
    //通知策略编码
    private String notice_strategy_code;
    //联系人名称
    @JsonIgnore
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotice_strategy_code() {
        return notice_strategy_code;
    }

    public void setNotice_strategy_code(String notice_strategy_code) {
        this.notice_strategy_code = notice_strategy_code;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFrequency_code() {
        return frequency_code;
    }

    public void setFrequency_code(String frequency_code) {
        this.frequency_code = frequency_code;
    }
}
