package com.xwtech.omweb.model;

import java.util.List;

/**
 * Created by Administrator on 2017/3/10 0010.
 * 字典信息
 */
public class Dictionaries {

    //预警级别
    private String level;
    //级别对应颜色
    private String color;

    private List<Dictionaries> alert_levels;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Dictionaries> getAlert_levels() {
        return alert_levels;
    }

    public void setAlert_levels(List<Dictionaries> alert_levels) {
        this.alert_levels = alert_levels;
    }
}
