package com.xwtech.es.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/3/22 0022.
 */
public class HealthBean {

    //应用名称
    private String appServer_name;
    //应用版本号
    private String appServer_ver;
    //时间
    private Date time;
    //主机IP
    private String server_ip;
    //主机端口
    private String server_port;
    //主机状态
    private String cm_status;
    //状态描述
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getAppServer_name() {
        return appServer_name;
    }

    public void setAppServer_name(String appServer_name) {
        this.appServer_name = appServer_name;
    }

    public String getAppServer_ver() {
        return appServer_ver;
    }

    public void setAppServer_ver(String appServer_ver) {
        this.appServer_ver = appServer_ver;
    }


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

    public String getCm_status() {
        return cm_status;
    }

    public void setCm_status(String cm_status) {
        this.cm_status = cm_status;
    }

    public static class  AppBean
    {
        //应用名称
        private String appServer_name;

        List<HostBean> hostList;

        public String getAppServer_name() {
            return appServer_name;
        }

        public void setAppServer_name(String appServer_name) {
            this.appServer_name = appServer_name;
        }

        public List<HostBean> getHostList() {
            return hostList;
        }

        public void setHostList(List<HostBean> hostList) {
            this.hostList = hostList;
        }
    }

    public static class HostBean
    {
        //主机IP
        private String server_ip;
        List<HealthBean> portList = new ArrayList<HealthBean>();

        //端口总数
        private int portCount;
        //成功数量
        private int successCount;
        //异常数量
        private int errorCount;

        public int getSuccessCount() {
            return successCount;
        }

        public void setSuccessCount(int successCount) {
            this.successCount = successCount;
        }

        public int getErrorCount() {
            return errorCount;
        }

        public void setErrorCount(int errorCount) {
            this.errorCount = errorCount;
        }

        public List<HealthBean> getPortList() {
            return portList;
        }

        public void setPortList(List<HealthBean> portList) {
            this.portList = portList;
        }

        public int getPortCount() {
            return portCount;
        }

        public void setPortCount(int portCount) {
            this.portCount = portCount;
        }

        public String getServer_ip() {
            return server_ip;
        }

        public void setServer_ip(String server_ip) {
            this.server_ip = server_ip;
        }
    }

}
