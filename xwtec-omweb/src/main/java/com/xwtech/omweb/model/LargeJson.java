package com.xwtech.omweb.model;

import java.util.Map;

/**
 * Created by zl on 2017/2/25.
 */
public class LargeJson {
	
	public int error;
	
	public String message;
   
	public Map<String, Object> result;

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, Object> getResult() {
		return result;
	}

	public void setResult(Map<String, Object> result) {
		this.result = result;
	}

	
	

}
