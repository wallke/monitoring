package com.xwtech.omweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * Created by Administrator on 2017/3/14 0014.
 * 通知策略
 */
public class NoticeStrategy {

    //通知策略主键
    private  String notice_strategy_code;
    //通知策略名称
    private String notice_strategy_name;
    //需要通知的干系人
    List<CropContacts> stakeholders;

    public List<CropContacts> getStakeholders() {
        return stakeholders;
    }

    public void setStakeholders(List<CropContacts> stakeholders) {
        this.stakeholders = stakeholders;
    }

    public String getNotice_strategy_code() {
        return notice_strategy_code;
    }

    public void setNotice_strategy_code(String notice_strategy_code) {
        this.notice_strategy_code = notice_strategy_code;
    }

    public String getNotice_strategy_name() {
        return notice_strategy_name;
    }

    public void setNotice_strategy_name(String notice_strategy_name) {
        this.notice_strategy_name = notice_strategy_name;
    }
}
