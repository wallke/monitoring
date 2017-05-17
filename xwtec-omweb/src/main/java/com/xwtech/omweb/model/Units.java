package com.xwtech.omweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Administrator on 2017/3/17 0017.
 * 预警单元格
 */
public class Units {
    //主键
    @JsonIgnore
    private String units_id;
    //预警网格编码(外键)
    @JsonIgnore
    private String grid_code;
    //预警级别
    private int alert_level;
    //时间左界限
    private String startTime;
    //时间右界限
    private String endTime;
    //警戒线
    private  double cordon;

    public String getUnits_id() {
        return units_id;
    }

    public void setUnits_id(String units_id) {
        this.units_id = units_id;
    }

    public String getGrid_code() {
        return grid_code;
    }

    public void setGrid_code(String grid_code) {
        this.grid_code = grid_code;
    }

    public int getAlert_level() {

        return alert_level;
    }

    public void setAlert_level(int alert_level) {
        this.alert_level = alert_level;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public double getCordon() {
        return cordon;
    }

    public void setCordon(double cordon) {
        this.cordon = cordon;
    }
}
