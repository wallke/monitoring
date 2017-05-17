package com.xwtech.omweb.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.dao.SDKMapper;
import com.xwtech.omweb.enums.SdkFlag;
import com.xwtech.omweb.model.*;
import com.xwtech.omweb.service.SDKService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/20 0020.
 */
@Service("sdkService")
public class SDKServiceImpl implements SDKService {

    @Autowired
    private SDKMapper sdkMapper;
    /**
     * 查询SDK配置信息列表
     *
     * @param pageInfo
     * @return
     */
    @Override
    public List<SDK> querySDKList(PageInfo pageInfo) {
        if (pageInfo != null)
        {
            PageHelper.startPage(pageInfo.getPageNum(),pageInfo.getPageSize());
        }
        return sdkMapper.querySDKList();
    }

    /**
     * 新增SDK信息
     *
     * @param sdk
     * @return
     */
    @Override
    public int addSDK(SDK sdk) {
        return sdkMapper.addSDK(sdk);
    }

    /**
     * 修改SDK信息
     *
     * @param sdk
     * @return
     */
    @Override
    public int updateSDK(SDK sdk) {
        return sdkMapper.updateSDK(sdk);
    }

    /**
     * 查询SDK详情
     *
     * @param confId
     * @return
     */
    @Override
    public SDK querySdkDetail(@Param("confId") String confId) {
        return sdkMapper.querySdkDetail(confId);
    }

    /**
     * 根据SDKId查询性能配置列表
     *
     * @param confId
     * @return
     */
    @Override
    public List<SDKConfHeart> querySDKConfHeartByConfId(@Param("confId") String confId) {
        return sdkMapper.querySDKConfHeartByConfId(confId);
    }

    /**
     * 修改sdk状态
     *
     * @param confId
     * @param flag
     * @return
     */
    @Override
    public int updateSdkFlagEnable(@Param("confId") String confId, @Param("flag") String flag) {
        return sdkMapper.updateSdkFlagEnable(confId,flag);
    }

    /**
     * 排除自身ID关闭废弃相同的服务端名称
     *
     * @param confId
     * @param appServerName
     * @param flag
     * @return
     */
    @Transactional
    @Override
    public int updateSdkFlag(@Param("confId") String confId, @Param("appServerName") String appServerName, @Param("flag") String flag) {
        int count = 0;
        if (flag.equals(SdkFlag.Zero.getFlag()))
        {
            //关闭除自身外的其他SDK信息
            count = sdkMapper.updateSdkFlag(confId,appServerName,SdkFlag.Zero.getFlag());
            //启用sdk
           count = sdkMapper.updateSdkFlagEnable(confId,SdkFlag.ONE.getFlag());

        }else
        {
            //关闭sdk
            count = sdkMapper.updateSdkFlagEnable(confId,SdkFlag.Zero.getFlag());
        }
        return count;
    }

    /**
     * 新增SDK心跳配置
     *
     * @param sdkConfHeart
     * @return
     */
    @Override
    public int addSdkHeart(SDKConfHeart sdkConfHeart) {
        return sdkMapper.addSdkHeart(sdkConfHeart);
    }

    /**
     * 修改SDK心跳配置
     *
     * @param sdkConfHeart
     * @return
     */
    @Override
    public int updateSdkHrart(SDKConfHeart sdkConfHeart) {
        return sdkMapper.updateSdkHrart(sdkConfHeart);
    }

    /**
     * 根据心跳ID查询心跳详细信息
     *
     * @param heartId
     * @return
     */
    @Override
    public SDKConfHeart querySDKConfHeartDetail(@Param("heartId") String heartId) {
        return sdkMapper.querySDKConfHeartDetail(heartId);
    }

    /**
     * 根据心跳ID删除配置信息
     *
     * @param heartId
     * @return
     */
    @Override
    public int deleteSdkConfHeartById(@Param("heartId") String heartId) {
        return sdkMapper.deleteSdkConfHeartById(heartId);
    }

    /**
     * 根据SDKId查询主机配置列表
     *
     * @param confId
     * @return
     */
    @Override
    public List<SDKConfHost> querySDKConfHostListByConfId(@Param("confId") String confId) {
        return sdkMapper.querySDKConfHostListByConfId(confId);
    }

    /**
     * 新增主机配置列表
     *
     * @param sdkConfHost
     * @return
     */
    @Override
    public int addSDKConfHost(SDKConfHost sdkConfHost) {
        return sdkMapper.addSDKConfHost(sdkConfHost);
    }

    /**
     * 修改主机配置列表
     *
     * @param sdkConfHost
     * @return
     */
    @Override
    public int updateSDKConfHost(SDKConfHost sdkConfHost) {
        return sdkMapper.updateSDKConfHost(sdkConfHost);
    }

    /**
     * 删除主机配置
     *
     * @param hostId
     * @return
     */
    @Override
    public int deleteSDKConfHostByHostId(@Param("hostId") String hostId) {
        return sdkMapper.deleteSDKConfHostByHostId(hostId);
    }

    /**
     * 根据ID查询主机详情
     *
     * @param hostId
     * @return
     */
    @Override
    public SDKConfHost querySDKConfHostDetail(@Param("hostId") String hostId) {
        return sdkMapper.querySDKConfHostDetail(hostId);
    }

    /**
     * 根据ID查询微环境列表
     *
     * @param confId
     * @return
     */
    @Override
    public List<SDKConfEnv> querySDKConfEnvList(@Param("confId") String confId) {
        return sdkMapper.querySDKConfEnvList(confId);
    }

    /**
     * 新增微环境
     *
     * @param sdkConfEnv
     * @return
     */
    @Override
    public int addSDKConfEnv(SDKConfEnv sdkConfEnv) {
        return sdkMapper.addSDKConfEnv(sdkConfEnv);
    }

    /**
     * 修改微环境
     *
     * @param sdkConfEnv
     * @return
     */
    @Override
    public int updateSDKConfEnv(SDKConfEnv sdkConfEnv) {
        return sdkMapper.updateSDKConfEnv(sdkConfEnv);
    }

    /**
     * 根据微环境ID删除微环境
     *
     * @param envId
     * @return
     */
    @Override
    public int deleteSDKConfEnvByEnvId(@Param("envId") String envId) {
        return sdkMapper.deleteSDKConfEnvByEnvId(envId);
    }

    /**
     * 根据微环境ID查看微环境详情
     *
     * @param envId
     * @return
     */
    @Override
    public SDKConfEnv querySDKConfEnvDetailByEnvId(@Param("envId") String envId) {
        return sdkMapper.querySDKConfEnvDetailByEnvId(envId);
    }

    /**
     * 查询主机下面所有的节点
     *
     * @param hostId
     * @return
     */
    @Override
    public List<SDKConfHostNode> querySDKConfHostNode(@Param("hostId") String hostId) {
        return sdkMapper.querySDKConfHostNode(hostId);
    }

    /**
     * 新增节点黑名单
     *
     * @param sdkConfHostNode
     * @return
     */
    @Override
    public int addSDKConfHostNode(SDKConfHostNode sdkConfHostNode) {
        return sdkMapper.addSDKConfHostNode(sdkConfHostNode);
    }

    /**
     * 修改节点黑名单
     *
     * @param sdkConfHostNode
     * @return
     */
    @Override
    public int updateSDKConfHostNode(SDKConfHostNode sdkConfHostNode) {
        return sdkMapper.updateSDKConfHostNode(sdkConfHostNode);
    }

    /**
     * 删除节点黑名单
     *
     * @param nodeId
     * @return
     */
    @Override
    public int deleteSDKConfHostNodeByNodeId(@Param("nodeId") String nodeId) {
        return sdkMapper.deleteSDKConfHostNodeByNodeId(nodeId);
    }

    /**
     * 根据ID查询节点详情
     *
     * @param nodeId
     * @return
     */
    @Override
    public SDKConfHostNode querySDKConfHostNodeDetail(@Param("nodeId") String nodeId) {
        return sdkMapper.querySDKConfHostNodeDetail(nodeId);
    }

    @Override
    public SdkJson querySdkJson(@Param("confId") String confId) {
        SdkJson sdkJson = sdkMapper.querySdkJson(confId);
        if(sdkJson !=null)
        {
            List<SDKConfHost> hosts = sdkJson.getHosts();
            if(hosts !=null && hosts.size()>0)
            {
                List<SDKConfHostNode> nodes = new ArrayList<SDKConfHostNode>();
                for(SDKConfHost host :hosts)
                {
                    nodes = sdkMapper.querySDKNodeJson(host.getHost_id());
                    host.setNodes(nodes);
                }
            }
        }
        return sdkJson;
    }

    /**
     * 根据应用ID查询微环境列表
     *
     * @param appId
     * @return
     */
    @Override
    public List<SDKConfEnv> querySDKConfEnvListByAppId(@Param("appId") String appId) {
        return sdkMapper.querySDKConfEnvListByAppId(appId);
    }
}
