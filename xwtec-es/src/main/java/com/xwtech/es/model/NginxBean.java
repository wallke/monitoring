package com.xwtech.es.model;

import java.util.Date;

/**
 * Created by zl on 2017/2/15. 4.Nginx运行日志
 */

public class NginxBean {
	
	
	public Object key;
	// 日志类型
	public String log_type;
	// 监控点IP
	public String host_ip;
	// 关键时间
	public String time;
	// 总连接数
	public String accepts;
	// 活跃连接数量
	public String active;
	// 读取连接数
	public String reading;
	// 等待连接数
	public String waiting;
	// 响应连接数
	public String writing;
	// 成功握手数
	public String handled;
	// 总请求数
	public String requests;
	
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
	public String getAccepts() {
		return accepts;
	}
	public void setAccepts(String accepts) {
		this.accepts = accepts;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getReading() {
		return reading;
	}
	public void setReading(String reading) {
		this.reading = reading;
	}
	public String getWaiting() {
		return waiting;
	}
	public void setWaiting(String waiting) {
		this.waiting = waiting;
	}
	public String getWriting() {
		return writing;
	}
	public void setWriting(String writing) {
		this.writing = writing;
	}
	public String getHandled() {
		return handled;
	}
	public void setHandled(String handled) {
		this.handled = handled;
	}
	public String getRequests() {
		return requests;
	}
	public void setRequests(String requests) {
		this.requests = requests;
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
	public Object getKey() {
		return key;
	}
	public void setKey(Object key) {
		this.key = key;
	}
   
    
}
