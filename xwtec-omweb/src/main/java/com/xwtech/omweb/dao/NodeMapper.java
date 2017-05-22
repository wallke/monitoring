package com.xwtech.omweb.dao;

import com.xwtech.omweb.model.Node;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by wangyu on 2017/2/15.
 */
@Mapper
public interface NodeMapper {
    List<Node> getNodes(Node node);
    List<Node> getAllNodes();
    List<Node> getNodesByAppId(String appId);

    List<Node> queryNodeList(@Param("appId")String appId,@Param("serverId")String serverId);
    List<Node> queryNodeListByServerId(@Param("serverId")String serverId);
    Node getNodeById(String id);
    Node getNodeByNum(String num);
    Node getNodeByName(String name);
    Node getNodeByNumExceptSelf(Node node);
    Node getNodeByNameExceptSelf(Node node);

    int createNode(Node node);
    int updateNode(Node node);
    int deleteNode(String id);
}
