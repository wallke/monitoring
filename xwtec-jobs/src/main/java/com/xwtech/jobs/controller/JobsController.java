package com.xwtech.jobs.controller;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.xwtech.framework.shiro.model.auth.User;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.jobs.WebApp;
import com.xwtech.jobs.model.JobInfo;
import com.xwtech.jobs.model.TriggerInfo;
import com.xwtech.jobs.service.ISchedulerService;
import com.xwtech.jobs.tasks.PrintJobDataMapJob;
import com.xwtech.jobs.utils.ClassUtil;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason on 16/7/22.
 */
@RequestMapping("jobs")
@Controller
public class JobsController {

    final static Logger logger = LoggerFactory.getLogger(JobsController.class);


    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;// 作业调度器

    @Autowired
    ISchedulerService schedulerService;

    @RequestMapping(value = {"", "/", "index"})
    public ModelAndView index(String group,String jobName,String jobClassName,
                              @RequestParam(name = "ps", defaultValue = "10") int ps,
                              @RequestParam(name = "pn", defaultValue = "1") int pn) {

        PageInfo<JobInfo> pageInfo = new PageInfo<JobInfo>();
        pageInfo.setPageSize(ps < 10 ? ps : 10);
        pageInfo.setPageNum(pn);

//        Scheduler scheduler = schedulerFactoryBean.getScheduler();
//
//        JobKey jobKey = new JobKey("PrintJobDataMapJob1", Scheduler.DEFAULT_GROUP);
//
//
//        //job
//        JobDetail jobDetail = JobBuilder.newJob(PrintJobDataMapJob.class)
//                .withIdentity(jobKey).build();
//        jobDetail.getJobDataMap().put("name", "John Doe1111111111111");
//        jobDetail.getJobDataMap().put("age", 23);
//
//        TriggerKey triggerKey = new TriggerKey("PrintJobDataMapJobTrigger1", Scheduler.DEFAULT_GROUP);
//
//
//        //触发器
//        CronTrigger trigger = TriggerBuilder
//                .newTrigger()
//                .startNow()
//                .withIdentity(triggerKey)
//                .withSchedule(CronScheduleBuilder.cronSchedule("0/1 * * * * ?"))
//                //.forJob(jobDetail)
//                .build();
//
//        try {
//            schedulerFactoryBean.getScheduler().scheduleJob(jobDetail,trigger);
//        } catch (SchedulerException e) {
//            e.printStackTrace();
//        }

        JobInfo jobInfo = new JobInfo();
        jobInfo.setJobGroup(group);
        jobInfo.setJobName(jobName);
        jobInfo.setJobClassName(jobClassName);
        List<JobInfo> jobInfoList = schedulerService.getJobs(jobInfo,pageInfo);

        ModelAndView modelAndView = new ModelAndView("jobs/index");
        modelAndView.addObject("jobs", jobInfoList);
        modelAndView.addObject("group", group);
        modelAndView.addObject("jobName", jobName);
        modelAndView.addObject("jobClassName", jobClassName);
        modelAndView.addObject("pageInfo",
                ((Page<JobInfo>) jobInfoList).toPageInfo());
        return modelAndView;
    }


    @RequestMapping(value = {"create"})
    public ModelAndView create(
            String jobName,
            String jobGroup,
            String triggerName,
            String triggerGroup) {

        JobInfo jobInfo = new JobInfo();
        TriggerInfo triggerInfo = new TriggerInfo();

        if(StringUtils.isNotEmpty(triggerName) && StringUtils.isNotEmpty(triggerGroup)){

            jobInfo = schedulerService.getJob(jobName,jobGroup, WebApp.SchedulerName);
            triggerInfo = schedulerService.getTrigger(jobGroup,jobName,WebApp.SchedulerName,triggerGroup,triggerName);
        }


        ArrayList list = ClassUtil.getAllClassByInterface(Job.class,WebApp.TaskPackage);

        ModelAndView modelAndView = new ModelAndView("jobs/create");
        modelAndView.addObject("job", jobInfo);
        modelAndView.addObject("trigger", triggerInfo);
        modelAndView.addObject("classList", list);
        return modelAndView;
    }


    @RequestMapping(value = {"create"},method = RequestMethod.POST)
    @ResponseBody
    public JSONResult createSubmit(
            String jobName,
            String jobGroup,
            String description ,
            String jobClassName,
            String cornExpression,
            String triggerName){

        JSONResult jsonResult = new JSONResult();
        try{
            JobInfo jobInfo = schedulerService.getJob(jobName,jobGroup, WebApp.SchedulerName);
            int count = jobInfo != null && jobInfo.getTriggerInfoList() != null ?  jobInfo.getTriggerInfoList().size() + 1 : 1;
            String temp = jobName + "_" + count;
            schedulerService.scheduleJob(jobName,jobGroup,jobClassName,temp,cornExpression,description);

        }catch (Exception ex){
            logger.error("新增任务",ex);
            jsonResult.setErrorInfo("添加异常");
        }

        return jsonResult;
    }



    @RequestMapping(value = {"corn"})
    public ModelAndView corn(
            String jobName,
            String jobGroup,
            String triggerName,
            String triggerGroup) {

        JobInfo jobInfo = new JobInfo();
        TriggerInfo triggerInfo = new TriggerInfo();

        if(StringUtils.isNotEmpty(triggerName) && StringUtils.isNotEmpty(triggerGroup)){

            jobInfo = schedulerService.getJob(jobName,jobGroup, WebApp.SchedulerName);
            triggerInfo = schedulerService.getTrigger(jobGroup,jobName,WebApp.SchedulerName,triggerGroup,triggerName);
        }

        ModelAndView modelAndView = new ModelAndView("jobs/corn");
        modelAndView.addObject("job", jobInfo);
        modelAndView.addObject("trigger", triggerInfo);
        return modelAndView;
    }


    @RequestMapping(value = {"corn"},method = RequestMethod.POST)
    @ResponseBody
    public JSONResult cornSubmit(
            String jobName,
            String jobGroup,
            String cornExpression,
            String triggerName){

        JSONResult jsonResult = new JSONResult();
        try{
            if(schedulerService.rescheduleJob(triggerName,jobGroup,cornExpression)){

            }else{
                jsonResult.setFailInfo("修改任务计划失败!");
            }
        }catch (Exception ex){
            logger.error("修改任务计划",ex);
            jsonResult.setErrorInfo("修改任务计划异常!");
        }

        return jsonResult;
    }



    @RequestMapping(value = {"removeTrigger"},method = RequestMethod.POST)
    @ResponseBody
    public JSONResult removeTriggerSubmit(
            String group,
            String triggerName){

        JSONResult jsonResult = new JSONResult();
        try{

            if(schedulerService.removeTrigger(triggerName,group)){

            }else{
                jsonResult.setFailInfo("删除触发器失败");
            }

        }catch (Exception ex){
            logger.error("删除触发器",ex);
            jsonResult.setErrorInfo("添加异常");
        }

        return jsonResult;
    }


    @RequestMapping(value = {"removeJob"},method = RequestMethod.POST)
    @ResponseBody
    public JSONResult removeJobSubmit(
            String group,
            String jobName){

        JSONResult jsonResult = new JSONResult();
        try{

            if(schedulerService.removeJob(jobName,group)){

            }else{
                jsonResult.setFailInfo("删除失败");
            }

        }catch (Exception ex){
            logger.error("删除任务",ex);
            jsonResult.setErrorInfo("添加异常");
        }

        return jsonResult;
    }


    @RequestMapping(value = {"pauseJob"},method = RequestMethod.POST)
    @ResponseBody
    public JSONResult pauseJobSubmit(
            String group,
            String jobName){

        JSONResult jsonResult = new JSONResult();
        try{

            if(schedulerService.pauseJob(jobName,group)){

            }else{
                jsonResult.setFailInfo("暂停任务失败");
            }

        }catch (Exception ex){
            logger.error("暂停任务",ex);
            jsonResult.setErrorInfo("暂停任务异常");
        }

        return jsonResult;
    }

    @RequestMapping(value = {"resumeJob"},method = RequestMethod.POST)
    @ResponseBody
    public JSONResult resumeJobSubmit(
            String group,
            String jobName){

        JSONResult jsonResult = new JSONResult();
        try{

            if(schedulerService.resumeJob(jobName,group)){

            }else{
                jsonResult.setFailInfo("重启任务失败");
            }

        }catch (Exception ex){
            logger.error("重启任务",ex);
            jsonResult.setErrorInfo("重启任务异常");
        }

        return jsonResult;
    }

    @RequestMapping(value = {"triggerJob"},method = RequestMethod.POST)
    @ResponseBody
    public JSONResult triggerJobSubmit(
            String group,
            String jobName){

        JSONResult jsonResult = new JSONResult();
        try{

            if(schedulerService.triggerJob(jobName,group)){

            }else{
                jsonResult.setFailInfo("执行任务失败");
            }

        }catch (Exception ex){
            logger.error("执行任务",ex);
            jsonResult.setErrorInfo("执行任务异常");
        }

        return jsonResult;
    }



    @RequestMapping(value = {"pauseTrigger"},method = RequestMethod.POST)
    @ResponseBody
    public JSONResult pauseTriggerSubmit(
            String group,
            String triggerName){

        JSONResult jsonResult = new JSONResult();
        try{

            if(schedulerService.pauseTrigger(triggerName,group)){

            }else{
                jsonResult.setFailInfo("暂停触发器失败");
            }

        }catch (Exception ex){
            logger.error("暂停触发器",ex);
            jsonResult.setErrorInfo("暂停触发器异常");
        }

        return jsonResult;
    }

    @RequestMapping(value = {"resumeTrigger"},method = RequestMethod.POST)
    @ResponseBody
    public JSONResult resumeTriggerSubmit(
            String group,
            String triggerName){

        JSONResult jsonResult = new JSONResult();
        try{

            if(schedulerService.resumeTrigger(triggerName,group)){

            }else{
                jsonResult.setFailInfo("重启触发器失败");
            }

        }catch (Exception ex){
            logger.error("重启触发器任务",ex);
            jsonResult.setErrorInfo("重启触发器异常");
        }

        return jsonResult;
    }



}
