package com.xwtech.jobs.service;

import com.github.pagehelper.PageInfo;
import com.xwtech.jobs.model.JobInfo;
import com.xwtech.jobs.model.TriggerInfo;

import java.util.List;

/**
 * 调度器，用于作业的添加、删除、启动
 * Created by zhangq on 2017/1/10.
 */
public interface ISchedulerService {


    /**
     * 唤醒作业
     *
     * @param jobName
     * @param group
     * @Title: resumeJob
     * @return: boolean
     */
     boolean resumeJob(String jobName, String group);


    /**
     * 暂停作业
     *
     * @param jobName
     * @param group
     * @Title: pauseJob
     * @return: boolean
     */
    boolean pauseJob(String jobName, String group);


    /**
     * 删除作业
     * @param jobName 作业名称
     * @param group 组名
     * @return: boolean
     */
    boolean removeJob(String jobName, String group);


    /**
     * 删除触发器
     *
     * @param triggerName
     * @param group
     * @return
     * @throws Exception
     * @Title: removeTrigger
     * @return: boolean
     */
    boolean removeTrigger(String triggerName, String group);

    /**
     * 重启作业
     *
     * @param triggerName
     * @param group
     * @Title: resumeTrigger
     * @return: boolean
     */
    boolean resumeTrigger(String triggerName, String group);

    /**
     * 暂停作业
     *
     * @param triggerName
     * @param group
     * @throws Exception
     * @Title: pauseTrigger
     * @return: boolean
     */
    boolean pauseTrigger(String triggerName, String group);

    /**
     * 新增作业
     *
     * @param jobName        作业名称
     * @param group          作业和触发器所在分组
     * @param job_class_name 作业的类名称，必须是全限定类名 如：com.acca.xxx.service（不能是接口）
     * @param triggerName    触发器名称
     * @param cronExpression 时间规则
     * @param description    描述
     * @Title: scheduleJob
     * @return: void
     */
    boolean scheduleJob(String jobName, String group, String job_class_name, String triggerName,
                     String cronExpression, String description);

    /**
     * 更新触发器的时间规则
     *
     * @param triggerName    触发器名称
     * @param group          触发器分组
     * @param cronExpression 时间规则
     *                       调度器异常
     * @Title: rescheduleJob
     * @return: void
     */
    boolean rescheduleJob(String triggerName, String group, String cronExpression);

    /**
     * 立即执行作业，不考虑时间规则
     *
     * @param jobName 作业名称
     * @param group   作业分组
     *                调度器异常
     * @Title: triggerJob
     * @return: void
     */
    boolean triggerJob(String jobName, String group);

    /**
     * 查询作业信息
     *
     * @Title: getJobs
     * @return
     * @return: List<JobInfo>
     */
    List<JobInfo> getJobs(JobInfo job, PageInfo<JobInfo> pageInfo);

    JobInfo getJob(String jobName ,String jobGroup,String schedulerName);



    TriggerInfo getTrigger(
            String jobGroup, String jobName ,String schedulerName,
            String triggerGroup,String triggerName);


    /**
     *
     * @param triggerInfo
     * @return
     */
    List<TriggerInfo> getTriggers(TriggerInfo triggerInfo);



}
