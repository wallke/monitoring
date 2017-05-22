package com.xwtech.omweb.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwtech.omweb.dao.NodeMapper;
import com.xwtech.omweb.model.Node;
import com.xwtech.omweb.service.INodeService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wangyu on 2017/2/15.
 */
@Service
public class NodeServiceImpl implements INodeService {

    @Autowired
    private NodeMapper nodeMapper;

    @Override
    public List<Node> getNodes(Node node, PageInfo pageInfo) {
        if (pageInfo != null) {
            PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        }
        return nodeMapper.getNodes(node);
    }

    @Override
    public List<Node> getAllNodes() {
        return nodeMapper.getAllNodes();
    }

    @Override
    public List<Node> getNodesByAppId(String appId) {
        return nodeMapper.getNodesByAppId(appId);
    }

    @Override
    public List<Node> queryNodeList(@Param("appId") String appId, @Param("serverId") String serverId) {
        return nodeMapper.queryNodeList(appId,serverId);
    }

    @Override
    public List<Node> queryNodeListByServerId(String serverId) {
        return nodeMapper.queryNodeListByServerId(serverId);
    }

    @Override
    public Node getNodeById(String id) {
        return nodeMapper.getNodeById(id);
    }

    @Override
    public Node getNodeByNum(String num) {
        return nodeMapper.getNodeByNum(num);
    }

    @Override
    public Node getNodeByName(String name) {
        return nodeMapper.getNodeByName(name);
    }

    @Override
    public Node getNodeByNumExceptSelf(Node node) {
        return nodeMapper.getNodeByNumExceptSelf(node);
    }

    @Override
    public Node getNodeByNameExceptSelf(Node node) {
        return nodeMapper.getNodeByNameExceptSelf(node);
    }

    @Override
    public boolean createNode(Node node) {
        return nodeMapper.createNode(node) > 0;
    }

    @Override
    public boolean updateNode(Node node) {
        return nodeMapper.updateNode(node) > 0;
    }

    @Override
    public boolean deleteNode(String id) {
        return nodeMapper.deleteNode(id) > 0;
    }
}
