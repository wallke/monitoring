package com.xwtech.es.model;

import java.util.Date;

/**
 * Created by zl on 2017/2/15.
 * 2.主机日志bean
 */

public class HostBean {
	
	//日志类型	
	public String log_type;
	//监控点IP	
	public String host_ip;
	//关键时间	
	public String time;
	//处理器负载	
	public String processor_load_1;
	//处理器负载	
	public String processor_load_5;
	//处理器负载	
	public String processor_load_15;
	//上下文切换	
	public String context_switches;
	//中断	
	public String Interrupts;
	//用户态时间比	
	public String user_time;
	//IO等待时间	
	public String Iowait_time;
	//加权用户态时间比	
	public String nice_time;
	//系统态时间比	
	public String system_time;
	//软中断时间	
	public String softirq_time;
	//虚拟机时间	
	public String steal_time;
	//中断时间	
	public String interrupt_time;
	//空间时间占比	
	public String idle_time;
	//可用内存	
	public double available_memory;
	//空闲交换区	
	public double free_swap_space;
	//空闲交换区占比	
	public double free_swap_space_per;
	//总内存	
	public double total_memory;
	//总交换区	
	public double total_swap_space;
	//传入网络流量	
	public double incoming;
	//传出网络流量	
	public double outgoing;
	//空闲磁盘	
	public double free_disk;
	//空闲磁盘占比	
	public double free_disk_per;
	//空闲索引节点占比	
	public double free_inodes;
	//总磁盘空间	
	public double totall_disk;
	//已使用磁盘空间	
	public double used_disk;

    
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
	public String getProcessor_load_1() {
		return processor_load_1;
	}
	public void setProcessor_load_1(String processor_load_1) {
		this.processor_load_1 = processor_load_1;
	}
	public String getProcessor_load_5() {
		return processor_load_5;
	}
	public void setProcessor_load_5(String processor_load_5) {
		this.processor_load_5 = processor_load_5;
	}
	public String getProcessor_load_15() {
		return processor_load_15;
	}
	public void setProcessor_load_15(String processor_load_15) {
		this.processor_load_15 = processor_load_15;
	}
	public String getContext_switches() {
		return context_switches;
	}
	public void setContext_switches(String context_switches) {
		this.context_switches = context_switches;
	}
	public String getInterrupts() {
		return Interrupts;
	}
	public void setInterrupts(String interrupts) {
		Interrupts = interrupts;
	}
	public String getUser_time() {
		return user_time;
	}
	public void setUser_time(String user_time) {
		this.user_time = user_time;
	}
	public String getIowait_time() {
		return Iowait_time;
	}
	public void setIowait_time(String iowait_time) {
		Iowait_time = iowait_time;
	}
	public String getNice_time() {
		return nice_time;
	}
	public void setNice_time(String nice_time) {
		this.nice_time = nice_time;
	}
	public String getSystem_time() {
		return system_time;
	}
	public void setSystem_time(String system_time) {
		this.system_time = system_time;
	}
	public String getSoftirq_time() {
		return softirq_time;
	}
	public void setSoftirq_time(String softirq_time) {
		this.softirq_time = softirq_time;
	}
	public String getSteal_time() {
		return steal_time;
	}
	public void setSteal_time(String steal_time) {
		this.steal_time = steal_time;
	}
	public String getInterrupt_time() {
		return interrupt_time;
	}
	public void setInterrupt_time(String interrupt_time) {
		this.interrupt_time = interrupt_time;
	}
	public String getIdle_time() {
		return idle_time;
	}
	public void setIdle_time(String idle_time) {
		this.idle_time = idle_time;
	}
	
	public double getAvailable_memory() {
		return available_memory;
	}
	public void setAvailable_memory(double available_memory) {
		this.available_memory = available_memory;
	}
	public double getFree_swap_space() {
		return free_swap_space;
	}
	public void setFree_swap_space(double free_swap_space) {
		this.free_swap_space = free_swap_space;
	}
	public double getFree_swap_space_per() {
		return free_swap_space_per;
	}
	public void setFree_swap_space_per(double free_swap_space_per) {
		this.free_swap_space_per = free_swap_space_per;
	}
	public double getTotal_memory() {
		return total_memory;
	}
	public void setTotal_memory(double total_memory) {
		this.total_memory = total_memory;
	}
	public double getTotal_swap_space() {
		return total_swap_space;
	}
	public void setTotal_swap_space(double total_swap_space) {
		this.total_swap_space = total_swap_space;
	}
	public double getIncoming() {
		return incoming;
	}
	public void setIncoming(double incoming) {
		this.incoming = incoming;
	}
	public double getOutgoing() {
		return outgoing;
	}
	public void setOutgoing(double outgoing) {
		this.outgoing = outgoing;
	}
	public double getFree_disk() {
		return free_disk;
	}
	public void setFree_disk(double free_disk) {
		this.free_disk = free_disk;
	}
	public double getFree_disk_per() {
		return free_disk_per;
	}
	public void setFree_disk_per(double free_disk_per) {
		this.free_disk_per = free_disk_per;
	}
	public double getFree_inodes() {
		return free_inodes;
	}
	public void setFree_inodes(double free_inodes) {
		this.free_inodes = free_inodes;
	}
	public double getTotall_disk() {
		return totall_disk;
	}
	public void setTotall_disk(double totall_disk) {
		this.totall_disk = totall_disk;
	}
	public double getUsed_disk() {
		return used_disk;
	}
	public void setUsed_disk(double used_disk) {
		this.used_disk = used_disk;
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
