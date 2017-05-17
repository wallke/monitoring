package com.xwtech.jobs.tasks;

import com.xwtech.jobs.service.IZabbixLogsTool;
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
 * Created by zl on 2017/2/10.
 */
public   class ZabbixLogsPrintJob implements Job {

    final static Logger logger = LoggerFactory.getLogger(ZabbixLogsPrintJob.class);

    @Autowired
    IZabbixLogsTool zabbixLogsTool;

    @SuppressWarnings({ "rawtypes", "unused" })
	public void execute(JobExecutionContext context)throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        Iterator iter = jobDataMap.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            Object value = jobDataMap.get(key);
        }
        
        zabbixLogsTool.getZabbixLogs();
        logger.info("--获取zabbix信息  end---");
    }

}

