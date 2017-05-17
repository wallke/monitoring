/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.store;

import xwtec.servercm.core.DataQueue;

/**
 * 日志监听者
 *
 * @author 张岩松
 */
public class LogListener implements Runnable {

    //日志存储接口
    public static LogStore logStore;

    @Override
    public void run() {
        try {
            while (true) {
                String log = DataQueue.getLog();
                if (log != null) {
                    if (log.equals("interrupted")) {
                        throw new InterruptedException();
                    }
                    if (logStore != null) {
                        logStore.saveToFile(log);
                    }
                }
            }
        } catch (InterruptedException ex) {
        }
    }

}
