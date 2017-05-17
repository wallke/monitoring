package com.xwtech.jobs.dao;

import com.xwtech.jobs.model.JobInfo;
import com.xwtech.jobs.model.TriggerInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by zhangq on 2017/1/10.
 */
@Mapper
public interface QuartzJobsMapper {


    JobInfo getJob(JobInfo job);

    List<JobInfo> getJobs(JobInfo job);

    TriggerInfo getTrigger(TriggerInfo triggerInfo);

    List<TriggerInfo> getTriggers(TriggerInfo triggerInfo);

}
