package com.xwtech.omweb.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xwtech.omweb.dao.LargeScreenOnMapper;
import com.xwtech.omweb.service.ILargeScreenOnService;

@Service
public class LargeScreenOnServiceImpl implements ILargeScreenOnService {

	@Autowired
	LargeScreenOnMapper mapper;

	@Override
	public String queryEnvirName(String envir_ID) {
		String envir_name = "";
		try {
			envir_name = mapper.queryEnvirName(envir_ID);
		} catch (Exception e) {
			envir_name = "";
		}
		return envir_name;
	}
	
	@Override
	public String queryInterFaceName(String interFace_id){
		String interFace_name = "";
		try {
			interFace_name = mapper.queryInterFaceName(interFace_id);
		} catch (Exception e) {
			interFace_name = "";
		}
		return interFace_name;
	}

}
