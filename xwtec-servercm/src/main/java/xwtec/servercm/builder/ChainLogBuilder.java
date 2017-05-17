/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.builder;

import java.text.SimpleDateFormat;
import java.util.Date;
import xwtec.servercm.collect.ServiceNode;
import xwtec.servercm.core.DataQueue;
import xwtec.servercm.init.Configer;
import xwtec.servercm.init.InitParamInfo;

/**
 * 调用链日志建造者
 *
 * @author 张岩松
 */
public class ChainLogBuilder implements CollectTask {

    private ServiceNode serviceNode;
    //日志类型
    private String log_type = "";
//日志建造时获得-----------------------------------------------------------------/    
    //日志版本
    private String log_ver = "";
    //当前应用名称
    private String application_name = "";
    //上衣应用名称
    private String last_application_name = "";
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
    //当前结点名称
    private String node_code = "";
    //上一级结点ID
    private String last_node_ID = "";
    //上一级结点名称
    private String last_node_code = "";
//构造日志----------------------------------------------------------------------/    
    //分隔符
    private String separator_one;
    //最终生成的日志
    private String log;

    @Override
    public void run() {
        this.config();
        this.log = this.log_type;
        this.addMainLogParam(this.log_ver);
        this.addMainLogParam(this.application_name);
        this.addMainLogParam(this.last_application_name);
        this.addMainLogParam(this.getLogTime(this.time));
        this.addMainLogParam(String.valueOf(this.interval));
        this.addMainLogParam(this.status);
        this.addMainLogParam(this.result_type);
        this.addMainLogParam(this.result_info);
        this.addMainLogParam(this.serviceNode.getService_ID());
        this.addMainLogParam(this.node_code);
        this.addMainLogParam(this.last_node_ID);
        this.addMainLogParam(this.last_node_code);
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
        this.interval = this.endTime - this.startTime;
        this.log_ver = InitParamInfo.getInitParam().getLog_ver();
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
     * 得到节点名称
     *
     * @return
     */
    public String getNode_code() {
        return node_code;
    }

}
