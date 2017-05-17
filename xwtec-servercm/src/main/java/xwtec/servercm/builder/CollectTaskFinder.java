/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.builder;

import xwtec.servercm.core.DataQueue;

/**
 * 采集消息发现者
 *
 * @author 张岩松
 */
public class CollectTaskFinder implements Runnable {

    @Override
    public void run() {
        try {
            while (true) {
                CollectTask collectTask = DataQueue.getCollectTask();
                if (collectTask instanceof StopTask) {
                    throw new InterruptedException();
                }
                BuildLogThreadPool.instance().exec(collectTask);
            }
        } catch (InterruptedException ex) {
        }
    }

}
