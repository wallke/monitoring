package com.xwtech.es.model;

/**
 * Created by zhangq on 2017/2/27.
 */
public class ChainBean {


    private String sourceApp;

    private String sourceAppCode;

    private String targetApp;

    private String targetAppCode;

    private String sourceMethod;

    private String targetMethod;

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

    public String getSourceApp() {
        return sourceApp;
    }

    public void setSourceApp(String sourceApp) {
        this.sourceApp = sourceApp;
    }

    public String getSourceAppCode() {
        return sourceAppCode;
    }

    public void setSourceAppCode(String sourceAppCode) {
        this.sourceAppCode = sourceAppCode;
    }

    public String getTargetApp() {
        return targetApp;
    }

    public void setTargetApp(String targetApp) {
        this.targetApp = targetApp;
    }

    public String getTargetAppCode() {
        return targetAppCode;
    }

    public void setTargetAppCode(String targetAppCode) {
        this.targetAppCode = targetAppCode;
    }

    public String getSourceMethod() {
        return sourceMethod;
    }

    public void setSourceMethod(String sourceMethod) {
        this.sourceMethod = sourceMethod;
    }

    public String getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(String targetMethod) {
        this.targetMethod = targetMethod;
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

    @Override
    public String toString() {
        return super.toString();
    }
}
