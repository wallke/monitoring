package com.xwtech.omweb.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.dao.ServerMapper;
import com.xwtech.omweb.model.Server;
import com.xwtech.omweb.service.IServerService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
	 * @param hostNum
	 *            主机编码
	 * @param hostName
	 *            主机名称
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
	 * @param hostIp
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
		if (server.getServerInfo() != null) {
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
		count = serverMapper.updateServer(server);
		count = serverMapper.updateServerInfo(server);

		if (count > 0) {
			flag = true;
		}

		return flag;
	}

	@Override
	public Server queryServerDetail(@Param("serverId") String serverId) {
		return serverMapper.queryServerDetail(serverId);
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
}
