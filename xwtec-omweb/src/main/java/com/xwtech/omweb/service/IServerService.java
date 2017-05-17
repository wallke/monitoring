package com.xwtech.omweb.service;

import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.model.Server;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by zhongxueye on 17-2-10.
 */
public interface IServerService {

    /**
     * 根据主机编码和主机名称查询主机列表信息
     * @param hostNum 主机编码
     * @param hostName 主机名称
     * @return List<Server> 主机列表
     */
     List<Server> queryServerListInfo(@Param("hostNum")String hostNum, @Param("hostName") String hostName,@Param("type")String type, @Param("roomName") String roomName,PageInfo pageInfo);

    /**
     *查询主机列表
     * @param hostIp
     * @return
     */
    List<Server> queryServerListByParam(@Param("hostIp")String hostIp);
    /**
     * 根据应用编码反查主机列表
     * @param appId
     * @return
     */
    List<Server> queryServerListByAppId(@Param("appId")String appId);
    /**
     * 根据机房的ID查询所有主机信息
     * @param roomId
     * @return
     */
     List<Server> queryServerListByRoomId(@Param("roomId")String roomId,PageInfo pageInfo);
    /**
     * 查询所有主机列表
     * @return
     */
    List<Server> queryServerList();
    /**
     * 新增主机
     * @param server
     * @return
     */
     boolean insertServer(Server server);

    /**
     * 修改主机信息
     * @param server
     * @return
     */
     boolean updateServer(Server server);

    /**
     * 根据主机ID查询主机详情
     * @param serverId
     * @return
     */
    Server queryServerDetail(@Param("serverId")String serverId);

    /**
     * 根据主机ID删除主机信息(逻辑删除，改变状态)
     * @param serverId
     * @return
     */
    int deleteServerById(@Param("serverId")String serverId);

    /**
     * 查询主机名称是否存在
     * @param serverName
     * @return
     */
    int queryServerName(String serverName,String serverId);

    /**
     * 查询主机编码是否存在
     * @param serverNum
     * @return
     */
    int queryServerNum(String serverNum,String serverId);

}
