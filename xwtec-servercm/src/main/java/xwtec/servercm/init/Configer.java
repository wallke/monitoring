/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.init;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import xwtec.servercm.core.CollectCtl;

/**
 * 配置信息管理器
 *
 * @author 张岩松
 */
public class Configer {

    //配置信息
    private static ConfigeInfo configeInfo;
    //配置信息读写锁
    private final static ReentrantReadWriteLock configeLock = new ReentrantReadWriteLock();
    //上级应用
    private static String last_AppServer_name = "";

    /**
     * 获取配置信息
     *
     * @return
     */
    public static ConfigeInfo instance() {
        ConfigeInfo confige;
        try {
            configeLock.readLock().lock();
            confige = configeInfo;
        } finally {
            configeLock.readLock().unlock();
        }
        return confige;
    }

    /**
     * 设置配置信息
     *
     * @param confige
     */
    public static void setConfigeInfo(ConfigeInfo confige) {
        try {
            configeLock.writeLock().lock();
            configeInfo = confige;
        } finally {
            configeLock.writeLock().unlock();
        }
    }

    /**
     * 加载采集控制
     */
    public static void loadCollectCtl() {
        CollectCtl.instance().loadCtl(configeInfo);
    }

    /**
     * 得到应用程序名
     *
     * @return
     */
    public static String getAppServer_name() {
        return configeInfo.getAppServer_name();
    }

    /**
     * 得到应用程序版本号
     *
     * @return
     */
    public static String getAppServer_ver() {
        return configeInfo.getAppServer_ver();
    }

    public static String getLast_AppServer_name() {
        return last_AppServer_name;
    }

    public static void setLast_AppServer_name(String last_AppServer_name) {
        Configer.last_AppServer_name = last_AppServer_name;
    }

    /**
     * 得到受控主机节点的清单
     *
     * @return
     */
    public static List<ConfigeInfo.AppHost> getHosts() {
        return configeInfo.getHosts();
    }

    /**
     * 得到环境清单
     *
     * @return
     */
    public static List<ConfigeInfo.Envir> getEnvirs() {
        return configeInfo.getEnvirs();
    }

    /**
     * 得到性能配置
     *
     * @return
     */
    public static ConfigeInfo.Capability getCapability() {
        return configeInfo.getCapability();
    }
}
