package com.xwtech.jobs.model;

import java.util.List;

/**
 * Created by zhangq on 2017/1/10.
 */
public class JobInfo {

    private String schedulerName;

    private String jobName;

    private String jobGroup;

    private String description;

    private String jobClassName;

    /**
     * 是否持久化
     */
    private String isDurable;

    /**
     * 作业是否并发调度 ，不并发等待
     */
    private String isNonConcurrent;

    /**
     * 中断是否恢复执行
     */
    private String requestsRecovery;

    private String isUpdateData;

    private Object jobData;

    private List<TriggerInfo> triggerInfoList;

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJobClassName() {
        return jobClassName;
    }

    public void setJobClassName(String jobClassName) {
        this.jobClassName = jobClassName;
    }

    public String getIsDurable() {
        return isDurable;
    }

    public void setIsDurable(String isDurable) {
        this.isDurable = isDurable;
    }

    public String getIsNonConcurrent() {
        return isNonConcurrent;
    }

    public void setIsNonConcurrent(String isNonConcurrent) {
        this.isNonConcurrent = isNonConcurrent;
    }

    public String getRequestsRecovery() {
        return requestsRecovery;
    }

    public void setRequestsRecovery(String requestsRecovery) {
        this.requestsRecovery = requestsRecovery;
    }

    public String getIsUpdateData() {
        return isUpdateData;
    }

    public void setIsUpdateData(String isUpdateData) {
        this.isUpdateData = isUpdateData;
    }

    public Object getJobData() {
        return jobData;
    }

    public void setJobData(Object jobData) {
        this.jobData = jobData;
    }

    public List<TriggerInfo> getTriggerInfoList() {
        return triggerInfoList;
    }

    public void setTriggerInfoList(List<TriggerInfo> triggerInfoList) {
        this.triggerInfoList = triggerInfoList;
    }
}
