package com.xwtech.omweb.dao;

import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author zl  20170227
 *
 */
@Mapper
public interface LargeScreenOnMapper {

	/**
	 * 根据微环境编码 获取微环境名称
	 * @param envir_ID
	 * @return
	 */
	String queryEnvirName(String envir_ID);
	
	
    String queryInterFaceName(String interFace_id);
}
