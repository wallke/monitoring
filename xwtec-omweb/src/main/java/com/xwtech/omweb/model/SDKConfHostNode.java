package com.xwtech.omweb.model;

import org.omg.CORBA.PRIVATE_MEMBER;

/**
 * Created by Administrator on 2017/2/20 0020.
 * 采集主机节点控制
 */
public class SDKConfHostNode {

    //节点ID
    private  String nodeId;
    //主机控制ID
    private  String hostId;
    //节点名称
    private  String node_name;
    //节点代码
    private  String nodeCode;
    //采集开关
    private  String collectNode;

    private boolean collect_node;
    private String node_code;


    public boolean isCollect_node() {
        return collect_node;
    }

    public void setCollect_node(boolean collect_node) {
        this.collect_node = collect_node;
    }

    public String getNode_code() {
        return node_code;
    }

    public void setNode_code(String node_code) {
        this.node_code = node_code;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getNode_name() {
        return node_name;
    }

    public void setNode_name(String node_name) {
        this.node_name = node_name;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getCollectNode() {
        return collectNode;
    }

    public void setCollectNode(String collectNode) {
        this.collectNode = collectNode;
    }
}
