package com.xwtech.jobs.KPI;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池
 * @author zl 2017/0220
 *
 */
public class KPICalculatorPool {
	   
	   private static ExecutorService threadPool = null;  
	    public static ExecutorService getThreadPool(){  
	        if(threadPool==null){  
	            threadPool = Executors.newFixedThreadPool(20);  
	        }  
	        return  threadPool;  
	    }  
}
