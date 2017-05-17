package com.xwtech.admin;

import org.apache.http.ProtocolVersion;

import javax.management.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Set;

/**
 * Created by zhangq on 2017/1/19.
 */
public class InternetProtocol {


    private   static Integer port;

    private static String address;


    /**
     * 获取端口
     * @return
     */
    public static  Integer getPort(){
        if(port == null){
            port = getPortByMBean();
        }
        return port;
    }


    /**
     * 获取HostAddress
     * @return
     */
    public static String getAddress(){
        if(null == address){
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

            MBeanServer mBeanServer = null;
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
                if (SSLEnabled) {// tomcat6开始用SSLEnabled
                    secureValue = true;// SSLEnabled=true但secure未配置的情况
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
            String port = String.valueOf(server.getAttribute(serverrt, "ListenPort"));
            if (port != null) {
                return Integer.valueOf(port);
            }

//            String listenAddr = (String) server.getAttribute(serverrt, "ListenAddress");
//            String[] tempAddr = listenAddr.split("/");
//            if (tempAddr.length == 1) {
//                listenAddr = tempAddr[0];
//            } else if (tempAddr[tempAddr.length - 1].trim().length() != 0) {
//                listenAddr = tempAddr[tempAddr.length - 1];
//            } else if (tempAddr.length > 2) {
//                listenAddr = tempAddr[tempAddr.length - 2];
//            }

        } catch (Exception e) {
            //            logger.error(e.getMessage(), e);
        }

        return null;
    }


    /* 获取本地IP地址
     * @return
     * @throws UnknownHostException
     */
    private static String getHostAddress() {
        Enumeration<NetworkInterface> netInterfaces = null;
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
        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
        }
        return "";
    }


}
