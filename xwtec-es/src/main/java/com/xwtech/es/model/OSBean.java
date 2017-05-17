package com.xwtech.es.model;

import java.util.Date;

/**
 * Created by zl on 2017/2/15.
 * 1.主机系统日志bean
 */

public class OSBean {
	
	public Object key;
    public String log_type;
    public String host_ip;
    public String time;
    public String hostname;
    public String host_boot_time;
    public String uptime;
    public String sys_info;
    public String sys_processes;
    public String running_processes;
    
    //搜索条件，开始日期结束日期 yyyy-MM-dd HH:mi:ss
    public Date beginTime;
    public Date endTime;
    
	public String getLog_type() {
		return log_type;
	}
	public void setLog_type(String log_type) {
		this.log_type = log_type;
	}
	public String getHost_ip() {
		return host_ip;
	}
	public void setHost_ip(String host_ip) {
		this.host_ip = host_ip;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getHost_boot_time() {
		return host_boot_time;
	}
	public void setHost_boot_time(String host_boot_time) {
		this.host_boot_time = host_boot_time;
	}
	public String getUptime() {
		return uptime;
	}
	public void setUptime(String uptime) {
		this.uptime = uptime;
	}
	public String getSys_info() {
		return sys_info;
	}
	public void setSys_info(String sys_info) {
		this.sys_info = sys_info;
	}
	public String getSys_processes() {
		return sys_processes;
	}
	public void setSys_processes(String sys_processes) {
		this.sys_processes = sys_processes;
	}
	public String getRunning_processes() {
		return running_processes;
	}
	public void setRunning_processes(String running_processes) {
		this.running_processes = running_processes;
	}
	public Object getKey() {
		return key;
	}
	public void setKey(Object key) {
		this.key = key;
	}
	public Date getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

}
