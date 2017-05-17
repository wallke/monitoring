package com.xwtech.jobs.tasks;

import com.xwtech.es.service.TestService;
import com.xwtech.jobs.service.JobTestService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;

/**
 * Created by zhangq on 2017/1/6.
 */
//JobInfo 能通过 JobExecutionContext 对象访问 JobDataMap
public   class PrintJobDataMapJob implements Job {

    final static Logger logger = LoggerFactory.getLogger(PrintJobDataMapJob.class);

    @Autowired
    JobTestService jobTestService;

    @Autowired
    TestService testService;

    public void execute(JobExecutionContext context)throws JobExecutionException {
        //logger.info("in PrintJobDataMapJob");
        // Every job has its own job detail
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        // Iterate through the key/value pairs
        Iterator iter = jobDataMap.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            Object value = jobDataMap.get(key);
            //logger.info("Key: " + key + " - Value: " + value);
        }

        logger.info(jobTestService.getData());

        testService.get();

    }

}

