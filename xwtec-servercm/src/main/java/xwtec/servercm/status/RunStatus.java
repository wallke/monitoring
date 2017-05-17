/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.status;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 运行状态
 *
 * @author 张岩松
 */
public class RunStatus {

    //采集管理器运行状态
    private static Status status = Status.DISABLE;
    //启动状态读写锁
    private final static ReentrantReadWriteLock statusLock = new ReentrantReadWriteLock();

    /**
     * 获取状态
     *
     * @return
     */
    private static Status getStatus() {
        Status sta;
        try {
            statusLock.readLock().lock();
            sta = status;
        } finally {
            statusLock.readLock().unlock();
        }
        return sta;
    }

    /**
     * 设置状态
     *
     * @param status
     */
    private static void setStatus(Status status) {
        try {
            statusLock.writeLock().lock();
            RunStatus.status = status;
        } finally {
            statusLock.writeLock().unlock();
        }
    }

//状态接口----------------------------------------------------------------------/    
    /**
     * 休眠
     */
    public static void sleep() {
        setStatus(Status.SLEEP);
    }

    /**
     * 判断当前状态是否处于休眠状态
     *
     * @return
     */
    public static boolean isSleep() {
        return getStatus() == Status.SLEEP;
    }

    /**
     * 激活
     */
    public static void active() {
        setStatus(Status.ACTIVE);
    }

    /**
     * 判断当前采集管理器是否被激活
     *
     * @return
     */
    public static boolean isActive() {
        return getStatus() == Status.ACTIVE;
    }

    /**
     * 设为不可用
     */
    public static void disable() {
        setStatus(Status.DISABLE);
    }

    /**
     * 判断当前采集管理器是否不可用
     *
     * @return
     */
    public static boolean isDisable() {
        return getStatus() == Status.DISABLE;
    }

}
