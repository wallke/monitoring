/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.builder;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 创建日志线程池
 *
 * @author 张岩松
 */
public class BuildLogThreadPool {

    //建造日志线程池实例
    private static final BuildLogThreadPool buildLogThreadPool = new BuildLogThreadPool();
    //线程池
    private ThreadPoolExecutor executor;

    /**
     * 获得创建日志线程池实例
     *
     * @return
     */
    public static BuildLogThreadPool instance() {
        return buildLogThreadPool;
    }

    /**
     * 创建线程池时指定大小
     *
     * @param size
     */
    public void open(int size) {
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(size);
    }

    /**
     * 关闭线程池
     */
    public void close() {
        if (this.executor != null) {
            this.executor.shutdown();
        }
    }

    /**
     * 执行采集任务
     *
     * @param collectTask
     */
    public void exec(CollectTask collectTask) {
        this.executor.execute(collectTask);
    }

}
