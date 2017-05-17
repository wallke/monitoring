/*
 * 队列
 */
package xwtec.servercm.core;

import xwtec.servercm.builder.CollectTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import xwtec.servercm.init.Configer;

/**
 * 队列
 *
 * @author 张岩松
 */
public class DataQueue {

    //采集任务的队列
    public static final BlockingQueue<CollectTask> collectTasks = new LinkedBlockingQueue<CollectTask>();
    //存储任务的队列
    public static final BlockingQueue<String> logs = new LinkedBlockingQueue<String>();

    /**
     * 添加采集任务
     *
     * @param collectTask
     */
    public static void addCollectTasks(CollectTask collectTask) {
        try {
            if (Configer.instance().getCapability().getLogsBuffer_max_size() > DataQueue.collectTasks.size()) {
                DataQueue.collectTasks.put(collectTask);
            } else {
            }
        } catch (InterruptedException ex) {
        }
    }

    /**
     * 从队列中得到采集任务
     *
     * @return
     */
    public static CollectTask getCollectTask() {
        try {
            return DataQueue.collectTasks.take();
        } catch (InterruptedException ex) {
            return null;
        }
    }

    /**
     * 添加日志
     *
     * @param log
     */
    public static void addLog(String log) {
        try {
            if (Configer.instance().getCapability().getLogsBuffer_max_size() > DataQueue.logs.size()) {
                DataQueue.logs.put(log);
            } else {
            }
        } catch (InterruptedException ex) {
        }
    }

    /**
     * 从队列中得到日志记录
     *
     * @return
     */
    public static String getLog() {
        try {
            return DataQueue.logs.take();
        } catch (InterruptedException ex) {
            return null;
        }
    }
}
