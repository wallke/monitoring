/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.collect;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 当前结点服务器的信息
 *
 * @author 张岩松
 */
public class ServerInfo {

    //服务器的IP地址
    private static String server_ip = "*";
    //服务器的端口号
    private static String server_port = "*";
    //IP读写锁
    private final static ReentrantReadWriteLock ipLock = new ReentrantReadWriteLock();
    //port读写锁
    private final static ReentrantReadWriteLock portLock = new ReentrantReadWriteLock();

    public static String getServer_ip() {
        String ip;
        try {
            ipLock.readLock().lock();
            ip = ServerInfo.server_ip;
        } finally {
            ipLock.readLock().unlock();
        }
        return ip;
    }

    public static void setServer_ip(String server_ip) {
        try {
            ipLock.writeLock().lock();
            ServerInfo.server_ip = server_ip;
        } finally {
            ipLock.writeLock().unlock();
        }
    }

    public static String getServer_port() {
        String port;
        try {
            portLock.readLock().lock();
            port = ServerInfo.server_port;
        } finally {
            portLock.readLock().unlock();
        }
        return port;
    }

    public static void setServer_port(String server_port) {
        try {
            portLock.writeLock().lock();
            ServerInfo.server_port = server_port;
        } finally {
            portLock.writeLock().unlock();
        }
    }

    public static class Server_Info {

        //服务器的IP地址
        private String server_ip;
        //服务器的端口号
        private String server_port;

        public String getServer_ip() {
            return server_ip;
        }

        public void setServer_ip(String server_ip) {
            this.server_ip = server_ip;
        }

        public String getServer_port() {
            return server_port;
        }

        public void setServer_port(String server_port) {
            this.server_port = server_port;
        }

    }
}
