package com.xwtech.omweb.service;

import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.model.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/2/20 0020.
 */
public interface SDKService {

    /**
     * 查询SDK配置信息列表
     * @return
     */
    List<SDK> querySDKList(PageInfo pageInfo);

    /**
     * 新增SDK信息
     * @param sdk
     * @return
     */
    int addSDK(SDK sdk);

    /**
     * 修改SDK信息
     * @param sdk
     * @return
     */
    int updateSDK(SDK sdk);

    /**
     * 查询SDK详情
     * @param confId
     * @return
     */
    SDK querySdkDetail(@Param("confId")String confId);

    /**
     * 根据SDKId查询性能配置列表
     * @param confId
     * @return
     */
    List<SDKConfHeart> querySDKConfHeartByConfId(@Param("confId")String confId);

    /**
     * 修改sdk状态
     * @param confId
     * @param flag
     * @return
     */
    int updateSdkFlagEnable(@Param("confId")String confId,@Param("flag")String flag);

    /**
     * 排除自身ID关闭废弃相同的服务端名称
     * @param confId
     * @param appServerName
     * @return
     */
    int updateSdkFlag(@Param("confId")String confId,@Param("appServerName")String appServerName,@Param("flag")String flag);


    /**
     * 新增SDK心跳配置
     * @param sdkConfHeart
     * @return
     */
    int addSdkHeart(SDKConfHeart sdkConfHeart);

    /**
     * 修改SDK心跳配置
     * @param sdkConfHeart
     * @return
     */
    int updateSdkHrart(SDKConfHeart sdkConfHeart);

    /**
     * 根据心跳ID查询心跳详细信息
     * @param heartId
     * @return
     */
    SDKConfHeart querySDKConfHeartDetail(@Param("heartId")String heartId);

    /**
     * 根据心跳ID删除配置信息
     * @param heartId
     * @return
     */
    int deleteSdkConfHeartById(@Param("heartId")String heartId);

    /**
     * 根据SDKId查询主机配置列表
     * @param confId
     * @return
     */
    List<SDKConfHost> querySDKConfHostListByConfId(@Param("confId")String confId);

    /**
     * 新增主机配置列表
     * @param sdkConfHost
     * @return
     */
    int addSDKConfHost(SDKConfHost sdkConfHost);

    /**
     * 修改主机配置列表
     * @param sdkConfHost
     * @return
     */
    int updateSDKConfHost(SDKConfHost sdkConfHost);

    /**
     * 删除主机配置
     * @return
     */
    int deleteSDKConfHostByHostId(@Param("hostId")String hostId);

    /**
     * 根据ID查询主机详情
     * @return
     */
    SDKConfHost querySDKConfHostDetail(@Param("hostId")String hostId);

    /**
     * 根据ID查询微环境列表
     * @param confId
     * @return
     */
    List<SDKConfEnv> querySDKConfEnvList(@Param("confId")String confId);

    /**
     * 新增微环境
     * @param sdkConfEnv
     * @return
     */
    int addSDKConfEnv(SDKConfEnv sdkConfEnv);

    /**
     * 修改微环境
     * @param sdkConfEnv
     * @return
     */
    int updateSDKConfEnv(SDKConfEnv sdkConfEnv);

    /**
     * 根据微环境ID删除微环境
     * @param envId
     * @return
     */
    int deleteSDKConfEnvByEnvId(@Param("envId")String envId);

    /**
     * 根据微环境ID查看微环境详情
     * @param envId
     * @return
     */
    SDKConfEnv querySDKConfEnvDetailByEnvId(@Param("envId")String envId);

    /**
     * 查询主机下面所有的节点
     * @param hostId
     * @return
     */
    List<SDKConfHostNode> querySDKConfHostNode(@Param("hostId")String hostId);

    /**
     * 新增节点黑名单
     * @param sdkConfHostNode
     * @return
     */
    int addSDKConfHostNode(SDKConfHostNode sdkConfHostNode);

    /**
     * 修改节点黑名单
     * @param sdkConfHostNode
     * @return
     */
    int updateSDKConfHostNode(SDKConfHostNode sdkConfHostNode);

    /**
     * 删除节点黑名单
     * @param nodeId
     * @return
     */
    int deleteSDKConfHostNodeByNodeId(@Param("nodeId")String nodeId);

    /**
     * 根据ID查询节点详情
     * @param nodeId
     * @return
     */
    SDKConfHostNode querySDKConfHostNodeDetail(@Param("nodeId")String nodeId);

    SdkJson querySdkJson(@Param("confId")String confId);

    /**
     * 根据应用ID查询微环境列表
     * @param appId
     * @return
     */
    List<SDKConfEnv> querySDKConfEnvListByAppId(@Param("appId")String appId);


}
