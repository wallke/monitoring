/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.core;

import xwtec.servercm.builder.CollectTaskFinder;
import xwtec.servercm.builder.StopTask;
import xwtec.servercm.builder.BuildLogThreadPool;
import xwtec.servercm.heart.HealthReporter;
import xwtec.servercm.init.ConfigUpdater;
import xwtec.servercm.status.StatusManager;
import xwtec.servercm.store.LogListener;

/**
 * 线程管理器
 * <br>所有线程都是全局的
 *
 * @author 张岩松
 */
public class ThreadManager {

    //线程管理器实例
    private static final ThreadManager thread = new ThreadManager();
    //状态管理器
    private Thread statusManagerThread;
    //日志监听者
    private Thread logListenerThread;
    //采集任务发现者
    private Thread collectTaskFinderThread;
    //健康度汇报者
    private Thread healthReporterThread;

    /**
     * 得到线程管理器实例
     *
     * @return
     */
    public static ThreadManager instance() {
        return thread;
    }

    /**
     * 启动状态管理器
     *
     * @param statusManager
     */
    public void startStatusManager(StatusManager statusManager) {
        this.statusManagerThread = new Thread(statusManager);
        this.statusManagerThread.setName("StatusManager");
        this.statusManagerThread.setUncaughtExceptionHandler(new RuntimeExceptionHandler());
        this.statusManagerThread.start();
    }

    /**
     * 停止状态管理器
     */
    public void stopStatusManager() {
        if (this.statusManagerThread != null) {
            ConfigUpdater.init();
            this.statusManagerThread.interrupt();
            this.statusManagerThread = null;
        }
    }

    /**
     * 启动日志监听者
     *
     * @param logListener
     */
    public void startLogListener(LogListener logListener) {
        this.logListenerThread = new Thread(logListener);
        this.logListenerThread.setName("LogListener");
        this.logListenerThread.setUncaughtExceptionHandler(new RuntimeExceptionHandler());
        this.logListenerThread.start();
    }

    /**
     * 停止日志监听者
     */
    public void stopLogListener() {
        if (this.logListenerThread != null) {
            DataQueue.addLog("interrupted");
            this.logListenerThread = null;
        }
    }

    /**
     * 启动采集任务发现者
     *
     * @param collectTaskFinder
     */
    public void startCollectTaskFinder(CollectTaskFinder collectTaskFinder) {
        this.collectTaskFinderThread = new Thread(collectTaskFinder);
        this.collectTaskFinderThread.setName("CollectTaskFinder");
        this.collectTaskFinderThread.setUncaughtExceptionHandler(new RuntimeExceptionHandler());
        this.collectTaskFinderThread.start();
    }

    /**
     * 停止采集任务发现者
     */
    public void stopCollectTaskFinder() {
        if (this.collectTaskFinderThread != null) {
            DataQueue.addCollectTasks(new StopTask());
            this.collectTaskFinderThread = null;
        }
    }

    /**
     * 启动健康度汇报者
     *
     * @param healthReporter
     */
    public void startHealthReporter(HealthReporter healthReporter) {
        this.healthReporterThread = new Thread(healthReporter);
        this.healthReporterThread.setName("HealthReporter");
        this.healthReporterThread.setUncaughtExceptionHandler(new RuntimeExceptionHandler());
        this.healthReporterThread.start();
    }

    /**
     * 停止健康度汇报者
     */
    public void stopHealthReporter() {
        if (this.healthReporterThread != null) {
            this.healthReporterThread.interrupt();
            this.healthReporterThread = null;
        }
    }

    /**
     * 关闭采集应用，停止所有模块
     */
    public void close() {
        this.stopStatusManager();
        this.stopLogListener();
        this.stopCollectTaskFinder();
        BuildLogThreadPool.instance().close();
    }

}
