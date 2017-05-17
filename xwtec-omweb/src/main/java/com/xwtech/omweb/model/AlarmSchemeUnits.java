package com.xwtech.omweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Administrator on 2017/3/28 0028.
 */
public class AlarmSchemeUnits {
    //主键
    @JsonIgnore
    private String untis_id;
    //告警方案标识
    @JsonIgnore
    private String alert_scheme_code;
    //告警压力
    private String alert_pressure;
    //预警级别
    private String alert_level;
    //持续时间（分钟）
    private String sustained;
    //通知策略编码
    private String notice_strategy_code;

    public String getUntis_id() {
        return untis_id;
    }

    public void setUntis_id(String untis_id) {
        this.untis_id = untis_id;
    }

    public String getAlert_pressure() {
        return alert_pressure;
    }

    public void setAlert_pressure(String alert_pressure) {
        this.alert_pressure = alert_pressure;
    }

    public String getAlert_level() {
        return alert_level;
    }

    public void setAlert_level(String alert_level) {
        this.alert_level = alert_level;
    }

    public String getSustained() {
        return sustained;
    }

    public void setSustained(String sustained) {
        this.sustained = sustained;
    }

    public String getNotice_strategy_code() {
        return notice_strategy_code;
    }

    public void setNotice_strategy_code(String notice_strategy_code) {
        this.notice_strategy_code = notice_strategy_code;
    }

    public String getAlert_scheme_code() {
        return alert_scheme_code;
    }

    public void setAlert_scheme_code(String alert_scheme_code) {
        this.alert_scheme_code = alert_scheme_code;
    }
}
