package com.xwtech.jobs.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by zl on 17/2/9.
 */
@ConfigurationProperties(prefix="zabbix")
public class ZabbixConfig {

	
    String api_url;
    String log_path;
    String sep_one;
    String sep_two;
    String user;
    String password;


	public String getApi_url() {
		return api_url;
	}

	public void setApi_url(String api_url) {
		this.api_url = api_url;
	}

	public String getLog_path() {
		return log_path;
	}

	public void setLog_path(String log_path) {
		this.log_path = log_path;
	}

	public String getSep_one() {
		return sep_one;
	}

	public void setSep_one(String sep_one) {
		this.sep_one = sep_one;
	}

	public String getSep_two() {
		return sep_two;
	}

	public void setSep_two(String sep_two) {
		this.sep_two = sep_two;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
    
    
    

}
