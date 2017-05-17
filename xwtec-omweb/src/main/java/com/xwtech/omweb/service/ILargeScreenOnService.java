package com.xwtech.omweb.service;

/**
 * @author zl 2017/2/27
 *
 */
public interface ILargeScreenOnService {
	/**
	 * 
	 * 根据微环境ID 获取微环境name
	 * @param envir_ID
	 * @return
	 */
	public String queryEnvirName(String envir_ID);
	
	
	public String queryInterFaceName(String interFace_id);
}