package com.xwtech.omweb.controller.monitoring;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xwtech.es.DateFormatUtil;
import com.xwtech.es.model.DateEnum;
import com.xwtech.es.model.DateRange;
import com.xwtech.es.model.NginxBean;
import com.xwtech.es.model.OSBean;
import com.xwtech.es.model.RedisBean;
import com.xwtech.es.service.SystemLogInfoService;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.model.AppGroup;
import com.xwtech.omweb.model.Room;
import com.xwtech.omweb.model.Server;
import com.xwtech.omweb.service.IAppGroupService;
import com.xwtech.omweb.service.IRoomService;
import com.xwtech.omweb.service.IServerService;

/**
 * 
 * @author lin.mr 2017/03/15
 *
 */
@Controller
@RequestMapping("omweb/")
public class RedisNginxController {
	
	private final static Logger logger = LoggerFactory.getLogger(RedisNginxController.class);
	

	@Autowired
	IAppGroupService appGroupService;

	@Autowired
	IRoomService roomService;
	
	@Autowired
	SystemLogInfoService systemLogInfoService;
	
	@Resource(name = "serverService")
	private IServerService serverService;
	
	/**
	 * 初始化信息--主机信息
	 * 
	 * @param hostName
	 * @param hostIp
	 * @return
	 */
	@RequestMapping(value = "redis/index", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView modelAndView = new ModelAndView("redis/index");

		List<AppGroup> appGroups = appGroupService.getAllAppGroups();

		modelAndView.addObject("appGroups", appGroups);

		return modelAndView;
	}
	
	
	/**
	 * 获取主机信息
	 * 
	 * @param appGroupId
	 *            系统应该ID
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = { "redis/service" })
	@ResponseBody
	public JSONResult service(String appGroupId) {

		JSONResult jsonResult = new JSONResult();
		List<Room> rooms = new ArrayList<Room>();
		List<Server> hosts = new ArrayList<Server>();
		try {
			rooms = roomService.getRoomsByAppGroupId(appGroupId);
			rooms.forEach(n->{
				List<Server> host = serverService.queryServerListByRoomId(n.getId(), null);
				hosts.removeAll(host);
				hosts.addAll(host);
			});
		} catch (Exception e) {
			logger.debug("主机信息初始化失败..." + e.getMessage());
			jsonResult.setErrorInfo("系统异常");
			e.printStackTrace();
		}
		jsonResult.setData(hosts);
		logger.info("jsonResult{}" + jsonResult);

		return jsonResult;

	}
	
	
	/**
	 * 获取
	 * @param hostIp
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked"})
	@RequestMapping(value = { "redis/redisMonitor" })
	@ResponseBody
	public JSONResult getRedisMonitorInfo(String hostIp) {

		JSONResult jsonResult = new JSONResult();
		
		Date endDate = new Date();
		
		Date startDate = new Date(endDate.getTime()-5*60*1000L);
		
		RedisBean bean = new RedisBean();
		bean.setHost_ip(hostIp);
		bean.setBeginTime(startDate);
		bean.setEndTime(endDate);
		List<RedisBean> list  = new ArrayList<RedisBean>();
		try {
			list	=  systemLogInfoService.getRedisInfos(bean);
		} catch (Exception e) {
			logger.debug("redis信息获取失败..." + e.getMessage());
			jsonResult.setErrorInfo("系统异常");
			e.printStackTrace();
		}
		jsonResult.setData(list);
		
		return jsonResult;

	}
	
	@RequestMapping(value = "redis/indexMonitor", method = RequestMethod.GET)
	public ModelAndView redisIndexMonitor() {
		ModelAndView modelAndView = new ModelAndView("redis/indexMonitor");

		List<AppGroup> appGroups = appGroupService.getAllAppGroups();

		modelAndView.addObject("appGroups", appGroups);

		return modelAndView;
	}
	
	

	@SuppressWarnings({ "rawtypes", "unchecked"})
	@RequestMapping(value = { "redis/monReinfo" })
	@ResponseBody
	public JSONResult osMoninfo(String serverId ,String hostIp, String dateType, String date) {
		JSONResult jsonResult = new JSONResult();

		if (StringUtils.isEmpty(dateType)) {
			jsonResult.setFailInfo("请选择查询时间！");
			return jsonResult;
		}
		DateEnum dateEnum = DateEnum.values()[Integer.parseInt(dateType)];
		DateRange dateRange = null;
		Date start = null;
		Date end = null; 

		if ("0".equals(dateType)) {

			if (StringUtils.isEmpty(date)) {
				jsonResult.setFailInfo("请选择查询时间！");
				return jsonResult;
			}

			String[] dates = date.split(" - ", -1);
			try {
				start = DateUtils.parseDate(dates[0], DateFormatUtil.DateFormatMil);
				end = DateUtils.parseDate(dates[1], DateFormatUtil.DateFormatMil);
			} catch (ParseException e) {
				logger.error("开始时间：{} 结束时间：{}", dates[0], dates[1]);
				logger.error("转换时间异常，时间格式不正确", e);
				jsonResult.setFailInfo("请选择查询时间！");
				return jsonResult;
			}
			 dateRange = new DateRange(start, end);
		} else {
			dateRange = new DateRange(dateEnum);
		}
		
		List<RedisBean> list = systemLogInfoService.getRedisMonitorInfo(dateRange, hostIp);
		
		jsonResult.setData(list);
		return jsonResult;
	}
	
	
	/**
	 * 初始化信息--主机信息
	 * 
	 * @param hostName
	 * @param hostIp
	 * @return
	 */
	@RequestMapping(value = "nginx/index", method = RequestMethod.GET)
	public ModelAndView nginxCreate() {
		ModelAndView modelAndView = new ModelAndView("nginx/index");

		List<AppGroup> appGroups = appGroupService.getAllAppGroups();

		modelAndView.addObject("appGroups", appGroups);

		return modelAndView;
	}
	
	
	/**
	 * 获取
	 * @param hostIp
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked"})
	@RequestMapping(value = { "nginx/redisMonitor" })
	@ResponseBody
	public JSONResult getNginxMonitorInfo(String hostIp) {

		JSONResult jsonResult = new JSONResult();
		
		Date endDate = new Date();
		
		Date startDate = new Date(endDate.getTime()-5*60*1000L);
		
		NginxBean bean = new NginxBean();
		bean.setHost_ip(hostIp);
		bean.setBeginTime(startDate);
		bean.setEndTime(endDate);
		List<NginxBean> list  = new ArrayList<NginxBean>();
		try {
			list	=  systemLogInfoService.getNginxInfos(bean);
		} catch (Exception e) {
			logger.debug("Nginx信息获取失败..." + e.getMessage());
			jsonResult.setErrorInfo("系统异常");
			e.printStackTrace();
		}
		jsonResult.setData(list);
		
		return jsonResult;

	}
	
	
	@RequestMapping(value = "nginx/indexMonitor", method = RequestMethod.GET)
	public ModelAndView nginxIndexMonitor() {
		ModelAndView modelAndView = new ModelAndView("nginx/indexMonitor");

		List<AppGroup> appGroups = appGroupService.getAllAppGroups();

		modelAndView.addObject("appGroups", appGroups);

		return modelAndView;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked"})
	@RequestMapping(value = { "nginx/monNginfo" })
	@ResponseBody
	public JSONResult monNginfo(String serverId ,String hostIp, String dateType, String date) {
		JSONResult jsonResult = new JSONResult();

		if (StringUtils.isEmpty(dateType)) {
			jsonResult.setFailInfo("请选择查询时间！");
			return jsonResult;
		}
		DateEnum dateEnum = DateEnum.values()[Integer.parseInt(dateType)];
		DateRange dateRange = null;
		Date start = null;
		Date end = null; 

		if ("0".equals(dateType)) {

			if (StringUtils.isEmpty(date)) {
				jsonResult.setFailInfo("请选择查询时间！");
				return jsonResult;
			}

			String[] dates = date.split(" - ", -1);
			try {
				start = DateUtils.parseDate(dates[0], DateFormatUtil.DateFormatMil);
				end = DateUtils.parseDate(dates[1], DateFormatUtil.DateFormatMil);
			} catch (ParseException e) {
				logger.error("开始时间：{} 结束时间：{}", dates[0], dates[1]);
				logger.error("转换时间异常，时间格式不正确", e);
				jsonResult.setFailInfo("请选择查询时间！");
				return jsonResult;
			}
			 dateRange = new DateRange(start, end);
		} else {
			dateRange = new DateRange(dateEnum);
		}
		
		List<NginxBean> list = systemLogInfoService.getNginxMonitorInfo(dateRange, hostIp);
		
		jsonResult.setData(list);
		return jsonResult;
	}
	
}
