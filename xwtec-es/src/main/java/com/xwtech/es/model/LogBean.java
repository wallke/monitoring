package com.xwtech.es.model;

/**
 * Created by zhangq on 2017/2/17.
 */
public class LogBean {


    /**
     * 日志类型 当前日志的类型名称，取值：transaction
     */
    String log_type;

    /**
     * 日志结构版本 日志结构的版本号
     */
    String log_ver;


    /**
     * 应用名称 当前执行的系统名称，或者渠道名称
     */
    String system;

    String system_name;

    String last_system;

    /**
     * 系统版本号  当前应用的版本号
     */
    String ver;

    /**
     * 微环境ID 当前事务所属微环境，可以作为识别某类事务的名称，比如某个活动的名称等，微环境可以包含一组不同的事务，缺省值为：default
     */
    String envir_ID;


    String envir_name;

    /**
     * 关键时间 当前节点的执行时间
     */
    String time;

    /**
     * 执行耗时 当前节点执行的总耗时纳秒数
     */
    double interval;


    /**
     * 状态 当前节点执行后的返回码
     */
    String status;

    /**
     * 返回信息类型 返回信息的类型，比如各种具体的异常类型等
     */
    String result_type;


    /**
     * 返回信息 当前节点执行后的返回的信息
     */
    String result_info;

    /**
     * 服务编码 当前一次针对服务的请求的ID
     */
    String service_ID;

    /**
     * 当前节点ID 当前事务中，当前节点的ID
     */
    String node_ID;

    /**
     * 节点名称 当前节点的名称，也可以是方法名称等
     */
    String node_code;


    /**
     * 上级节点ID 当前事务中，上级节点的ID，若当前结点是根节点，则此为空
     */
    String last_node_ID;

    /**
     * 上级节点名称 上级节点的名称，也可以是方法名称等
     */
    String last_node_code;


    /**
     * 主机IP 当前节点执行的服务器地址
     */
    String server_ip;


    /**
     * 执行端口 当前节点执行的服务器端口
     */
    String server_port;


    /**
     * 调用模式
     */
    String mode;


    /**
     * 客户端类型 Android|IOS|Server|Method
     */
    String client_type;


    /**
     * 客户端IP 客户端地址
     */
    String client_ip;


    /**
     * 客户端端口
     */
    String client_port;


    /**
     * 终端信息
     */
    String client_info;
    /**
     * 自定义属性 针对业务可自定义的属性集合，用字符”&“进行分割，属性是”key=value“结构
     */
    String custm_property;

    String prefix_custm_property;

    /**
     *具体发起调用的方法节点ID
     */
    String invoke_node_ID;


    /**
     * 具体发起调用的方法名称
     */
    String invoke_node_code;

    public String getPrefix_custm_property() {
        if(this.custm_property != null && custm_property.length() > 10){
            return this.custm_property.substring(0,9).concat("...") ;
        }
        return this.custm_property;
    }

    public void setPrefix_custm_property(String prefix_custm_property) {
        //this.prefix_custm_property = prefix_custm_property;
    }

    public String getLog_type() {
        return log_type;
    }

    public void setLog_type(String log_type) {
        this.log_type = log_type;
    }

    public String getLog_ver() {
        return log_ver;
    }

    public void setLog_ver(String log_ver) {
        this.log_ver = log_ver;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getEnvir_ID() {
        return envir_ID;
    }

    public void setEnvir_ID(String envir_ID) {
        this.envir_ID = envir_ID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getInterval() {
        return interval;
    }

    public void setInterval(double interval) {
        this.interval = interval;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult_type() {
        return result_type;
    }

    public void setResult_type(String result_type) {
        this.result_type = result_type;
    }

    public String getResult_info() {
        return result_info;
    }

    public void setResult_info(String result_info) {
        this.result_info = result_info;
    }

    public String getService_ID() {
        return service_ID;
    }

    public void setService_ID(String service_ID) {
        this.service_ID = service_ID;
    }

    public String getNode_ID() {
        return node_ID;
    }

    public void setNode_ID(String node_ID) {
        this.node_ID = node_ID;
    }

    public String getNode_code() {
        return node_code;
    }

    public void setNode_code(String node_code) {
        this.node_code = node_code;
    }

    public String getLast_node_ID() {
        return last_node_ID;
    }

    public void setLast_node_ID(String last_node_ID) {
        this.last_node_ID = last_node_ID;
    }

    public String getLast_node_code() {
        return last_node_code;
    }

    public void setLast_node_code(String last_node_code) {
        this.last_node_code = last_node_code;
    }

    public String getServer_ip() {
        return server_ip;
    }

    public void setServer_ip(String server_ip) {
        this.server_ip = server_ip;
    }

    public String getServer_port() {
        return server_port;
    }

    public void setServer_port(String server_port) {
        this.server_port = server_port;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getClient_type() {
        return client_type;
    }

    public void setClient_type(String client_type) {
        this.client_type = client_type;
    }

    public String getClient_ip() {
        return client_ip;
    }

    public void setClient_ip(String client_ip) {
        this.client_ip = client_ip;
    }

    public String getClient_port() {
        return client_port;
    }

    public void setClient_port(String client_port) {
        this.client_port = client_port;
    }

    public String getClient_info() {
        return client_info;
    }

    public void setClient_info(String client_info) {
        this.client_info = client_info;
    }

    public String getCustm_property() {
        return custm_property;
    }

    public void setCustm_property(String custm_property) {
        this.custm_property = custm_property;
    }

    public String getInvoke_node_ID() {
        return invoke_node_ID;
    }

    public void setInvoke_node_ID(String invoke_node_ID) {
        this.invoke_node_ID = invoke_node_ID;
    }

    public String getInvoke_node_code() {
        return invoke_node_code;
    }

    public void setInvoke_node_code(String invoke_node_code) {
        this.invoke_node_code = invoke_node_code;
    }

    public String getLast_system() {
        return last_system;
    }

    public void setLast_system(String last_system) {
        this.last_system = last_system;
    }

    public String getSystem_name() {
        return system_name;
    }

    public void setSystem_name(String system_name) {
        this.system_name = system_name;
    }

    public String getEnvir_name() {
        return envir_name;
    }

    public void setEnvir_name(String envir_name) {
        this.envir_name = envir_name;
    }



    @Override
    public String toString() {
        return "LogBean{" +
                "log_type='" + log_type + '\'' +
                ", log_ver='" + log_ver + '\'' +
                ", system='" + system + '\'' +
                ", last_system='" + last_system + '\'' +
                ", ver='" + ver + '\'' +
                ", envir_ID='" + envir_ID + '\'' +
                ", time='" + time + '\'' +
                ", interval=" + interval +
                ", status='" + status + '\'' +
                ", result_type='" + result_type + '\'' +
                ", result_info='" + result_info + '\'' +
                ", service_ID='" + service_ID + '\'' +
                ", node_ID='" + node_ID + '\'' +
                ", node_code='" + node_code + '\'' +
                ", last_node_ID='" + last_node_ID + '\'' +
                ", last_node_code='" + last_node_code + '\'' +
                ", server_ip='" + server_ip + '\'' +
                ", server_port='" + server_port + '\'' +
                ", mode='" + mode + '\'' +
                ", client_type='" + client_type + '\'' +
                ", client_ip='" + client_ip + '\'' +
                ", client_port='" + client_port + '\'' +
                ", client_info='" + client_info + '\'' +
                ", custm_property='" + custm_property + '\'' +
                ", invoke_node_ID='" + invoke_node_ID + '\'' +
                ", invoke_node_code='" + invoke_node_code + '\'' +
                '}';
    }
}
