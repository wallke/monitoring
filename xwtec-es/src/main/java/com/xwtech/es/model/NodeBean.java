package com.xwtech.es.model;

import java.util.List;

/**
 * Created by zhangq on 2017/2/17.
 */
public class NodeBean {


    private String environmentId;

    private String nodeCode;

    private double min;

    private double max;

    private double avg;

    private double count;

    private double errorCount;

    private double qbs;

    private double errorRate;

    private double line99;

    private double line50;

    private double line95;

    private double rate;

    private String nodeCodePrefix;

    private List<DateHistogramBean> dateHistogramBeans;

    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(String environmentId) {
        this.environmentId = environmentId;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
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

    public double getQbs() {
        return qbs;
    }

    public void setQbs(double qbs) {
        this.qbs = qbs;
    }

    public double getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(double errorRate) {
        this.errorRate = errorRate;
    }

    public double getLine99() {
        return line99;
    }

    public void setLine99(double line99) {
        this.line99 = line99;
    }

    public double getLine50() {
        return line50;
    }

    public void setLine50(double line50) {
        this.line50 = line50;
    }

    public double getLine95() {
        return line95;
    }

    public void setLine95(double line95) {
        this.line95 = line95;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public List<DateHistogramBean> getDateHistogramBeans() {
        return dateHistogramBeans;
    }

    public void setDateHistogramBeans(List<DateHistogramBean> dateHistogramBeans) {
        this.dateHistogramBeans = dateHistogramBeans;
    }

    @Override
    public String toString() {
        return "NodeBean{" +
                "environmentId='" + environmentId + '\'' +
                ", nodeCode='" + nodeCode + '\'' +
                ", min=" + min +
                ", max=" + max +
                ", avg=" + avg +
                ", count=" + count +
                ", errorCount=" + errorCount +
                ", qbs=" + qbs +
                ", errorRate=" + errorRate +
                ", line99=" + line99 +
                ", line50=" + line50 +
                ", line95=" + line95 +
                ", rate=" + rate +
                '}';
    }

    public String getNodeCodePrefix() {

        if(this.nodeCode != null && nodeCode.length() > 10){
            return this.nodeCode.substring(0,9).concat("...") ;
        }
        return this.nodeCode;

    }
}
