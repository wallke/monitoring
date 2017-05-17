/*package com.xwtech.jobs.KPI;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.xwtech.jobs.KPI.queue.Queue;
import com.xwtech.jobs.KPI.queue.QueueManager;
import com.xwtech.jobs.controller.KPIConnetionController;

*//**
 * 获取KPIes内的值启动线程池
 * 
 * @author zl 2017/0220
 *
 *//*
public class KPITaskManager {
	
	@Autowired
	KPIConnetionController kpiController;

	private static ExecutorService pool = KPICalculatorPool.getThreadPool();

	private static final Logger logger = LoggerFactory.getLogger(KPITaskManager.class);
	
	
	private static KPITaskManager kpiTaskManager = null;
	
	
	public static KPITaskManager getKPITaskManager(){
		if(kpiTaskManager == null){
			kpiTaskManager = new KPITaskManager();
		}
		return kpiTaskManager;
	}

	public  void getESKpiValues() {

		List<Map<String, Object>> list = kpiController.getKPIEsDslList();
		// TODO
		list.forEach(n -> {
			final Map<String, Object> map = n;
			logger.info("==============入队列+++" + n);
			Thread thread = new Thread(new Runnable() {
				@Override
				public synchronized void run() {
					try {
						final Iterator<String> iter = map.keySet().iterator();
						while (iter.hasNext()) {
							String key = iter.next();
							((Queue) QueueManager.getInstance().createQueue(key)).push(KPICalculator.getKpi(map));
						}
					} catch (Exception e) {
						logger.error("队列启动报错===");
						e.printStackTrace();
					}
					logger.info(Thread.currentThread().getName() + "======" + map);
				}

			});
			pool.execute(thread);
		});

	}

}
*/