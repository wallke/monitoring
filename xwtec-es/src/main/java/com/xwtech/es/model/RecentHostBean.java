package com.xwtech.es.model;

import java.util.List;
import java.util.Map;

/**
 * Created by zl on 2017/2/25.
 *  主机资源消耗 CPU 
 *  磁盘总空间 / 利用率
 *  网络流量
 *  内存
 *  共用Bean
 */
public class RecentHostBean {

	String ip;

	/**
	 * 预警信息
	 */
	List<Map<String, Object>> data;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public List<Map<String, Object>> getData() {
		return data;
	}

	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}
}
