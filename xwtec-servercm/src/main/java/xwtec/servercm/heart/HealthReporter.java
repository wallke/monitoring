/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.heart;

import java.util.concurrent.TimeUnit;
import xwtec.servercm.collect.ServerInfo;
import xwtec.servercm.init.ConfigeInfo.Heart;
import xwtec.servercm.init.Configer;

/**
 * 心跳管理器:健康度汇报者
 *
 * s* @author 张岩松
 */
public class HealthReporter implements Runnable {

    //心跳配置
    private Heart heart;

    public Heart getHeart() {
        return heart;
    }

    public void setHeart(Heart heart) {
        this.heart = heart;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                if (this.heart.isCollect_heart()) {
                    this.reportHealth();
                }
                TimeUnit.SECONDS.sleep(this.heart.getHeart_interval());
            }
        } catch (InterruptedException ex) {
            this.reportHealth();
        }
    }

    /**
     * 汇报健康度
     */
    private void reportHealth() {
        FaultReporter.instance().setAppServer_name(Configer.getAppServer_name());
        FaultReporter.instance().setAppServer_ver(Configer.getAppServer_ver());
        FaultReporter.instance().setServer_ip(ServerInfo.getServer_ip());
        FaultReporter.instance().setServer_port(ServerInfo.getServer_port());
        FaultReporter.report();
    }
}
