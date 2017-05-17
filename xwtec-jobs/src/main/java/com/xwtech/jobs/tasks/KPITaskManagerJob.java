package com.xwtech.jobs.tasks;

import java.util.Iterator;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * KPI定时任务
 * Created by zl on 2017/2/10.
 */
public class KPITaskManagerJob implements Job {
	 final static Logger logger = LoggerFactory.getLogger(ZabbixLogsPrintJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		  JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
	        Iterator iter = jobDataMap.keySet().iterator();
	        while (iter.hasNext()) {
	            Object key = iter.next();
	            Object value = jobDataMap.get(key);
	        }
	        logger.info("=====KPI定时任务执行！=====");
	        
		
	}

}
