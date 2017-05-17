package com.xwtech.es;

public class ConstantsCode {
	
	   public static final String node_code  = "node_code";
	   
	   public static final String node_code_keyword  = "node_code.keyword";
	   
	   public static final String envir_ID  = "envir_ID";
	   
	   public static final String envir_ID_keyword = "envir_ID.keyword";
	   
	   
	   
	   public static final String error  = "error";
	   
	   public static final String result_info   = "result_info";
	   
	   public static final String name = "name";
	   
	   public static final String value = "value";
	   
	   public static final String total = "total";
	   
	   public static final String use = "use";
	   
	   public static final String in = "in";
	   
	   public static final String out = "out";
	   
	   public static final String interval = "interval";
	   
	   public static final String interval_sum = "interval_sum";
	   
	   
	   public static final String alert_level = "alert_level";
	   
	   
	   public static final String alert_time = "alert_time";
	   
	   public static final String target_template = "target_template";
	   
	   public static final String target_template_keyword = "target_template.keyword";
	   
	    /**
	     * 主机系统日志
	     */
	    public static final String log_type  = "log_type";
	    public static final String host_ip   = "host_ip";
	    public static final String time      = "time";
	    public static final String hostname  = "hostname";
	    public static final String host_boot_time  = "host_boot_time";
	    public static final String uptime           = "uptime";
	    public static final String sys_info         = "sys_info";
	    public static final String sys_processes    = "sys_processes";
	    public static final String running_processes = "running_processes";
	    
	    /**
	     * 主机日志
	     */
	    //日志类型	
		//public static final String log_type;
		//监控点IP	
		//public static final String host_ip;
		//关键时间	
		//public static final String time;
		//处理器负载	
		public static final String processor_load_1 = "processor_load_1";
		//处理器负载	
		public static final String processor_load_5 = "processor_load_5";
		//处理器负载	
		public static final String processor_load_15 = "processor_load_15";
		//上下文切换	
		public static final String context_switches = "context_switches";
		//中断	
		public static final String Interrupts = "Interrupts";
		//用户态时间比	
		public static final String user_time = "user_time";
		//IO等待时间	
		public static final String Iowait_time = "Iowait_time";
		//加权用户态时间比	
		public static final String nice_time = "nice_time";
		//系统态时间比	
		public static final String system_time = "system_time";
		//软中断时间	
		public static final String softirq_time ="softirq_time";
		//虚拟机时间	
		public static final String steal_time = "steal_time";
		//中断时间	
		public static final String interrupt_time = "interrupt_time";
		//空间时间占比	
		public static final String idle_time = "idle_time";
		//可用内存	
		public static final String available_memory = "available_memory";
		//空闲交换区	
		public static final String free_swap_space = "free_swap_space";
		//空闲交换区占比	
		public static final String free_swap_space_per = "free_swap_space_per";
		//总内存	
		public static final String total_memory = "total_memory";
		//总交换区	
		public static final String total_swap_space = "total_swap_space";
		//传入网络流量	
		public static final String incoming = "incoming";
		//传出网络流量	
		public static final String outgoing = "outgoing";
		//空闲磁盘	
		public static final String free_disk = "free_disk";
		//空闲磁盘占比	
		public static final String free_disk_per = "free_disk_per";
		//空闲索引节点占比	
		public static final String free_inodes = "free_inodes";
		//总磁盘空间	
		public static final String totall_disk = "totall_disk";
		//已使用磁盘空间	
		public static final String used_disk = "used_disk";
		/**
	     * Redis运行日志
	     */
		// 日志类型
		//public static final String log_type;
		// 监控点IP
		//public static final String host_ip;
		// 关键时间
		//public static final String time;
		// 内存总量
		public static final String used_memory = "used_memory";
		// 内存消耗峰值
		public static final String used_memory_peak = "used_memory_peak";
		// Lua使用内存
		public static final String used_memory_lua = "used_memory_lua";
		// 内存占用率
		public static final String mem_fragmentation_ratio = "mem_fragmentation_ratio";
		// 已连接客户端数
		public static final String connected_clients = "connected_clients";
		// 最长输出列表
		public static final String client_longest_output_list = "client_longest_output_list";
		// 最大输入缓存
		public static final String client_biggest_input_buf = "client_biggest_input_buf";
		// 阻塞客户端
		public static final String blocked_clients = "blocked_clients";
		// 系统CPU占用
		public static final String used_cpu_sys = "used_cpu_sys";
		// 用户CPU占用
		public static final String used_cpu_user = "used_cpu_user";
		// 后台系统CPU占用
		public static final String used_cpu_sys_children = "used_cpu_sys_children";
		// 后台用户CPU占用
		public static final String used_cpu_user_children = "used_cpu_user_children";
		// 总连接数
		public static final String total_connections_received  = "total_connections_received";
		// 总命令数
		public static final String total_commands_processed = "total_commands_processed";
		// 每秒命令数
		public static final String instantaneous_ops_per_sec = "instantaneous_ops_per_sec";
		// 被拒绝连接数
		public static final String rejected_connections = "rejected_connections";
		// 被删键数量
		public static final String expired_keys = "expired_keys";
		// 被驱逐键数量
		public static final String evicted_keys = "evicted_keys";
		// 成功查找键次数
		public static final String keyspace_hits = "keyspace_hits";
		// 查找键失败次数
		public static final String keyspace_misses = "keyspace_misses";
		
		
		/**
		 * Nginx运行日志
		 */
		// 日志类型
		//public static final String log_type;
		// 监控点IP
		//public static final String host_ip;
		// 关键时间
		//public static final String time;
		// 总连接数
		public static final String accepts = "accepts";
		// 活跃连接数量
		public static final String active = "active";
		// 读取连接数
		public static final String reading = "reading";
		// 等待连接数
		public static final String waiting = "waiting";
		// 响应连接数
		public static final String writing = "writing";
		// 成功握手数
		public static final String handled = "handled";
		// 总请求数
		public static final String requests = "requests";

}
