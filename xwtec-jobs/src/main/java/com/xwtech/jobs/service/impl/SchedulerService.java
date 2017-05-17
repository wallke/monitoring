package com.xwtech.jobs.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwtech.jobs.dao.QuartzJobsMapper;
import com.xwtech.jobs.model.JobInfo;
import com.xwtech.jobs.model.TriggerInfo;
import com.xwtech.jobs.service.ISchedulerService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;


/**
 * Created by zhangq on 2017/1/10.
 */
@Service
public class SchedulerService implements ISchedulerService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    QuartzJobsMapper quartzJobsMapper;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;// 作业调度器

    private Scheduler scheduler = null;

    private Scheduler getScheduler() {
        if (this.scheduler == null) {
            this.scheduler = schedulerFactoryBean.getScheduler();
        }
        return this.scheduler;
    }

    /**
     * 唤醒作业
     *
     * @param jobName
     * @param group
     * @Title: resumeJob
     * @return: boolean
     */
    public boolean resumeJob(String jobName, String group) {
        JobKey jobKey = new JobKey(jobName, group);
        try {
            getScheduler().resumeJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            logger.error("唤醒作业时发生异常", e);
        }
        return false;
    }



    /**
     * 暂停作业
     *
     * @param jobName
     * @param group
     * @Title: pauseJob
     * @return: boolean
     */
    public boolean pauseJob(String jobName, String group) {
        JobKey jobKey = new JobKey(jobName, group);
        try {
            getScheduler().pauseJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            logger.error("暂停作业时发生异常", e);
        }
        return false;
    }



    /**
     * 删除作业
     *
     * @param jobName
     * @param group
     * @Title: removeJob
     * @return: boolean
     */
    public boolean removeJob(String jobName, String group) {
        JobKey jobKey = new JobKey(jobName, group);
        try {
            return getScheduler().deleteJob(jobKey);// 移除触发器
        } catch (SchedulerException e) {
            logger.error("删除作业时发生异常", e);
        }
        return false;
    }



    /**
     * 删除触发器
     *
     * @param triggerName
     * @param group
     * @Title: removeTrigger
     * @return: boolean
     */
    public boolean removeTrigger(String triggerName, String group) {
        TriggerKey triggerKey = getTriggerKey(triggerName, group);
        try {

            getScheduler().pauseTrigger(triggerKey);// 停止触发器
            return getScheduler().unscheduleJob(triggerKey);// 移除触发器

        } catch (SchedulerException e) {
            logger.error("删除作业触发器时发生异常", e);
        }
        return false;
    }

    /**
     * 重启作业
     *
     * @param triggerName
     * @param group
     * @Title: resumeTrigger
     * @return: boolean
     */
    public boolean resumeTrigger(String triggerName, String group) {
        TriggerKey triggerKey = getTriggerKey(triggerName, group);
        try {
            getScheduler().resumeTrigger(triggerKey);
        } catch (SchedulerException e) {
            logger.error("重启作业时发生异常", e);
            return false;
        }
        return true;
    }

    /**
     * 暂停作业
     *
     * @param triggerName
     * @param group
     * @throws Exception
     * @Title: pauseTrigger
     * @return: void
     */
    public boolean pauseTrigger(String triggerName, String group) {
        TriggerKey triggerKey = getTriggerKey(triggerName, group);
        try {
            getScheduler().pauseTrigger(triggerKey);
        } catch (SchedulerException e) {
            logger.error("暂停作业是发生异常", e);
            return false;
        }
        return true;
    }

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
     * @return: boolean
     */
    public boolean scheduleJob(String jobName,
                               String group,
                               String job_class_name,
                               String triggerName,
                               String cronExpression,
                               String description) {
        try {
            JobDetail jobDetail = getJobDetail(jobName, group, job_class_name, description);
            Trigger trigger = getTrigger(triggerName, group, cronExpression, description);

            getScheduler().scheduleJob(jobDetail, trigger);
        } catch (ClassNotFoundException e) {
            logger.error("未找到作业类：" + job_class_name, e);
            return false;
        } catch (SchedulerException e) {
            logger.error("调度器执行异常", e);
            return false;
        }
        return true;
    }

    /**
     * 更新触发器的时间规则
     *
     * @param triggerName    触发器名称
     * @param group          触发器分组
     * @param cronExpression 时间规则
     * @throws ParseException     解析异常
     * @throws SchedulerException 调度器异常
     * @Title: rescheduleJob
     * @return: boolean
     */
    public boolean rescheduleJob(String triggerName, String group, String cronExpression) {

        TriggerKey triggerKey = getTriggerKey(triggerName, group);
        try {
            Trigger newTrigger = getTrigger(triggerName, group, cronExpression);
            getScheduler().rescheduleJob(triggerKey, newTrigger);
        } catch (SchedulerException e) {
            logger.error("调度器执行异常", e);
            return false;
        }
        return true;
    }

    /**
     * 立即执行作业，不考虑时间规则
     *
     * @param jobName 作业名称
     * @param group   作业分组
     * @throws SchedulerException 调度器异常
     * @Title: triggerJob
     * @return: boolean
     */
    public boolean triggerJob(String jobName, String group) {
        JobKey jobKey = new JobKey(jobName, group);
        try {
            getScheduler().triggerJob(jobKey);
        } catch (SchedulerException e) {
            logger.error("立即执行作业时发生异常", e);
            return false;
        }
        return true;
    }


    @Override
    public List<JobInfo> getJobs(JobInfo job, PageInfo<JobInfo> pageInfo) {

        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());

        List<JobInfo> list = quartzJobsMapper.getJobs(job);
        if(list != null){
            for (JobInfo jobInfo:list) {
                TriggerInfo triggerInfo = new TriggerInfo();
                triggerInfo.setJobGroup(jobInfo.getJobGroup());
                triggerInfo.setJobName(jobInfo.getJobName());
                triggerInfo.setSchedulerName(jobInfo.getSchedulerName());
                List<TriggerInfo> triggerInfos = quartzJobsMapper.getTriggers(triggerInfo);
               jobInfo.setTriggerInfoList(triggerInfos);
            }
        }
        return list;
    }

    @Override
    public JobInfo getJob(String jobName ,String jobGroup,String schedulerName) {

        JobInfo jobInfo = new JobInfo();
        jobInfo.setJobGroup(jobGroup);
        jobInfo.setJobName(jobName);
        jobInfo.setSchedulerName(schedulerName);
        jobInfo =   quartzJobsMapper.getJob(jobInfo);
        if(jobInfo != null){
            TriggerInfo triggerInfo = new TriggerInfo();
            triggerInfo.setJobGroup(jobInfo.getJobGroup());
            triggerInfo.setJobName(jobInfo.getJobName());
            triggerInfo.setSchedulerName(jobInfo.getSchedulerName());
            List<TriggerInfo> triggerInfos = quartzJobsMapper.getTriggers(triggerInfo);
            jobInfo.setTriggerInfoList(triggerInfos);
        }
        return jobInfo;
    }


    @Override
    public TriggerInfo getTrigger(
            String jobGroup, String jobName ,String schedulerName,
            String triggerGroup,String triggerName){
        TriggerInfo triggerInfo = new TriggerInfo();
        triggerInfo.setJobGroup(jobGroup);
        triggerInfo.setJobName(jobName);
        triggerInfo.setSchedulerName(schedulerName);
        triggerInfo.setTriggerName(triggerName);
        triggerInfo.setTriggerGroup(triggerGroup);
        return quartzJobsMapper.getTrigger(triggerInfo);
    }


    @Override
    public List<TriggerInfo> getTriggers(TriggerInfo triggerInfo) {
        return null;
    }

    /**
     * 获取触发器的key
     *
     * @param triggerName
     * @param group
     * @return
     * @Title: getTriggerKey
     * @return: TriggerKey
     */
    private TriggerKey getTriggerKey(String triggerName, String group) {

        TriggerKey triggerKey = new TriggerKey(triggerName, group);

        return triggerKey;
    }

    /**
     * 创建Jobdetail类
     *
     * @param jobName        作业名称
     * @param group          作业所在组
     * @param job_class_name 作业的权限定类名
     * @param description
     * @Title: getJobDetail
     * @return: JobDetail
     */
    @SuppressWarnings("unchecked")
	private JobDetail getJobDetail(String jobName, String group, String job_class_name, String description)
            throws ClassNotFoundException {
        try {
            return JobBuilder
                    .newJob((Class<? extends Job>) Class.forName(job_class_name))
                    .withIdentity(jobName, group)
                    .withDescription(description)
                    .storeDurably(true)
                    .build();

        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException(job_class_name + "不存在或者书写不正确", e);
        }
    }


    /**
     * 获取出发器
     *
     * @param triggerName    触发器名称
     * @param group          触发器分组
     * @param cronExpression 触发器时间规则
     * @return
     * @throws ParseException 解析异常
     * @Title: getTrigger
     * @return: Trigger
     */
    private Trigger getTrigger(String triggerName, String group, String cronExpression) {

        return getTrigger(triggerName,group,cronExpression,"");
    }

    /**
     * 创建触发器
     *
     * @param triggerName    触发器名称
     * @param group          触发器所在组
     * @param cronExpression 触发器的时间规则
     * @return
     * @Title: getTrigger
     * @return: Trigger
     */
    private Trigger getTrigger(String triggerName, String group, String cronExpression, String description) {
        return TriggerBuilder
                .newTrigger()
                //.startNow()
                .withIdentity(triggerName, group)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .withDescription(description)
                .build();
    }

}
