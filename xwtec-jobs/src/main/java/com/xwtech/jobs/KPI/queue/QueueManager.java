package com.xwtech.jobs.KPI.queue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * KPI值队列
 * 
 * @author zl 2017/0220
 *
 */
public class QueueManager {

	private static QueueManager instance = null;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map queueMap = Collections.synchronizedMap(new HashMap());

	public static synchronized QueueManager getInstance() {
		if (instance == null) {
			instance = new QueueManager();
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	public Queue createQueue(String queueName) {
		if ((queueName == null) || (queueName.equals(""))) {
			throw new NullPointerException("队列名称不能为空");
		}
		Queue queue = (Queue) queueMap.get(queueName);

		if (queue != null) {
			return queue;
		} else {
			queue = (Queue) new LinkedQueue(queueName);
		}
		queueMap.put(queueName, queue);

		return queue;
	}

	public Queue getQueue(String queueName) {
		return (Queue) queueMap.get(queueName);
	}

	@SuppressWarnings("unused")
	public void removeQueue(String queueName) {
		Queue queue = (Queue) queueMap.remove(queueName);
		queue = null;
	}

	@SuppressWarnings("rawtypes")
	public Iterator getQueues() {
		return queueMap.values().iterator();
	}
	// TODO startTime，以及queryTime
}
