package com.xwtech.omweb.service;

import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.model.Node;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by wangyu on 2017/2/15.
 */
public interface INodeService {

    List<Node> getNodes(Node node, PageInfo pageInfo);
    List<Node> getAllNodes();
    List<Node> getNodesByAppId(String appId);
    List<Node> queryNodeList(@Param("appId")String appId, @Param("serverId")String serverId);
    List<Node> queryNodeListByServerId(@Param("serverId")String serverId);
    Node getNodeById(String id);
    Node getNodeByNum(String num);
    Node getNodeByName(String name);
    Node getNodeByNumExceptSelf(Node node);
    Node getNodeByNameExceptSelf(Node node);

    boolean createNode(Node node);
    boolean updateNode(Node node);
    boolean deleteNode(String id);
}
