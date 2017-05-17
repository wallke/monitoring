/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.status;

import java.util.concurrent.TimeUnit;
import xwtec.servercm.builder.BuildLogThreadPool;
import xwtec.servercm.heart.HeartManager;
import xwtec.servercm.core.Print;
import xwtec.servercm.init.ConfigException;
import xwtec.servercm.init.ConfigUpdater;
import xwtec.servercm.init.Configer;
import xwtec.servercm.init.InitException;
import xwtec.servercm.init.InitParamInfo;

/**
 * 状态管理器
 *
 * @author 张岩松
 */
public class StatusManager implements Runnable {

    //间隔10秒做一次检测
    private int update_interval = 10;
    //日志构造线程池大小
    private int buildLog_thread_size;

    @Override
    public void run() {
        try {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                try {
                    //加载初始化参数
                    InitParamInfo.init();
                    if (InitParamInfo.isValid()) {
                        //加载采集控制参数
                        if (ConfigUpdater.checkUpdate()) {
                            this.startSleep();
                            if (ConfigUpdater.update()) {
                                //若更新成功则获得更新间隔时间
                                this.update_interval = Configer.instance().getCapability().getUpdate_interval();
                            }
                            this.endSleep();
                        }
                    }
                } catch (InitException ex) {
                    //加载初始化参数失败
                    RunStatus.disable();
                    Print.exception(ex.getMessage());
                } catch (ConfigException ex) {
                    Print.exception(ex.getMessage());
                }
                TimeUnit.SECONDS.sleep(this.update_interval);
            }
        } catch (InterruptedException ex) {
            RunStatus.disable();
            Print.exception("采集管理器停止运行");
        }
    }

    /**
     * 开始休眠
     */
    private void startSleep() {
        RunStatus.sleep();
        HeartManager.instance().close();
    }

    /**
     * 休眠结束，重新激活
     */
    private void endSleep() {
        RunStatus.active();
        this.restart_buildlog_Thread_pool();
        HeartManager.instance().open();
        Print.message("采集管理器启动成功");
    }

    /**
     * 重启建造日志线程池
     */
    private void restart_buildlog_Thread_pool() {
        int thread_pool_size = Configer.instance().getCapability().getBuildLog_thread_size();
        if (buildLog_thread_size != thread_pool_size) {
            buildLog_thread_size = thread_pool_size;
            BuildLogThreadPool.instance().close();
            BuildLogThreadPool.instance().open(buildLog_thread_size);
        }
    }
}
