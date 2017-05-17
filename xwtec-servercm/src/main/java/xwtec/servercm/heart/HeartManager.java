/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.heart;

import java.util.concurrent.TimeUnit;
import xwtec.servercm.core.ThreadManager;
import xwtec.servercm.init.ConfigeInfo.Heart;
import xwtec.servercm.init.Configer;
import xwtec.servercm.status.RunStatus;

/**
 * 心跳管理器
 *
 * @author 张岩松
 */
public class HeartManager {

    //心跳管理器实例
    private static final HeartManager heartManager = new HeartManager();

    public static HeartManager instance() {
        return heartManager;
    }

    /**
     * 等待采集管理器被激活
     */
    private void waitActive() throws InterruptedException {
        while (!RunStatus.isActive()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    /**
     * 启动心跳
     */
    public void open() {
        try {
            this.waitActive();
            for (Heart heart : Configer.instance().getCapability().getHeart()) {
                if (heart.getHeart_type().equals("health")) {
                    HealthReporter healthReporter = new HealthReporter();
                    healthReporter.setHeart(heart);
                    ThreadManager.instance().startHealthReporter(healthReporter);
                }
                //在此增加更多类型的心跳
            }
        } catch (InterruptedException ex) {
        }
    }

    /**
     * 关闭心跳
     */
    public void close() {
        ThreadManager.instance().stopHealthReporter();
        //在此关闭更多类型的心跳模块
    }
}
