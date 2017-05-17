package com.xwtech.es.model;

import java.util.Date;

/**
 * Created by zl on 2017/2/15. 3.Redis运行日志Bean
 */

public class RedisBean {
	
	
	public Object key;

	// 日志类型
	public String log_type;
	// 监控点IP
	public String host_ip;
	// 关键时间
	public String time;
	// 内存总量
	public String used_memory;
	// 内存消耗峰值
	public String used_memory_peak;
	// Lua使用内存
	public String used_memory_lua;
	// 内存占用率
	public String mem_fragmentation_ratio;
	// 已连接客户端数
	public String connected_clients;
	// 最长输出列表
	public String client_longest_output_list;
	// 最大输入缓存
	public String client_biggest_input_buf;
	// 阻塞客户端
	public String blocked_clients;
	// 系统CPU占用
	public String used_cpu_sys;
	// 用户CPU占用
	public String used_cpu_user;
	// 后台系统CPU占用
	public String used_cpu_sys_children;
	// 后台用户CPU占用
	public String used_cpu_user_children;
	// 总连接数
	public String total_connections_received;
	// 总命令数
	public String total_commands_processed;
	// 每秒命令数
	public String instantaneous_ops_per_sec;
	// 被拒绝连接数
	public String rejected_connections;
	// 被删键数量
	public String expired_keys;
	// 被驱逐键数量
	public String evicted_keys;
	// 成功查找键次数
	public String keyspace_hits;
	// 查找键失败次数
	public String keyspace_misses;

	// 搜索条件，开始日期结束日期 yyyy-MM-dd HH:mi:ss
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
	public String getUsed_memory() {
		return used_memory;
	}
	public void setUsed_memory(String used_memory) {
		this.used_memory = used_memory;
	}
	public String getUsed_memory_peak() {
		return used_memory_peak;
	}
	public void setUsed_memory_peak(String used_memory_peak) {
		this.used_memory_peak = used_memory_peak;
	}
	public String getUsed_memory_lua() {
		return used_memory_lua;
	}
	public void setUsed_memory_lua(String used_memory_lua) {
		this.used_memory_lua = used_memory_lua;
	}
	public String getMem_fragmentation_ratio() {
		return mem_fragmentation_ratio;
	}
	public void setMem_fragmentation_ratio(String mem_fragmentation_ratio) {
		this.mem_fragmentation_ratio = mem_fragmentation_ratio;
	}
	public String getConnected_clients() {
		return connected_clients;
	}
	public void setConnected_clients(String connected_clients) {
		this.connected_clients = connected_clients;
	}
	public String getClient_longest_output_list() {
		return client_longest_output_list;
	}
	public void setClient_longest_output_list(String client_longest_output_list) {
		this.client_longest_output_list = client_longest_output_list;
	}
	public String getClient_biggest_input_buf() {
		return client_biggest_input_buf;
	}
	public void setClient_biggest_input_buf(String client_biggest_input_buf) {
		this.client_biggest_input_buf = client_biggest_input_buf;
	}
	public String getBlocked_clients() {
		return blocked_clients;
	}
	public void setBlocked_clients(String blocked_clients) {
		this.blocked_clients = blocked_clients;
	}
	public String getUsed_cpu_sys() {
		return used_cpu_sys;
	}
	public void setUsed_cpu_sys(String used_cpu_sys) {
		this.used_cpu_sys = used_cpu_sys;
	}
	public String getUsed_cpu_user() {
		return used_cpu_user;
	}
	public void setUsed_cpu_user(String used_cpu_user) {
		this.used_cpu_user = used_cpu_user;
	}
	public String getUsed_cpu_sys_children() {
		return used_cpu_sys_children;
	}
	public void setUsed_cpu_sys_children(String used_cpu_sys_children) {
		this.used_cpu_sys_children = used_cpu_sys_children;
	}
	public String getUsed_cpu_user_children() {
		return used_cpu_user_children;
	}
	public void setUsed_cpu_user_children(String used_cpu_user_children) {
		this.used_cpu_user_children = used_cpu_user_children;
	}
	public String getTotal_connections_received() {
		return total_connections_received;
	}
	public void setTotal_connections_received(String total_connections_received) {
		this.total_connections_received = total_connections_received;
	}
	public String getTotal_commands_processed() {
		return total_commands_processed;
	}
	public void setTotal_commands_processed(String total_commands_processed) {
		this.total_commands_processed = total_commands_processed;
	}
	public String getInstantaneous_ops_per_sec() {
		return instantaneous_ops_per_sec;
	}
	public void setInstantaneous_ops_per_sec(String instantaneous_ops_per_sec) {
		this.instantaneous_ops_per_sec = instantaneous_ops_per_sec;
	}
	public String getRejected_connections() {
		return rejected_connections;
	}
	public void setRejected_connections(String rejected_connections) {
		this.rejected_connections = rejected_connections;
	}
	public String getExpired_keys() {
		return expired_keys;
	}
	public void setExpired_keys(String expired_keys) {
		this.expired_keys = expired_keys;
	}
	public String getEvicted_keys() {
		return evicted_keys;
	}
	public void setEvicted_keys(String evicted_keys) {
		this.evicted_keys = evicted_keys;
	}
	public String getKeyspace_hits() {
		return keyspace_hits;
	}
	public void setKeyspace_hits(String keyspace_hits) {
		this.keyspace_hits = keyspace_hits;
	}
	public String getKeyspace_misses() {
		return keyspace_misses;
	}
	public void setKeyspace_misses(String keyspace_misses) {
		this.keyspace_misses = keyspace_misses;
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
