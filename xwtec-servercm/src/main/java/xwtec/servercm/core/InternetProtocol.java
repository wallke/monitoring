/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.core;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.naming.InitialContext;

/**
 * 获取IP和端口
 *
 * @author 张强
 */
public class InternetProtocol {

    //当前主机地址
    private static String address;
    //当前应用在容器中的端口号
    private static Integer port;

    /**
     * 获取本机端口
     *
     * @return
     */
    public static Integer getPort() {
        if (port == null) {
            port = getPortByMBean();
        }
        return port;
    }

    /**
     * 获取HostAddress
     *
     * @return
     */
    public static String getAddress() {
        if (null == address) {
            address = getHostAddress();
        }
        return address;
    }

    /**
     * 根据协议和scheme获取服务端口号 使用MBean获取tomcat和jboss端口
     *
     * @return
     */
    private static Integer getPortByMBean() {

        try {
            MBeanServer mBeanServer;
            ArrayList<MBeanServer> mBeanServers = MBeanServerFactory.findMBeanServer(null);
            mBeanServer = mBeanServers.size() > 0 ? mBeanServers.get(0) : null;
            if (mBeanServer == null) {
                throw new IllegalStateException("没有发现JVM中关联的MBeanServer.");
            }
            //获取 tomcat  Connector  多个端口
            Set<ObjectName> objectNames = mBeanServer.queryNames(new ObjectName("*:type=Connector,*"), null);
            if (objectNames == null || objectNames.size() <= 0) {
                throw new IllegalStateException("没有发现JVM中关联的MBeanServer : " + mBeanServer.getDefaultDomain() + " 中的对象名称.");
            }
            //返回 符合条件端口 protocol = "HTTP/1.1",scheme = "http"
            for (ObjectName objectName : objectNames) {
                String protocol = (String) mBeanServer.getAttribute(objectName, "protocol");
                String _scheme = (String) mBeanServer.getAttribute(objectName, "scheme");
                Boolean secureValue = (Boolean) mBeanServer.getAttribute(objectName, "secure");
                Boolean SSLEnabled = (Boolean) mBeanServer.getAttribute(objectName, "SSLEnabled");
                // tomcat6开始用SSLEnabled
                if (SSLEnabled) {
                    // SSLEnabled=true但secure未配置的情况
                    secureValue = true;
                    _scheme = "https";
                } else {
                    _scheme = "http";
                }
                if ("HTTP/1.1".equals(protocol) || protocol.contains("http")) {
                    if ("https".equals(_scheme) && secureValue) {
                        return (Integer) mBeanServer.getAttribute(objectName, "port");
                    } else if ("http".equalsIgnoreCase(_scheme) && !secureValue) {
                        return (Integer) mBeanServer.getAttribute(objectName, "port");
                    }
                }
            }
            //获取 weblogic 端口号
            InitialContext ctx = new InitialContext();
            MBeanServer server = (MBeanServer) ctx.lookup("java:comp/env/jmx/runtime");
            ObjectName objName = new ObjectName("com.bea:Name=RuntimeService,Type=weblogic.management.mbeanservers.runtime.RuntimeServiceMBean");
            ObjectName serverrt = (ObjectName) server.getAttribute(objName, "ServerRuntime");
            String listen_port = String.valueOf(server.getAttribute(serverrt, "ListenPort"));
            if (listen_port != null) {
                return Integer.valueOf(listen_port);
            }
        } catch (Exception ex) {
        }
        return null;
    }


    /* 获取本地IP地址
     * @return
     */
    private static String getHostAddress() {
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress ip = ips.nextElement();
                    if (ip.isSiteLocalAddress()) {
                        return ip.getHostAddress();
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (SocketException e) {
        } catch (UnknownHostException e) {
        }
        return "";
    }

}
