/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.core;

import java.util.HashMap;
import java.util.Map;
import xwtec.servercm.collect.ServerInfo;
import xwtec.servercm.init.ConfigeInfo;
import xwtec.servercm.init.Configer;

/**
 * 采集控制单元
 *
 * s* @author 张岩松
 */
public class CollectCtl {

    private static final CollectCtl collectCtl = new CollectCtl();
    //所有受控的节点
    private final Map<String, Boolean> nodes = new HashMap<String, Boolean>();
    //总控
    private boolean collect_all;

    /**
     * 得到受控管理器
     *
     * @return
     */
    public static CollectCtl instance() {
        return collectCtl;
    }

    /**
     * 加载配置
     *
     * @param configeInfo
     */
    public void loadCtl(ConfigeInfo configeInfo) {
        this.collect_all = configeInfo.isCollect_all();
        nodes.clear();
        for (ConfigeInfo.AppHost appHost : Configer.getHosts()) {
            for (ConfigeInfo.Node node : appHost.getNodes()) {
                String key = createKey(appHost.getIp(), appHost.getPort(), node.getNode_code());
                this.nodes.put(key, this.collect_all && appHost.isCollect_ip() && appHost.isCollect_port() && node.isCollect_node());
            }
        }
    }

    /**
     * 判断当前节点是否需要采集
     *
     * @param envir_ID
     * @param node_code
     * @return
     */
    public static boolean isCollect(String envir_ID, String node_code) {
        return collectCtl.isCollectNode(node_code) && collectCtl.isCollectEnvir(envir_ID) && collectCtl.isSampling();
    }

    /**
     * 计算一定范围的随机数
     *
     * @param minNumber
     * @param maxNumber
     * @return
     */
    public static int random(int minNumber, int maxNumber) {
        return minNumber + (int) (Math.random() * (maxNumber - minNumber + 1));
    }

    /**
     * 生成Key
     *
     * @param server_ip
     * @param server_port
     * @param envir_ID
     * @param node_code
     * @return
     */
    private static String createKey(String server_ip, String server_port, String node_code) {
        return server_ip.concat("-").concat(server_port).concat("-").concat(node_code);
    }

    /**
     * 获取值
     *
     * @param node_code
     * @return
     */
    public boolean isCollectNode(String node_code) {
        String key = createKey(ServerInfo.getServer_ip(), ServerInfo.getServer_port(), node_code);
        if (this.nodes.containsKey(key)) {
            return this.nodes.get(key);
        }
        key = createKey(ServerInfo.getServer_ip(), "*", node_code);
        if (this.nodes.containsKey(key)) {
            return this.nodes.get(key);
        }
        key = createKey("*", ServerInfo.getServer_port(), node_code);
        if (this.nodes.containsKey(key)) {
            return this.nodes.get(key);
        }
        key = createKey("*", "*", node_code);
        if (this.nodes.containsKey(key)) {
            return this.nodes.get(key);
        }
        return true;
    }

    /**
     * 判断是否采集指定的环境
     *
     * @param envir_ID
     * @return
     */
    public boolean isCollectEnvir(String envir_ID) {
        for (ConfigeInfo.Envir envir : Configer.getEnvirs()) {
            if (envir.getEnvir_ID().equals(envir_ID)) {
                return envir.isCollect_envir();
            }
        }
        return true;
    }

    /**
     * 计算当前是否需要采样
     *
     * @return
     */
    public boolean isSampling() {
        return random(0, 100) <= Configer.getCapability().getSampling_rate();
    }
}
