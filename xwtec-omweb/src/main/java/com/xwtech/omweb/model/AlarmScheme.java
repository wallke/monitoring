package com.xwtech.omweb.model;

import java.util.List;

/**
 * Created by Administrator on 2017/3/13 0013.
 * 告警方案信息
 */
public class AlarmScheme {

    //告警方案名称
    private String alert_scheme_name;
    //告警方案标识
    private String alert_scheme_code;

    List<AlarmSchemeUnits> units;

    public String getAlert_scheme_name() {
        return alert_scheme_name;
    }

    public void setAlert_scheme_name(String alert_scheme_name) {
        this.alert_scheme_name = alert_scheme_name;
    }

    public String getAlert_scheme_code() {
        return alert_scheme_code;
    }

    public void setAlert_scheme_code(String alert_scheme_code) {
        this.alert_scheme_code = alert_scheme_code;
    }

    public List<AlarmSchemeUnits> getUnits() {
        return units;
    }

    public void setUnits(List<AlarmSchemeUnits> units) {
        this.units = units;
    }
}
