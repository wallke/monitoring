package com.xwtech.es.model;

import java.util.List;
import java.util.Map;

/**
 * Created by zl on 2017/2/25. 
 * 预警动态
 * 预警类型 
 * 微环境请求类型TOP百分比
 * 接口请求类型TOP百分比
 * 共用Bean
 */
public class RecentWarnBean {
	/**
	 * 天数
	 */
	int duration;

	/**
	 * 预警信息
	 */
	List<Map<String, Object>> data;

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public List<Map<String, Object>> getData() {
		return data;
	}

	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}

}
