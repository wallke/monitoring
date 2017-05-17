/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.builder;

import java.text.SimpleDateFormat;
import java.util.Date;
import xwtec.servercm.core.DataQueue;
import xwtec.servercm.init.Configer;
import xwtec.servercm.init.InitParamInfo;
import xwtec.servercm.collect.ServiceNode;

/**
 * 日志构造线程接口
 *
 * @author 张岩松
 */
public abstract class LogBuilder implements CollectTask {

    private ServiceNode serviceNode;
    //日志类型
    private String log_type = "";
//日志建造时获得-----------------------------------------------------------------/    
    //日志版本
    private String log_ver = "";
    //当前应用名称
    private String application_name = "";
    private String application_ver = "";
    //上衣应用名称
    private String last_application_name = "";
    //微环境ID
    private String envir_ID = "";
    //开始时间（关键时间）
    private Date time;
    //开始时间纳秒
    private long startTime;
    //结束时间纳秒
    private long endTime;
    //执行耗时(纳秒)
    private long interval;
    //执行状态
    private String status = "";
    //返回信息类型
    private String result_type = "";
    //返回信息
    private String result_info = "";
    //当前结点ID
    private String node_ID = "";
    //当前结点名称
    private String node_code = "";
    //上一级结点ID
    private String last_node_ID = "";
    //上一级结点名称
    private String last_node_code = "";
    //主机IP
    private String server_ip = "";
    //主机端口号
    private String server_port = "";
//构造日志----------------------------------------------------------------------/    
    //分隔符
    private String separator_one;
    private String separator_two;
    //最终生成的日志
    private String log;

    @Override
    public void run() {
        this.config();
        this.log = this.log_type;
        this.addMainLogParam(this.log_ver);
        this.addMainLogParam(Configer.getAppServer_name());
        this.addMainLogParam(Configer.getAppServer_ver());
        this.addMainLogParam(Configer.getLast_AppServer_name());
        this.addMainLogParam(this.envir_ID);
        this.addMainLogParam(this.getLogTime(this.time));
        this.addMainLogParam(String.valueOf(this.interval));
        this.addMainLogParam(this.status);
        this.addMainLogParam(this.result_type);
        this.addMainLogParam(this.result_info);
        this.addMainLogParam(this.serviceNode.getService_ID());
        this.addMainLogParam(this.node_ID);
        this.addMainLogParam(this.node_code);
        this.addMainLogParam(this.last_node_ID);
        this.addMainLogParam(this.last_node_code);
        this.addMainLogParam(this.server_ip);
        this.addMainLogParam(this.server_port);
        this.addMainLogParam(build(this.separator_one, this.separator_two));
        this.saveLog(this.log);
    }

    /**
     * 存储日志
     *
     * @param log
     */
    private void saveLog(String log) {
        DataQueue.addLog(log);
    }

    /**
     * 获得计算信息
     */
    private void config() {
        this.separator_one = Configer.instance().getSeparator().getLevel_one();
        this.separator_two = Configer.instance().getSeparator().getLevel_two();
        this.interval = this.endTime - this.startTime;
        this.log_ver = InitParamInfo.getInitParam().getLog_ver();
        this.envir_ID = this.serviceNode.getEnvir_ID();
    }

    /**
     * 开始补充建造
     *
     * @param separator_one
     * @param separator_two
     * @return
     */
    protected abstract String build(String separator_one, String separator_two);

    /**
     * 获得日志格式时间
     *
     * @param date
     * @return
     */
    protected String getLogTime(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return fmt.format(date);
    }

    /**
     * 向日志中添加一级参数
     *
     * @param param
     */
    protected void addMainLogParam(String param) {
        if (param != null) {
            this.log = this.log.concat(this.separator_one).concat(param);
        }
    }

    /**
     * 设置服务节点对象
     *
     * @param serviceNode
     */
    public void setServiceNode(ServiceNode serviceNode) {
        this.serviceNode = serviceNode;
    }

    /**
     * 设置日志类型
     *
     * @param log_type
     */
    public void setLog_type(String log_type) {
        this.log_type = log_type;
    }

    /**
     * 设置当前应用名称
     *
     * @param application_name
     */
    public void setApplication_name(String application_name) {
        this.application_name = application_name;
    }

    /**
     * 设置当前应用版本号
     *
     * @param application_ver
     */
    public void setApplication_ver(String application_ver) {
        this.application_ver = application_ver;
    }

    /**
     * 设置上级应用名称
     *
     * @param last_application_name
     */
    public void setLast_application_name(String last_application_name) {
        this.last_application_name = last_application_name;
    }

    /**
     * 设置开始时间，也就是关键时间
     *
     * @param time
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * 设置开始时的纳秒数
     *
     * @param startTime
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * 设置结束时的纳秒数
     *
     * @param endTime
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * 设置执行返回状态
     *
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 设置执行返回类型，包括异常类型
     *
     * @param result_type
     */
    public void setResult_type(String result_type) {
        this.result_type = result_type;
    }

    /**
     * 设置执行返回信息，包括异常信息
     *
     * @param result_info
     */
    public void setResult_info(String result_info) {
        this.result_info = result_info;
    }

    /**
     * 设置节点ID
     *
     * @param node_ID
     */
    public void setNode_ID(String node_ID) {
        this.node_ID = node_ID;
    }

    /**
     * 设置节点名称
     *
     * @param node_code
     */
    public void setNode_code(String node_code) {
        this.node_code = node_code;
    }

    /**
     * 设置上级节点ID
     *
     * @param last_node_ID
     */
    public void setLast_node_ID(String last_node_ID) {
        this.last_node_ID = last_node_ID;
    }

    /**
     * 设置上级节点名称
     *
     * @param last_node_code
     */
    public void setLast_node_code(String last_node_code) {
        this.last_node_code = last_node_code;
    }

    /**
     * 设置当前结点IP地址
     *
     * @param server_ip
     */
    public void setServer_ip(String server_ip) {
        this.server_ip = server_ip;
    }

    /**
     * 设置当前节点端口号
     *
     * @param server_port
     */
    public void setServer_port(String server_port) {
        this.server_port = server_port;
    }

    /**
     * 得到节点ID
     *
     * @return
     */
    public String getNode_ID() {
        return node_ID;
    }

    /**
     * 得到节点名称
     *
     * @return
     */
    public String getNode_code() {
        return node_code;
    }

}
