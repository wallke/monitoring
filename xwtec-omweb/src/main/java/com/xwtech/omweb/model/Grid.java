package com.xwtech.omweb.model;

import java.util.List;

/**
 * Created by Administrator on 2017/3/17 0017.
 * 预警网格
 */
public class Grid {

    //预警网格编码
    private String grid_code;
    //网格名称
    private String grid_name;
    //指标
    private String metric;

    List<Units> units;

    public List<Units> getUnits() {
        return units;
    }

    public void setUnits(List<Units> units) {
        this.units = units;
    }

    public String getGrid_code() {
        return grid_code;
    }

    public void setGrid_code(String grid_code) {
        this.grid_code = grid_code;
    }

    public String getGrid_name() {
        return grid_name;
    }

    public void setGrid_name(String grid_name) {
        this.grid_name = grid_name;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }
}
