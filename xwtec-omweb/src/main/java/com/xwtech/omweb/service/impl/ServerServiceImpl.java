package com.xwtech.omweb.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.dao.ServerMapper;
import com.xwtech.omweb.model.Select2Data;
import com.xwtech.omweb.model.Select2RtnData;
import com.xwtech.omweb.model.Server;
import com.xwtech.omweb.service.IServerService;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by zhongxueye on 17-2-10.
 */
@Service("serverService")
public class ServerServiceImpl implements IServerService {

	@Autowired
	private ServerMapper serverMapper;

	/**
	 * 根据主机编码和主机名称查询主机列表信息
	 *
	 * @param hostNum  主机编码
	 * @param hostName 主机名称
	 * @return List<Server> 主机列表
	 */
	@Override
	public List<Server> queryServerListInfo(@Param("hostNum") String hostNum, @Param("hostName") String hostName,
											@Param("type") String type, @Param("roomName") String roomName, PageInfo pageInfo) {
		if (pageInfo != null) {
			PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
		}
		List<Server> servers = serverMapper.queryServerListInfo(hostNum, hostName, type, roomName);

		return servers;
	}

	/**
	 * 查询主机列表
	 *
	 * @param hostNum
	 * @return
	 */
	@Override
	public List<Server> queryServerListByParam(@Param("hostNum") String hostNum) {
		return serverMapper.queryServerListByParam(hostNum);
	}

	/**
	 * 根据应用编码反查主机列表
	 *
	 * @param appId
	 * @return
	 */
	@Override
	public List<Server> queryServerListByAppId(@Param("appId") String appId) {
		return serverMapper.queryServerListByAppId(appId);
	}

	/**
	 * 根据机房的ID查询所有主机信息
	 *
	 * @param roomId
	 * @param pageInfo
	 * @return
	 */
	@Override
	public List<Server> queryServerListByRoomId(@Param("roomId") String roomId, PageInfo pageInfo) {
		if (pageInfo != null) {
			PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
		}
		return serverMapper.queryServerListByRoomId(roomId);
	}

	@Override
	public List<Select2RtnData> queryServiceGroupType(String roomId) {
		List<Server> list= serverMapper.queryServiceGroupType(roomId);
		List<Select2RtnData> dataList=new ArrayList<Select2RtnData>();
		List<Select2Data> sList=null;
		Select2Data sData=null;
		Select2RtnData rtnData=null;
		String temp="";
		for(Server ser :list){
			sData=new Select2Data();
			rtnData=new Select2RtnData();

			sList=new ArrayList<Select2Data>();
			if (ser.getMemo().equals(temp)){
				sData.setId(ser.getHostNum());
				sData.setText(ser.getHostName());
				sList.add(sData);
				rtnData.setChildren(sList);
			}else{
				temp=ser.getMemo();
				rtnData.setText(ser.getMemo());
				sData.setId(ser.getHostNum());
				sData.setText(ser.getHostName());
				sList.add(sData);
				rtnData.setChildren(sList);
			}
			dataList.add(rtnData);

		}
		return dataList;

	}

	/**
	 * 根据机房的ID查询所有主机信息
	 *
	 * @param roomId
	 * @param pageInfo
	 * @return
	 */
	@Override
	public List<Server> queryServerListByRoomIdAndRef(@Param("roomId") String roomId, PageInfo pageInfo,String ref) {
		if (pageInfo != null) {
			PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
		}
		List<String> list=new ArrayList() ;
		if(StringUtils.isNoneEmpty(ref))
		{
			list.add(ref);
			Map<String, Object> map = new HashMap<>();
			map.put("roomId", roomId);
			map.put("ref", list);
			return serverMapper.queryServerListByRoomIdAndRef(map);
		}else{
			return	serverMapper.queryServerListByRoomId(roomId);
		}

	}
	/**
	 * 查询所有主机列表
	 *
	 * @return
	 */
	@Override
	public List<Server> queryServerList() {
		return serverMapper.queryServerList();
	}

	/**
	 * 新增主机
	 *
	 * @param server
	 * @return
	 */
	@Override
	@Transactional
	public boolean insertServer(Server server) {
		boolean flag = false;
		int count = 0;

		String replace = UUID.randomUUID().toString().replace("-", "");
		server.setServerId(replace);
		count = serverMapper.addServer(server);
		Map<String,String> map=new HashMap<>();
		if (server.getServerInfo() != null) {
			map.put(replace,server.getCategorys());
			AddCagegoryRel(map);
			count = serverMapper.addServerInfo(server);
        }
		if (count > 0) {
			flag = true;
		}
		return flag;
	}

	@Override
	public boolean updateServer(Server server) {
		boolean flag = false;
		int count = 0;
		DelCagegoryRel(server.getServerId());
		Map<String,String> map=new HashMap<>();
	    map.put(server.getServerId(),server.getCategorys());
	    AddCagegoryRel(map);

		count = serverMapper.updateServer(server);
		count = serverMapper.updateServerInfo(server);

		if (count > 0) {
			flag = true;
		}

		return flag;
	}

	@Override
	public Server queryServerDetail(@Param("serverId") String serverId) {
    	List<String> cats=GetCategoryIds(serverId);
		Server ser=serverMapper.queryServerDetail(serverId);
		String strCat=StringUtils.join(cats.toArray(),",");
		ser.setCategorys(strCat);
		return ser;
	}

	/**
	 * 根据主机ID删除主机信息(逻辑删除，改变状态)
	 *
	 * @param serverId
	 * @return
	 */
	@Override
	public int deleteServerById(@Param("serverId") String serverId) {
		return serverMapper.deleteServerById(serverId);
	}

	/**
	 * 查询主机名称是否存在
	 *
	 * @param serverName
	 * @param serverId
	 * @return
	 */
	@Override
	public int queryServerName(@Param("serverName") String serverName, @Param("serverId") String serverId) {
		return serverMapper.queryServerName(serverName, serverId);
	}

	/**
	 * 查询主机编码是否存在
	 *
	 * @param serverNum
	 * @param serverId
	 * @return
	 */
	@Override
	public int queryServerNum(@Param("serverNum") String serverNum, @Param("serverId") String serverId) {
		return serverMapper.queryServerNum(serverNum, serverId);
	}
	/**
	 * 获取所有主机分类
	 * @return
	 */
	@Override
	public List<Map<String, String>> GetAllServerCategory() {
		return serverMapper.GetAllServerCategory();
	}
	/**
	 * 获取所有主机分类关联ID
	 * @return
	 */
	@Override
	public List<String> GetCategoryIds(String F_SERVER_ID) {
		return serverMapper.GetCategoryIds(F_SERVER_ID);
	}
	/**
	 * 删除主机分类关联
	 * @return
	 */
	@Override
	public int DelCagegoryRel(String F_SERVER_ID) {
		return serverMapper.DelCagegoryRel(F_SERVER_ID);
	}
	/**
	 * 添加主机分类关联
	 * @return
	 */
	@Override
	public int AddCagegoryRel(Map<String, String> map) {
    	for (String key : map.keySet()) {
			String value=map.get(key);
           if (value.contains(","))
		   {
		   	for (int i=0;i<value.split(",").length;i++)
			{

				map.clear();
				map.put("F_SERVER_ID",key);
				map.put("F_SERVER_CATEGORY_ID",value.split(",")[i]);
				serverMapper.AddCagegoryRel(map);
			}
			   return 1;
		   }else{
			   map.put("F_SERVER_ID",key);
			   map.put("F_SERVER_CATEGORY_ID",value);
			   return serverMapper.AddCagegoryRel(map);
		   }


		}
		return 0;

	}
}



