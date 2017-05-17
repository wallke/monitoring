package com.xwtech.jobs.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.xwtech.jobs.bootstrap.KPIEsConfig;
import com.xwtech.jobs.model.KPIInfos;

import io.netty.util.internal.StringUtil;

/**
 * 
 * get 请求获取KPI queryList
 * 
 * @author zl 2017/02/21
 *
 */

@RestController
public class KPIConnetionController {

//	private static String type = "";
//
//	private static String maxQueryTime = "";
//
//	public static List<Map<String, Object>> kpiQueryList = null;
//
//	@Autowired
//	RestTemplate restTemplate;
//
//	@Autowired
//	KPIEsConfig kpiConfig;
//
//	private static final Logger logger = LoggerFactory.getLogger(KPIConnetionController.class);
//
//	/**
//	 * 获取ESConfig配置Dsl
//	 *
//	 * @return
//	 */
//	//TODO update 时间比较
//	public List<Map<String, Object>> getKPIEsDslList() {
//
//		if (kpiQueryList == null) {
//			String[] esIPs = kpiConfig.getEsIPs();
//			String esPort = kpiConfig.getEsPort();
//			String esIndex = kpiConfig.getEsIndex();
//
//			kpiQueryList = new ArrayList<Map<String, Object>>();
//			Map<String, Object> map = this.getKPIEsConfig();
//
//			String type = String.valueOf(map.get(KPIInfos.type));
//			String maxQueryTime = String.valueOf(map.get(KPIInfos.maxQueryTime));
//
//			String result = "";
//
//			if (esIPs != null && esIPs.length > 0) {
//				for (int i = 0; i < esIPs.length; i++) {
//					try {
//						String url = KPIInfos.urlHttp + esIPs[i] + KPIInfos.splitPort + esPort + KPIInfos.split
//								+ esIndex + KPIInfos.split + type + KPIInfos.split + KPIInfos._search;
//						result = this.restESHttpGet(url);
//						if (!StringUtil.isNullOrEmpty(result)) {
//							JSONObject resultList = JSONObject.parseObject(result).getJSONObject(KPIInfos.hits);
//							logger.info("resultList ========"+resultList);
//							List<Object> jsonArray = resultList.getJSONArray(KPIInfos.hits);
//							jsonArray.forEach(n -> {
//								JSONObject finaJson = JSONObject.parseObject(String.valueOf(n)).getJSONObject(KPIInfos._source);
//								Iterator<String> it = finaJson.keySet().iterator();
//								Map<String, Object> finaMap = new HashMap<String, Object>();
//								while (it.hasNext()) {
//									String key = String.valueOf(it.next());
//									Object value = finaJson.get(key);
//									finaMap.put(key, value);
//								}
//								finaMap.put(KPIInfos.maxQueryTime, maxQueryTime);
//								kpiQueryList.add(finaMap);
//							});
//
//							if (!kpiQueryList.isEmpty()) {
//								return kpiQueryList;
//							}
//
//						}
//					} catch (Exception e) {
//						logger.error("解析esconfig dsl失败" + e.getMessage());
//					}
//				}
//			}
//		}
//
//		return kpiQueryList;
//	}
//
//	/**
//	 * 获取ESConfig配置
//	 *
//	 * @return
//	 */
//	public Map<String, Object> getKPIEsConfig() {
//
//		String[] esIPs = kpiConfig.getEsIPs();
//		String esPort  = kpiConfig.getEsPort();
//		String esIndex = kpiConfig.getEsIndex();
//		String esType  = kpiConfig.getEsType();
//		String esConfigID = kpiConfig.getEsConfigID();
//
//		Map<String, Object> map = new HashMap<String, Object>();
//		String result = "";
//
//		if (esIPs != null && esIPs.length > 0) {
//			for (int i = 0; i < esIPs.length; i++) {
//				try {
//					String url = KPIInfos.urlHttp + esIPs[i] + KPIInfos.splitPort + esPort + KPIInfos.split + esIndex
//							+ KPIInfos.split + esType + KPIInfos.split + esConfigID + KPIInfos.split + KPIInfos._source;
//					result = this.restESHttpGet(url);
//					if (!StringUtil.isNullOrEmpty(result)) {
//						JSONObject obj = JSONObject.parseObject(result).getJSONObject(KPIInfos.monitor_target);
//						if (obj.containsKey(KPIInfos.type) && !obj.getString(KPIInfos.type).equals(type)) {
//							type = obj.getString(KPIInfos.type);
//						}
//						if (obj.containsKey(KPIInfos.maxQueryTime)
//								&& !obj.getString(KPIInfos.maxQueryTime).equals(maxQueryTime)) {
//							maxQueryTime = obj.getString(KPIInfos.maxQueryTime);
//						}
//
//						if (!StringUtil.isNullOrEmpty(type) && !StringUtil.isNullOrEmpty(maxQueryTime)) {
//							map.put(KPIInfos.type, type);
//							map.put(KPIInfos.maxQueryTime, maxQueryTime);
//							return map;
//						} else {
//							continue;
//						}
//					} else {
//						continue;
//					}
//				} catch (Exception ex) {
//					logger.error("获取esconfig失败" + ex.getMessage());
//				}
//
//			}
//		}
//
//		return map;
//
//	}
//
//
//	/**
//	 * private rest get URL result
//	 *
//	 * @param Url
//	 * @return
//	 */
//	private String restESHttpGet(String Url) {
//		String result = "";
//		logger.info("url============" + Url);
//		result = restTemplate.getForObject(Url, String.class);
//		logger.info("result=========" + result);
//		return result;
//
//	}

}
