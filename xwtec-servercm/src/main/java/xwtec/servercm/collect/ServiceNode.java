/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.collect;

import xwtec.servercm.core.IDCreater;
import xwtec.servercm.init.ConfigeInfo;
import xwtec.servercm.init.Configer;

/**
 * 服务节点需要的配置信息
 *
 * @author 张岩松
 */
public class ServiceNode {

    private String envir_ID;
    private String node_code;
    private String service_ID;

    public ServiceNode(String service_ID) {
        this.service_ID = service_ID;
    }

    public String getEnvir_ID() {
        return envir_ID;
    }

    public void setEnvir_ID(String envir_ID) {
        this.envir_ID = envir_ID;
    }

    public String getNode_code() {
        return node_code;
    }

    public void setNode_code(String node_code) {
        this.node_code = node_code;
    }

    public String getService_ID() {
        return service_ID;
    }

    public void setService_ID(String service_ID) {
        this.service_ID = service_ID;
    }

    /**
     * 根据节点名称，找到其环境配置
     *
     * @param node_code
     * @return
     */
    public static ServiceNode instance(String node_code) {
        ServiceNode serviceNode = new ServiceNode(IDCreater.createNodeID());
        for (ConfigeInfo.Envir envir : Configer.getEnvirs()) {
            if (envir.getNode_codes().contains(node_code)) {
                serviceNode.setEnvir_ID(envir.getEnvir_ID());
                serviceNode.setNode_code(node_code);
                return serviceNode;
            }
        }
        serviceNode.setEnvir_ID("default");
        serviceNode.setNode_code(node_code);
        return serviceNode;
    }
}
