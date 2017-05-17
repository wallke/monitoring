package com.xwtech.es.model;

/**
 * Created by zhangq on 2017/2/17.
 */
public class DateHistogramBean {

    private Object key;

    private double avg;

    private double count;

    private double errorCount;

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public double getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(double errorCount) {
        this.errorCount = errorCount;
    }
}
