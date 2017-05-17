/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.collect;

/**
 * 节点位置
 *
 * @author 张岩松
 */
public class NodeID {

    //节点名称
    private String node_code;
    //节点ID
    private String node_ID;
    //节点模式
    private String mode;

    public NodeID() {
    }

    public NodeID(String node_code, String node_ID, String mode) {
        this.node_code = node_code;
        this.node_ID = node_ID;
        this.mode = mode;
    }

    public String getNode_code() {
        return node_code;
    }

    public String getNode_ID() {
        return node_ID;
    }

    public String getMode() {
        return mode;
    }

}
