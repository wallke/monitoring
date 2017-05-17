package com.xwtech.jobs.service.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xwtech.jobs.bootstrap.ZabbixConfig;
import com.xwtech.jobs.service.IZabbixLogsTool;

@Service
public class ZabbixLogsToolService implements IZabbixLogsTool {
	@Autowired
	public ZabbixConfig config;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	static final String[] OS_ITEM_KEYS = new String[] { "system.boottime", "system.uptime", "system.uname",
			"proc.num[]", "proc.num[,,run]" };
	
	static final String[] HOST_ITEM_KEYS = new String[] { "system.cpu.load[percpu,avg1]",
			"system.cpu.load[percpu,avg5]", "system.cpu.load[percpu,avg15]", "system.cpu.switches", "system.cpu.intr",
			"system.cpu.util[,user]", "system.cpu.util[,iowait]", "system.cpu.util[,nice]", "system.cpu.util[,system]",
			"system.cpu.util[,softirq]", "system.cpu.util[,steal]", "system.cpu.util[,interrupt]",
			"system.cpu.util[,idle]", "vm.memory.size[available]", "system.swap.size[,free]",
			"system.swap.size[,pfree]", "vm.memory.size[total]", "system.swap.size[,total]", "net.if.in[eth0]",
			"net.if.out[eth0]", "vfs.fs.size[/,free]", "vfs.fs.size[/,pfree]", "vfs.fs.inode[/boot,pfree]",
			"vfs.fs.size[/,total]", "vfs.fs.size[/,used]" };
	
	static final String REDIS_PORTS_KEY = "redis.port";

	static final String[] REDIS_ITEM_KEYS = new String[] { "redis.info[#{port},used_memory]",
			"redis.info[#{port},used_memory_peak]", "redis.info[#{port},used_memory_lua]",
			"redis.info[#{port},mem_fragmentation_ratio]", "redis.info[#{port},connected_clients]",
			"redis.info[#{port},client_longest_output_list]", "redis.info[#{port},client_biggest_input_buf]",
			"redis.info[#{port},blocked_clients]", "redis.info[#{port},used_cpu_sys]", "redis.info[#{port},used_cpu_user]",
			"redis.info[#{port},used_cpu_sys_children]", "redis.info[#{port},used_cpu_user_children]",
			"redis.info[#{port},total_connections_received]", "redis.info[#{port},total_commands_processed]",
			"redis.info[#{port},instantaneous_ops_per_sec]", "redis.info[#{port},rejected_connections]",
			"redis.info[#{port},expired_keys]", "redis.info[#{port},evicted_keys]", "redis.info[#{port},keyspace_hits]",
			"redis.info[#{port},keyspace_misses]" };

	static final String NGINX_PORTS_KEY = "nginx.port";
	
	static final String[] NGINX_ITEM_KEYS = new String[] { "nginx.info[#{port},accepts]", "nginx.info[#{port},active]",
			"nginx.info[#{port},reading]", "nginx.info[#{port},waiting]", "nginx.info[#{port},writing]", "nginx.info[#{port},handled]",
			"nginx.info[#{port},requests]" };
	
	static final String[] TOMCAT_ITEM_KEYS = new String[] { "java.port.listen{#8080}", "java.thread{#8080}" };

	public void getZabbixLogs() {
		String LOG_PATH = config.getLog_path();
		String sep = StringEscapeUtils.unescapeJava(config.getSep_one());
		try {
			logger.info("--获取zabbix信息 start---" + LOG_PATH);
			String loginToken = getLoginToken();
			JSONArray host_result_array = getHostArray(loginToken);

			String os_log = "";
			String host_log = "";
			String redis_log = "";
			String nginx_log = "";
			// String tomcat_log = "";
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String now = dateFormat.format(new Date());

			for (int i = 0; i < host_result_array.size(); i++) {
				JSONObject host_result = host_result_array.getJSONObject(i);
				String host_id = host_result.getString("hostid");
				String host_name = host_result.getString("name");
				JSONArray host_interfaces = host_result.getJSONArray("interfaces");
				String host_ip = host_interfaces.getJSONObject(0).getString("ip");
				logger.info("--获取zabbix信息 host_ip---" + host_ip);
				if (!"127.0.0.1".equals(host_ip)) {

					String os_log_one = "";
					String host_log_one = "";
					

					Map<String, String> itemResultMap = getItemResultMap(host_id, loginToken);

					for (String item_key : OS_ITEM_KEYS) {
						String value = "0";
						if (itemResultMap.containsKey(item_key)) {
							value = itemResultMap.get(item_key);
						}
						if ("".equals(os_log_one)) {
							os_log_one += "z_os" + sep + host_ip + sep + now + sep + host_name;
						}
						os_log_one += sep + value;

					}
					for (String item_key : HOST_ITEM_KEYS) {
						String value = "0";
						if (itemResultMap.containsKey(item_key)) {
							value = itemResultMap.get(item_key);
						}
						if ("".equals(host_log_one)) {
							host_log_one += "z_host" + sep + host_ip + sep + now;
						}
						host_log_one += sep + value;

					}
					
					if(itemResultMap.containsKey(REDIS_PORTS_KEY)){
						String [] ports  = itemResultMap.get(REDIS_PORTS_KEY).split("\n");
						for(String port : ports){
							String redis_log_one = "";
							for (String item_key : REDIS_ITEM_KEYS) {
								String value = "0";
								item_key = item_key.replace("#{port}", port);
								if (itemResultMap.containsKey(item_key)) {
									value = itemResultMap.get(item_key);
								}
								
								if ("".equals(redis_log_one)) {
									redis_log_one += "z_redis" + sep + host_ip + sep + now;
								}
								redis_log_one += sep + value;

							}
							
							if (!"".equals(redis_log_one)) {
								redis_log += redis_log_one+"\n";
							}
						}
					}
					
					if(itemResultMap.containsKey(NGINX_PORTS_KEY)){
						String [] ports  = itemResultMap.get(NGINX_PORTS_KEY).split("\n");
						for(String port : ports){
							String nginx_log_one = "";
							for (String item_key : NGINX_ITEM_KEYS) {
								String value = "0";
								item_key = item_key.replace("#{port}", port);
								if (itemResultMap.containsKey(item_key)) {
									value = itemResultMap.get(item_key);
								}
								if ("".equals(nginx_log_one)) {
									nginx_log_one += "z_nginx" + sep + host_ip + sep + now;
								}
								nginx_log_one += sep + value;

							}
							
							if (!"".equals(nginx_log_one)) {
								nginx_log += nginx_log_one+"\n";
							}
						}
					}
					
					if (!"".equals(os_log_one)) {
						os_log += os_log_one + "\n";
					}
					
					if (!"".equals(host_log_one)) {
						host_log += host_log_one+"\n";
					}
					
					
					
					// tomcat_log += "\n";
				}
			}

			if(!"".equals(os_log)){
				write(os_log, LOG_PATH + "os." + now + ".log");
			}
			
			if(!"".equals(host_log)){
				write(host_log, LOG_PATH + "host." + now + ".log");
			}
			
			if(!"".equals(redis_log)){
				write(redis_log, LOG_PATH + "redis." + now + ".log");
			}
			
			if(!"".equals(nginx_log)){
				write(nginx_log, LOG_PATH + "nginx." + now + ".log");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("--获取zabbix信息 error---" + e.getMessage());
		}

	}

	private String getLoginToken() {
		String USER = config.getUser();
		String PASSWORD = config.getPassword();
		JSONObject login_param = new JSONObject();
		login_param.put("jsonrpc", "2.0");
		login_param.put("method", "user.login");
		JSONObject login_param_params = new JSONObject();
		login_param_params.put("user", USER);
		login_param_params.put("password", PASSWORD);
		login_param.put("params", login_param_params);
		login_param.put("id", "1");
		login_param.put("auth", null);
		JSONObject loginResult = getZabbixApiResult(login_param);
//		System.out.println("loginResult = " + loginResult);
		String loginToken = loginResult.getString("result");
		return loginToken;
	}

	private JSONArray getHostArray(String loginToken) {
		JSONObject host_param = new JSONObject();
		host_param.put("jsonrpc", "2.0");
		host_param.put("method", "host.get");
		JSONObject host_param_params = new JSONObject();
		host_param_params.put("output", new String[] { "hostid", "name" });
		host_param_params.put("selectInterfaces", new String[] { "ip" });
		host_param_params.put("selectGroups", "extend");
		host_param.put("params", host_param_params);
		host_param.put("id", "1");
		host_param.put("auth", loginToken);
		JSONObject hostResult = getZabbixApiResult(host_param);
//		System.out.println("hostResult = " + hostResult);
		JSONArray host_result_array = hostResult.getJSONArray("result");
		return host_result_array;
	}

	
	
	/**
	 * zabbix item get
	 * @param host_id
	 * @param loginToken
	 * @return
	 */
	private Map<String, String> getItemResultMap(String host_id, String loginToken) {
		String sep_inner = StringEscapeUtils.unescapeJava(config.getSep_two());
		JSONObject item_param = new JSONObject();
		item_param.put("jsonrpc", "2.0");
		item_param.put("method", "item.get");
		JSONObject item_param_params = new JSONObject();
		item_param_params.put("output", new String[] { "itemid", "name", "key_", "lastvalue" });
		item_param_params.put("hostids", host_id);
		JSONObject search_param = new JSONObject();
		search_param.put("key_", "");
		item_param_params.put("search", search_param);
		item_param_params.put("sortfield", "key_");
		item_param.put("params", item_param_params);
		item_param.put("id", "1");
		item_param.put("auth", loginToken);
		JSONObject itemResult = getZabbixApiResult(item_param);
//		System.out.println("itemResult = " + itemResult);

		JSONArray item_result_array = itemResult.getJSONArray("result");
		Map<String, String> itemResultMap = new HashMap<String, String>();
		for (int j = 0; j < item_result_array.size(); j++) {
			JSONObject item_result = item_result_array.getJSONObject(j);
			String key = item_result.getString("key_");
			String lastvalue = item_result.getString("lastvalue");
			if (itemResultMap.containsKey(key)) {
				String value = itemResultMap.get(key);
				itemResultMap.put(key, value + sep_inner + lastvalue);
			} else {
				itemResultMap.put(key, lastvalue);
			}
		}

		return itemResultMap;
	}

	private JSONObject getZabbixApiResult(JSONObject param) {
		try {
			// 创建连接
			String ZABBIX_API_URL = config.getApi_url();
			URL url = new URL(ZABBIX_API_URL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-Type", "application/json");

			connection.connect();

			// POST请求
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.writeBytes(param.toString());
			out.flush();
			out.close();

			// 读取响应
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String lines;
			StringBuffer sb = new StringBuffer("");
			while ((lines = reader.readLine()) != null) {
				lines = new String(lines.getBytes(), "utf-8");
				sb.append(lines);
			}
			reader.close();
			// 断开连接
			connection.disconnect();

			return JSONObject.parseObject(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("--获取zabbix信息 连接异常---" + e.getMessage());
			return null;
		}
	}

	private static void write(String str, String filePath) {
		if ("".equals(str))
			return;
		filePath = filePath.replaceAll("\\\\", "/");
		File file = new File(filePath);
		StringReader fis = null;
		FileOutputStream fos = null;
		File path = new File(file.getParent());
		try {
			if (!path.exists()) {
				path.mkdirs();
			}
			byte[] bytes = str.getBytes("utf-8");
			fos = new FileOutputStream(file);
			fos.write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
