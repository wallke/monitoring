package com.xwtech.omweb.controller.monitoring;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.xwtech.es.model.HostBean;
import com.xwtech.es.model.HostMonitorBean;
import com.xwtech.es.model.OSBean;
import com.xwtech.es.service.SystemLogInfoService;
import com.xwtech.framework.web.result.JSONResult;
import com.xwtech.omweb.model.AppGroup;
import com.xwtech.omweb.model.Room;
import com.xwtech.omweb.model.Server;
import com.xwtech.omweb.service.IAppGroupService;
import com.xwtech.omweb.service.IRoomService;
import com.xwtech.omweb.service.IServerService;

/**
 * Created by zl on 17/3/10.
 */
@Controller
@RequestMapping("omweb/host")
public class HostController {

	private final static Logger logger = LoggerFactory.getLogger(HostController.class);

	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	
	private static final DecimalFormat df = new DecimalFormat("0.00");

	@Autowired
	IAppGroupService appGroupService;

	@Autowired
	IRoomService roomService;

	@Resource(name = "serverService")
	private IServerService serverService;

	@Autowired
	SystemLogInfoService systemLogInfoService;

	private static List<Server> servers = null;

	/**
	 * 初始化信息--主机信息
	 *
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView modelAndView = new ModelAndView("host/index");

		List<AppGroup> appGroups = appGroupService.getAllAppGroups();

		modelAndView.addObject("appGroups", appGroups);

		return modelAndView;
	}
	
	/**
	 * 初始化机房信息初始化
	 *
	 * @param appGroupId
	 *            系统应该ID
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = { "room" })
	@ResponseBody
	public JSONResult room(String appGroupId) {

		JSONResult jsonResult = new JSONResult();
		List<Room> rooms = new ArrayList<Room>();
		try {
			rooms = roomService.getRoomsByAppGroupId(appGroupId);
		} catch (Exception e) {
			logger.debug("初始化机房信息初始化失败..." + e.getMessage());
			jsonResult.setErrorInfo("系统异常");
			e.printStackTrace();
		}

		jsonResult.setData(rooms);
		logger.info("jsonResult{}" + jsonResult);

		return jsonResult;

	}

	/**
	 * 根据机房获取主机信息
	 *
	 *            系统应该ID
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = { "service" })
	@ResponseBody
	public JSONResult service(String roomId) {

		JSONResult jsonResult = new JSONResult();
		List<Server> hosts = new ArrayList<Server>();
		try {
			hosts = serverService.queryServerListByRoomId(roomId, null);
		} catch (Exception e) {
			logger.debug("初始化机房信息初始化失败..." + e.getMessage());
			jsonResult.setErrorInfo("系统异常");
			e.printStackTrace();
		}

		jsonResult.setData(hosts);
		logger.info("jsonResult{}" + jsonResult);

		return jsonResult;

	}

	/**
	 * 获取各机房主机信息
	 *
	 * @param appGroupId
	 * @param roomId
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = { "infos" })
	@ResponseBody
	public JSONResult getServiceInfo(String appGroupId, String roomId, String roomName, String hostIp) {
		JSONResult jsonResult = new JSONResult();
		try {
			List<Map> list = new ArrayList<Map>();
			Map map = new HashMap();
			if (!StringUtils.isEmpty(hostIp)) {
				map = this.getinfosByIp(hostIp);
				map.put("roomName", roomName);
				list.add(map);
			} else if (StringUtils.isEmpty(hostIp) && !StringUtils.isEmpty(roomId)) {
				// List<Room> rooms =
				// roomService.getRoomsByAppGroupId(appGroupId);
				List<Server> server = serverService.queryServerListByRoomId(roomId, null);
				server.forEach(n -> {
					Map mapInfo = getinfosByIp(n.getHostNum());
					mapInfo.put("roomName", roomName);
					list.add(mapInfo);

				});
			} else if (StringUtils.isEmpty(hostIp) && StringUtils.isEmpty(roomId) && !StringUtils.isEmpty(appGroupId)) {
				List<Room> rooms = roomService.getRoomsByAppGroupId(appGroupId);
				rooms.forEach(m -> {
					List<Server> server = serverService.queryServerListByRoomId(m.getId(), null);
					server.forEach(n -> {
						Map mapInfo = getinfosByIp(n.getHostNum());
						mapInfo.put("roomName", m.getName());
						list.add(mapInfo);
					});
				});
			}

			jsonResult.setData(list);

		} catch (Exception e) {
			jsonResult.setFailInfo("系统异常");
			e.printStackTrace();
		}

		logger.info("jsonResult{}" + jsonResult);

		return jsonResult;

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map getinfosByIp(String hostIp) {
		Map map = new HashMap();
		map.put("memory", 0);
		map.put("disk", 0);
		map.put("inCom", 0);
		map.put("outGo", 0);
		map.put("isW", 0);
		map.put("isX", 2);
		map.put("ip", hostIp);
		map.put("cpu", 0);
		map.put("time", sdf.format(new Date()));
		// 主机运行信息 获取内存 IO 磁盘
		List<HostBean> listHost = new ArrayList<HostBean>();
		// 主机信息，获取主机系统
		List<OSBean> listOs = new ArrayList<OSBean>();
		List<Server> serList = serverService.queryServerListByParam(hostIp);

		if (!listHost.isEmpty()) {
			Server ser = serList.get(0);
			// 是否虚拟机 1-实体主机;2-虚拟主机;）
			map.put("isX", ser.getType());
		}
		HostBean bean = new HostBean();
		OSBean osBean = new OSBean();

		osBean.setHost_ip(hostIp);
		osBean.setBeginTime(new Date(System.currentTimeMillis() -  172800 * 1000L));
		osBean.setEndTime(new Date());
		bean.setHost_ip(hostIp);
		bean.setBeginTime(new Date(System.currentTimeMillis() -172800 * 1000L));
		bean.setEndTime(new Date());
		try {
			listHost = systemLogInfoService.getHostInfos(bean);

			listOs = systemLogInfoService.getOsInfos(osBean);
			if (!listHost.isEmpty()) {
				bean = listHost.get(0);

				Double userMemory =bean.getTotal_memory() - bean.getAvailable_memory();

				if (bean.getTotal_memory() > 0) {
					map.put("memory", df.format(userMemory * 100 / bean.getTotal_memory()));
				}
				map.put("cpu", Float.parseFloat(bean.getProcessor_load_5()) * 100);
				if (bean.getTotall_disk() > 0) {
					map.put("disk",df.format(bean.getUsed_disk() * 100 / bean.getTotall_disk()));
				}
				double inComing = Double.valueOf(df.format(bean.getIncoming() / 1024));
				double outGoing = Double.valueOf(df.format(bean.getOutgoing() / 1024));
				map.put("inCom", inComing);
				map.put("outGo", outGoing);

				map.put("time", bean.getTime());

			}
			if (!listOs.isEmpty()) {
				osBean = listOs.get(0);
				if (osBean.getSys_info().toUpperCase().contains("LINUX")) {
					// 展示 linux windows
					map.put("isW", 0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}

	@RequestMapping(value = "/indexMonitor", method = RequestMethod.GET)
	public ModelAndView indexMonitor() {
		ModelAndView modelAndView = new ModelAndView("host/indexMonitor");

		List<AppGroup> appGroups = appGroupService.getAllAppGroups();

		modelAndView.addObject("appGroups", appGroups);

		return modelAndView;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = { "serviceInfos" })
	@ResponseBody
	public JSONResult serviceInfos(String appGroupId, String roomId, String roomName, String hostIp) {
		JSONResult jsonResult = new JSONResult();

		servers = new ArrayList<Server>();

		if (!StringUtils.isEmpty(hostIp)) {
			servers = serverService.queryServerListByParam(hostIp);
		} else if (StringUtils.isEmpty(hostIp) && !StringUtils.isEmpty(roomId)) {
			servers = serverService.queryServerListByRoomId(roomId, null);
		} else if (StringUtils.isEmpty(hostIp) && StringUtils.isEmpty(roomId) && !StringUtils.isEmpty(appGroupId)) {
			List<Room> rooms = roomService.getRoomsByAppGroupId(appGroupId);
			rooms.forEach(m -> {
				List<Server> server = serverService.queryServerListByRoomId(m.getId(), null);
				servers.removeAll(server);
				servers.addAll(server);
			});

		}
		jsonResult.setData(servers);
		return jsonResult;
	}

	@SuppressWarnings({ "rawtypes", "unchecked"})
	@RequestMapping(value = { "moninfo" })
	@ResponseBody
	public JSONResult moninfo(String serverId ,String hostIp, String dateType, String date) {
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

		List<HostMonitorBean> list = systemLogInfoService.getHostMonitorInfo(dateRange, hostIp);

		jsonResult.setData(list);
		return jsonResult;
	}


	@RequestMapping(value = "/indexMarket", method = RequestMethod.GET)
	public ModelAndView indexMarket() {
		ModelAndView modelAndView = new ModelAndView("host/indexMarket");

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
	@RequestMapping(value = { "serviceById" })
	@ResponseBody
	public JSONResult serviceByGroupId(String appGroupId) {

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


	@SuppressWarnings({ "rawtypes", "unchecked"})
	@RequestMapping(value = { "marketinfo" })
	@ResponseBody
	public JSONResult marketinfo(String hostIp) {
		JSONResult jsonResult = new JSONResult();

		if (StringUtils.isEmpty(hostIp)) {
			return jsonResult;
		}
		Date endDate = new Date();

		Date startDate = new Date(endDate.getTime()-5*60*1000L);

		DateRange dateRange = new DateRange(startDate,endDate);

		List<HostMonitorBean> list = systemLogInfoService.getHostMonitorInfo(dateRange, hostIp);

		jsonResult.setData(list);
		return jsonResult;
	}



	/**
	 * 初始化信息--主机信息
	 *
	 * @return
	 */
	@RequestMapping(value = "/indexOSMonitor", method = RequestMethod.GET)
	public ModelAndView indexOSMonitor() {
		ModelAndView modelAndView = new ModelAndView("host/indexOSMonitor");

		List<AppGroup> appGroups = appGroupService.getAllAppGroups();

		modelAndView.addObject("appGroups", appGroups);

		return modelAndView;
	}



	/**
	 * 获取主机系统进程等信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked"})
	@RequestMapping(value = { "osMoninfo" })
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

		List<OSBean> list = systemLogInfoService.getOsMonitorInfo(dateRange, hostIp);

		jsonResult.setData(list);
		return jsonResult;
	}


}
