/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.init;

import java.io.Serializable;
import java.util.List;

/**
 * 配置信息
 *
 * @author 张岩松
 */
public class ConfigeInfo implements Serializable {

    //日志结构版本
    private boolean collect_all;
    //日志结构版本
    private String log_ver;
    //应用的名称
    private String appServer_name;
    //应用的版本
    private String appServer_ver;
    //分隔符
    private Separator separator;
    //性能参数
    private Capability capability;
    //应用程序信息
    private List<AppHost> hosts;
    //配置节点的类型
    private List<Envir> envirs;

    public boolean isCollect_all() {
        return collect_all;
    }

    public void setCollect_all(boolean collect_all) {
        this.collect_all = collect_all;
    }

    public String getLog_ver() {
        return log_ver;
    }

    public void setLog_ver(String log_ver) {
        this.log_ver = log_ver;
    }

    public String getAppServer_name() {
        return appServer_name;
    }

    public void setAppServer_name(String appServer_name) {
        this.appServer_name = appServer_name;
    }

    public String getAppServer_ver() {
        return appServer_ver;
    }

    public void setAppServer_ver(String appServer_ver) {
        this.appServer_ver = appServer_ver;
    }

    public Separator getSeparator() {
        return separator;
    }

    public void setSeparator(Separator separator) {
        this.separator = separator;
    }

    public Capability getCapability() {
        return capability;
    }

    public void setCapability(Capability capability) {
        this.capability = capability;
    }

    public List<AppHost> getHosts() {
        return hosts;
    }

    public void setHosts(List<AppHost> hosts) {
        this.hosts = hosts;
    }

    public List<Envir> getEnvirs() {
        return envirs;
    }

    public void setEnvirs(List<Envir> envirs) {
        this.envirs = envirs;
    }

    /**
     * 心跳控制
     *
     * @author 张岩松
     */
    public static class Heart {

        private boolean collect_heart;
        private String heart_type;
        private int heart_interval;

        public boolean isCollect_heart() {
            return collect_heart;
        }

        public void setCollect_heart(boolean collect_heart) {
            this.collect_heart = collect_heart;
        }

        public String getHeart_type() {
            return heart_type;
        }

        public void setHeart_type(String heart_type) {
            this.heart_type = heart_type;
        }

        public int getHeart_interval() {
            return heart_interval;
        }

        public void setHeart_interval(int heart_interval) {
            this.heart_interval = heart_interval;
        }
    }

    /**
     * 分隔符定义
     *
     * @author 张岩松
     */
    public static class Separator {

        private String level_one;
        private String level_two;

        public String getLevel_one() {
            return level_one;
        }

        public void setLevel_one(String level_one) {
            this.level_one = level_one;
        }

        public String getLevel_two() {
            return level_two;
        }

        public void setLevel_two(String level_two) {
            this.level_two = level_two;
        }

    }

    /**
     * 性能指标
     *
     * @author 张岩松
     */
    public static class Capability {

        //心跳控制
        private List<Heart> heart;
        //更新间隔时间
        private int update_interval;
        //采样率
        private int sampling_rate = 100;
        //日志缓存队列的最大尺寸
        private int logsBuffer_max_size;
        //建造日志的线程池大小
        private int buildLog_thread_size;

        public List<Heart> getHeart() {
            return heart;
        }

        public void setHeart(List<Heart> heart) {
            this.heart = heart;
        }

        public int getUpdate_interval() {
            return update_interval;
        }

        public void setUpdate_interval(int update_interval) {
            this.update_interval = update_interval;
        }

        public int getSampling_rate() {
            return sampling_rate;
        }

        public void setSampling_rate(int sampling_rate) {
            this.sampling_rate = sampling_rate;
        }

        public int getLogsBuffer_max_size() {
            return logsBuffer_max_size;
        }

        public void setLogsBuffer_max_size(int logsBuffer_max_size) {
            this.logsBuffer_max_size = logsBuffer_max_size;
        }

        public int getBuildLog_thread_size() {
            return buildLog_thread_size;
        }

        public void setBuildLog_thread_size(int buildLog_thread_size) {
            this.buildLog_thread_size = buildLog_thread_size;
        }

    }

    /**
     * 应用程序主机
     *
     * @author 张岩松
     */
    public static class AppHost implements Serializable {

        //控制IP
        private boolean collect_ip;
        //控制端口
        private boolean collect_port;
        //主机IP
        private String ip;
        //主机端口号
        private String port;
        //用于配置时显示用
        private String host_name;
        //节点
        private List<Node> nodes;

        public boolean isCollect_ip() {
            return collect_ip;
        }

        public void setCollect_ip(boolean collect_ip) {
            this.collect_ip = collect_ip;
        }

        public boolean isCollect_port() {
            return collect_port;
        }

        public void setCollect_port(boolean collect_port) {
            this.collect_port = collect_port;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String getHost_name() {
            return host_name;
        }

        public void setHost_name(String host_name) {
            this.host_name = host_name;
        }

        public List<Node> getNodes() {
            return nodes;
        }

        public void setNodes(List<Node> nodes) {
            this.nodes = nodes;
        }

    }

    /**
     * 微环境
     *
     * @author 张岩松
     */
    public static class Envir implements Serializable {

        //微环境采集控制
        private boolean collect_envir;
        //微环境ID
        private String envir_ID;
        //微环境描述名称
        private String envir_name;
        //环境中的节点
        private List<String> node_codes;

        public boolean isCollect_envir() {
            return collect_envir;
        }

        public void setCollect_envir(boolean collect_envir) {
            this.collect_envir = collect_envir;
        }

        public String getEnvir_ID() {
            return envir_ID;
        }

        public void setEnvir_ID(String envir_ID) {
            this.envir_ID = envir_ID;
        }

        public String getEnvir_name() {
            return envir_name;
        }

        public void setEnvir_name(String envir_name) {
            this.envir_name = envir_name;
        }

        public List<String> getNode_codes() {
            return node_codes;
        }

        public void setNode_codes(List<String> node_codes) {
            this.node_codes = node_codes;
        }

    }

    /**
     * 采集控制节点
     *
     * @author 张岩松
     */
    public static class Node implements Serializable {

        //节点采集控制
        private boolean collect_node;
        //节点名称描述
        private String node_name;
        //节点名称
        private String node_code;

        public boolean isCollect_node() {
            return collect_node;
        }

        public void setCollect_node(boolean collect_node) {
            this.collect_node = collect_node;
        }

        public String getNode_name() {
            return node_name;
        }

        public void setNode_name(String node_name) {
            this.node_name = node_name;
        }

        public String getNode_code() {
            return node_code;
        }

        public void setNode_code(String node_code) {
            this.node_code = node_code;
        }

    }

}
