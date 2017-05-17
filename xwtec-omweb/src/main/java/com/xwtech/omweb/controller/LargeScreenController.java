package com.xwtech.omweb.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.xwtech.es.ConstantsCode;
import com.xwtech.es.model.RecentHostBean;
import com.xwtech.es.model.RecentWarnBean;
import com.xwtech.es.service.LargeScreenService;
import com.xwtech.omweb.model.LargeJson;
import com.xwtech.omweb.model.ServiceDescription;
import com.xwtech.omweb.service.ILargeScreenOnService;
import com.xwtech.omweb.service.IServiceDescription;

/**
 * 
 * @author zl 20170227
 *
 */
@Controller
@RequestMapping("_largeApi")
public class LargeScreenController {

	@Autowired
	LargeScreenService service;

	@Autowired
	ILargeScreenOnService onService;

	@Autowired
	IServiceDescription serviceDescription;

	private static Logger logger = LoggerFactory.getLogger(LargeScreenController.class);

	// 天数时间
	private static final int day = 7;

	private static final int daysbefore = 0;

	private static final int mins = 5;
	// 系统时间
	private static final String time = "time";
	// 安全运行时间
	private static final String aqyxljsj = "aqyxljsj";
	// 无预警累计
	private static final String wyklj = "wyklj";
	// 服务总数
	private static final String fwzs = "fwzs";
	// 错误率
	private static final String cwl = "cwl";
	// 近日预警
	private static final String jryj = "jryj";
	// 预警动态
	private static final String yjdt = "yjdt";
	// 预警类型
	private static final String yjlx = "yjlx";
	// 微环境请求次数TOP
	private static final String whjqqsc = "whjqqsc";
	// 接口请求时长TOP
	private static final String jkqqsc = "jkqqsc";
	// 近X天微环境请求类型
	private static final String whjqqlx = "whjqqlx";
	// 近X天接口请求类型
	private static final String jkqqlx = "jkqqlx";
	// 主机资源消耗 CPU
	private static final String zjzyxh = "zjzyxh";
	// 磁盘总空间 / 利用率
	private static final String cp = "cp";
	// 网络流量
	private static final String wlll = "wlll";
	// 内存
	private static final String nc = "nc";

	@RequestMapping(value = "")
	@ResponseBody
	public JSONPObject getDate(HttpServletRequest request, String methods, String callback) throws Exception {
		LargeJson bean = new LargeJson();
		JSONPObject jsonpRe = null;
		String result = "";
		Map<String, Object> map = new HashMap<String, Object>();
		try {

			if (!StringUtils.isEmpty(methods)) {
				String[] methodGs = methods.split(",");
				for (String method : methodGs) {
					if (time.equals(method)) {
						Long time = service.getDateTime();
						map.put(method, time);
					} else if (aqyxljsj.equals(method)) {
						int day = service.getSaleOperDay();
						map.put(method, day);
					} else if (wyklj.equals(method)) {
						int noWaring = service.getNoWarningDay();
						map.put(method, noWaring);
					} else if (fwzs.equals(method)) {
						int fwCount = service.getServiceCount();
						map.put(method, fwCount);
					} else if (cwl.equals(method)) {
						double error = service.getErrorRate();
						map.put(method, error);
					} else if (jryj.equals(method)) {
						int TodayWraingCount = service.getTodayWraingCount();
						map.put(method, TodayWraingCount);
					} else if (yjdt.equals(method)) {
						RecentWarnBean Recentwarning = service.getRecentwarning(day);
						map.put(method, Recentwarning);
					} else if (yjlx.equals(method)) {
						RecentWarnBean typewarning = service.getTypewarning(day);
						map.put(method, typewarning);
					} else if (whjqqsc.equals(method)) {
						List<Map<String, Object>> minenvironmentTop = service.getMinenvironmentTop(daysbefore);
						logger.info("minenvironmentTop  ====" + minenvironmentTop);
						if (minenvironmentTop != null && minenvironmentTop.size() > 0) {
							logger.info("minenvironmentTop size ====" + minenvironmentTop.size());
							for (Map<String, Object> minmap : minenvironmentTop) {
								String envir_ID = String.valueOf(minmap.get(ConstantsCode.name));
								String envir_name = onService.queryEnvirName(envir_ID);
								if (!StringUtils.isEmpty(envir_name)) {
									minmap.put(ConstantsCode.name, envir_name);
								}
							}
						}
						map.put(method, minenvironmentTop);
					} else if (jkqqsc.equals(method)) {
						List<Map<String, Object>> interFaceTop = service.getInterFaceTop(daysbefore);
						List<Map<String, Object>> interFaceTop5 = new ArrayList <Map<String, Object>>();
						if (interFaceTop != null && interFaceTop.size() > 0) {
							for (Map<String, Object> iterTop : interFaceTop) {
								String interFace_id = String.valueOf(iterTop.get(ConstantsCode.name));
								String interFace_name = onService.queryInterFaceName(interFace_id);
								if (!StringUtils.isEmpty(interFace_name)) {
									iterTop.put(ConstantsCode.name, interFace_name);
									interFaceTop5.add(iterTop);
								}
							}
						}
						if(interFaceTop5.size()>5){
							interFaceTop5 = interFaceTop5.subList(0, 5);
						}
						map.put(method, interFaceTop5);
					} else if (whjqqlx.equals(method)) {
						RecentWarnBean minenvironmentTopRate = service.getMinenvironmentTopRate(day);
						if (minenvironmentTopRate != null) {
							List<Map<String, Object>> data = minenvironmentTopRate.getData();
							for (Map<String, Object> minmap : data) {
								String envir_ID = String.valueOf(minmap.get(ConstantsCode.name));
								String envir_name = onService.queryEnvirName(envir_ID);
								if (!StringUtils.isEmpty(envir_name)) {
									minmap.put(ConstantsCode.name, envir_name);
								}
							}
						}
						map.put(method, minenvironmentTopRate);
					} else if (jkqqlx.equals(method)) {
						RecentWarnBean interFaceTopRate = service.getInterFaceTopRate(day);
						if (interFaceTopRate != null) {
							List<Map<String, Object>> data = interFaceTopRate.getData();
							List<Map<String, Object>> dataTop5 = new ArrayList <Map<String, Object>>();
							for (Map<String, Object> iterTop : data) {
								String interFace_id = String.valueOf(iterTop.get(ConstantsCode.name));
								String interFace_name = onService.queryInterFaceName(interFace_id);
								if (!StringUtils.isEmpty(interFace_name)) {
									iterTop.put(ConstantsCode.name, interFace_name);
									dataTop5.add(iterTop);
								}
							}
							if(dataTop5.size()>5){
								dataTop5 = dataTop5.subList(0, 5);
							}
							interFaceTopRate.setData(dataTop5);
						}
						map.put(method, interFaceTopRate);
					} else if (zjzyxh.equals(method)) {
						List<RecentHostBean> hostResources = service.getHostResourcesConsume(mins);
						map.put(method, hostResources);
					} else if (cp.equals(method)) {
						List<RecentHostBean> hostdiskused = service.getHostDiskUsed(mins);
						map.put(method, hostdiskused);
					} else if (wlll.equals(method)) {
						List<RecentHostBean> netflow = service.getNetFlow(mins);
						map.put(method, netflow);
					} else if (nc.equals(method)) {
						List<RecentHostBean> hostcpuused = service.getHostCpuUsedRate(mins);
						map.put(method, hostcpuused);
					}

				}

			}
			bean.setError(0);
			bean.setMessage("成功");
			bean.setResult(map);
			result = JSONObject.toJSONString(bean);
			jsonpRe = new JSONPObject(callback + "&&" + callback, JSONObject.parseObject(result));
			// result = callback + "&&" + callback + "(" + result + ")";
			// System.out.println(result);
		} catch (Exception e) {
			bean.setError(1);
			bean.setMessage("系统异常");
			result = JSONObject.toJSONString(bean);
			jsonpRe = new JSONPObject(callback + "&&" + callback, JSONObject.parseObject(result));
			e.printStackTrace();
			return jsonpRe;
		}
		return jsonpRe;
	}
}
