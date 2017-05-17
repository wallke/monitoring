/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.collect;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import xwtec.servercm.builder.ChainLogBuilder;
import xwtec.servercm.builder.CollectTask;
import xwtec.servercm.builder.MethodLogBuilder;
import xwtec.servercm.builder.RequestLogBuilder;
import xwtec.servercm.core.DataQueue;
import xwtec.servercm.core.IDCreater;
import xwtec.servercm.init.Configer;

/**
 * 服务节点上下文
 *
 * @author 张岩松
 */
public class ServiceNodeContext {

    //服务节点配置
    private ServiceNode serviceNode;
    //本地方法节点执行任务堆栈
    private final Stack<MethodLogBuilder> methodStack = new Stack<MethodLogBuilder>();
    //当前服务节点执行任务
    private RequestLogBuilder current_service_log_builder;
    //当前服务节点执行任务
    private RequestLogBuilder target_service_log_builder;
    //调用链任务
    private ChainLogBuilder chain_log_builder;
    //最终可以生成日志的节点
    private final List<CollectTask> collectTasks = new ArrayList<CollectTask>();
    //当前是否采集标记
    private boolean collect = true;

    /**
     * 用方法名来打开新的上下文
     *
     * @param methodName
     * @param serviceNode
     */
    public void open(String methodName, ServiceNode serviceNode) {
        this.serviceNode = serviceNode;
        this.methodStack.clear();
        this.collectTasks.clear();
        this.current_service_log_builder = new RequestLogBuilder();
        this.current_service_log_builder.setNode_ID(this.serviceNode.getService_ID());
        this.current_service_log_builder.setNode_code(methodName);
        this.current_service_log_builder.setMode("server");
        this.current_service_log_builder.setApplication_name(Configer.getAppServer_name());
        this.current_service_log_builder.setApplication_ver(Configer.getAppServer_ver());
    }

    /**
     * 设置请求传入的客户端信息
     *
     * @param type
     * @param ip
     * @param port
     */
    public void setClientInfo(String type, String ip, String port) {
        this.current_service_log_builder.setClient_type(type);
        this.current_service_log_builder.setClient_ip(ip);
        this.current_service_log_builder.setClient_port(port);
    }

    /**
     * 关闭节点的采集
     */
    public void close() {
        if ((this.methodStack.empty()) && (this.target_service_log_builder == null) && (this.current_service_log_builder == null)) {
            this.buildLog();
        }
    }

    /**
     * 开始服务节点的采集
     *
     */
    public void startRequest() {
        this.current_service_log_builder.setLog_type("transaction");
        this.current_service_log_builder.setServiceNode(this.serviceNode);
        this.current_service_log_builder.setTime(new Date());
        this.current_service_log_builder.setServer_ip(ServerInfo.getServer_ip());
        this.current_service_log_builder.setServer_port(ServerInfo.getServer_port());
        this.current_service_log_builder.setStartTime(System.nanoTime());
    }

    /**
     * 服务节点执抛出的异常
     *
     * @param ex
     */
    private void request_exception(Exception ex) {
        this.current_service_log_builder.setEndTime(System.nanoTime());
        this.current_service_log_builder.setStatus("1");
        this.current_service_log_builder.setResult_type(ex.getClass().getName());
        this.current_service_log_builder.setResult_info(this.getLogExceptionMessage(ex));
    }

    /**
     * 服务节点执抛出的异常
     *
     * @param ex
     */
    private void endRequest() {
        this.current_service_log_builder.setEndTime(System.nanoTime());
        this.current_service_log_builder.setStatus("0");
        this.current_service_log_builder.setResult_type("");
        this.current_service_log_builder.setResult_info("");
        this.collectTasks.add(this.current_service_log_builder);
        this.current_service_log_builder = null;
    }

    /**
     * 结束节点
     *
     * @param ex
     */
    public void endNode(Exception ex) {
        if (this.current_service_log_builder == null) {
            return;
        }
        if (ex == null) {
            this.endNode();
        }
        if (!this.methodStack.empty()) {
            this.method_exception(ex);
        }
        this.request_exception(ex);
    }

    /**
     * 结束节点
     */
    public void endNode() {
        if (this.current_service_log_builder == null) {
            return;
        }
        if (this.methodStack.empty()) {
            this.endRequest();
        } else {
            this.endMethod();
        }
    }

    /**
     * 开始方法节点的采集
     *
     * @param methodName
     */
    public void startMethod(String methodName) {
        MethodLogBuilder methodLogBuilder = new MethodLogBuilder();
        methodLogBuilder.setLog_type("method");
        methodLogBuilder.setServiceNode(this.serviceNode);
        methodLogBuilder.setTime(new Date());
        methodLogBuilder.setNode_ID(IDCreater.createNodeID());
        methodLogBuilder.setNode_code(methodName);
        if (this.methodStack.empty()) {
            methodLogBuilder.setLast_node_ID(this.current_service_log_builder.getNode_ID());
            methodLogBuilder.setLast_node_code(this.current_service_log_builder.getNode_code());
        } else {
            MethodLogBuilder lastMethodLogBuilder = this.methodStack.peek();
            methodLogBuilder.setLast_node_ID(lastMethodLogBuilder.getNode_ID());
            methodLogBuilder.setLast_node_code(lastMethodLogBuilder.getNode_code());
        }
        methodLogBuilder.setServer_ip(ServerInfo.getServer_ip());
        methodLogBuilder.setServer_port(ServerInfo.getServer_port());
        this.methodStack.push(methodLogBuilder);
        methodLogBuilder.setStartTime(System.nanoTime());
    }

    /**
     * 本地方法抛出的异常
     *
     * @param ex
     */
    private void method_exception(Exception ex) {
        long endTime = System.nanoTime();
        boolean isSet = false;
        while (!this.methodStack.empty()) {
            MethodLogBuilder methodLogBuilder = this.methodStack.pop();
            methodLogBuilder.setEndTime(endTime);
            methodLogBuilder.setStatus("1");
            if (!isSet) {
                methodLogBuilder.setResult_type(ex.getClass().getName());
                methodLogBuilder.setResult_info(this.getLogExceptionMessage(ex));
                isSet = true;
            }
            this.collectTasks.add(methodLogBuilder);
        }
    }

    /**
     * 结束方法节点的采集
     *
     * @param status
     */
    private void endMethod() {
        MethodLogBuilder methodLogBuilder = this.methodStack.pop();
        methodLogBuilder.setEndTime(System.nanoTime());
        methodLogBuilder.setStatus("0");
        methodLogBuilder.setResult_type("");
        methodLogBuilder.setResult_info("");
        this.collectTasks.add(methodLogBuilder);
    }

    /**
     * 开始远程调用,远端一定无法埋点
     *
     * @param remoteName
     */
    public void startRemote(String remoteName) {
        //目标节点
        this.target_service_log_builder = new RequestLogBuilder();
        this.target_service_log_builder.setLog_type("transaction");
        this.target_service_log_builder.setServiceNode(this.serviceNode);
        this.target_service_log_builder.setTime(new Date());
        this.target_service_log_builder.setMode("client");
        this.target_service_log_builder.setLast_node_ID(this.current_service_log_builder.getNode_ID());
        this.target_service_log_builder.setLast_node_code(this.current_service_log_builder.getNode_code());
        this.target_service_log_builder.setNode_ID(IDCreater.createNodeID());
        this.target_service_log_builder.setNode_code(remoteName);
        this.target_service_log_builder.setLast_application_name(Configer.getAppServer_name());
        this.target_service_log_builder.setClient_type("Remote");
        this.target_service_log_builder.setClient_ip(ServerInfo.getServer_ip());
        this.target_service_log_builder.setClient_port(ServerInfo.getServer_port());
        //配置调用链
        this.chain_log_builder = new ChainLogBuilder();
        this.chain_log_builder.setLog_type("chain");
        this.chain_log_builder.setServiceNode(this.serviceNode);
        this.chain_log_builder.setTime(new Date());
        this.chain_log_builder.setLast_node_ID(this.current_service_log_builder.getNode_ID());
        this.chain_log_builder.setLast_node_code(this.current_service_log_builder.getNode_code());
        this.chain_log_builder.setNode_code(remoteName);
        this.chain_log_builder.setLast_application_name(Configer.getAppServer_name());
        long startTime = System.nanoTime();
        this.chain_log_builder.setStartTime(startTime);
        this.target_service_log_builder.setStartTime(startTime);
    }

    /**
     * 设置远程主机
     *
     * @param applicationName
     */
    public void setRemoteApplicationName(String applicationName) {
        if (this.target_service_log_builder != null) {
            this.target_service_log_builder.setApplication_name(applicationName);
        }
        if (this.chain_log_builder != null) {
            this.chain_log_builder.setApplication_name(applicationName);
        }
    }

    /**
     * 结束远程调用
     *
     */
    public void endRemote() {
        long endTime = System.nanoTime();
        //处理目标节点
        if (this.target_service_log_builder != null) {
            this.target_service_log_builder.setEndTime(endTime);
            DataQueue.addCollectTasks(this.target_service_log_builder);
            this.target_service_log_builder = null;
        }
        //处理调用链
        if (this.chain_log_builder != null) {
            this.chain_log_builder.setEndTime(endTime);
            DataQueue.addCollectTasks(this.chain_log_builder);
            this.chain_log_builder = null;
        }
    }

    /**
     * 设置远程调用的返回结果
     *
     * @param status
     * @param result_type
     * @param result_info
     */
    public void setRemoteResult(String status, String result_type, String result_info) {
        if (this.target_service_log_builder != null) {
            this.target_service_log_builder.setStatus(status);
            this.target_service_log_builder.setResult_type(result_type);
            this.target_service_log_builder.setResult_info(result_info);
        }
        if (this.chain_log_builder != null) {
            this.chain_log_builder.setStatus(status);
            this.chain_log_builder.setResult_type(result_type);
            this.chain_log_builder.setResult_info(result_info);
        }
    }

    /**
     * 设置远程调用出现异常
     *
     * @param exception
     */
    public void setRemoteResult(Exception exception) {
        if (exception == null) {
            this.setRemoteResult("0", "", "");
            return;
        }
        String result_type = exception.getClass().getName();
        String result_info = this.getLogExceptionMessage(exception);
        if (this.target_service_log_builder != null) {
            this.target_service_log_builder.setStatus("2");
            this.target_service_log_builder.setResult_type(result_type);
            this.target_service_log_builder.setResult_info(result_info);
        }
        if (this.chain_log_builder != null) {
            this.chain_log_builder.setStatus("2");
            this.chain_log_builder.setResult_type(result_type);
            this.chain_log_builder.setResult_info(result_info);
        }
    }

    /**
     * 设置远程调用模式
     *
     * @param mode
     */
    public void setRemoteMode(String mode) {
        if (mode.equals("server")) {
            this.target_service_log_builder = null;
        }
    }

    /**
     * 转换异常消息
     *
     * @param ex
     * @return
     */
    private String getLogExceptionMessage(Exception ex) {
        String message = ex.getMessage();
        if (message == null) {
            message = "";
        }
        return message.concat(this.exceptionStack(ex.getStackTrace()));
    }

    /**
     * 将错误堆栈合并成字符串
     *
     * @param stackTraceElements
     * @return
     */
    private String exceptionStack(StackTraceElement[] stackTraceElements) {
        String msg = "";
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            if (msg.isEmpty()) {
                msg = "\\u0003".concat(stackTraceElement.toString());
            } else {
                msg = msg.concat("\\u0003").concat(stackTraceElement.toString());
            }
        }
        return msg;
    }

    /**
     * 开始建造日志
     */
    private void buildLog() {
        for (CollectTask collectTask : this.collectTasks) {
            DataQueue.addCollectTasks(collectTask);
        }
    }

    /**
     * 设置客户端类型
     *
     * @param type
     */
    public void setClient_type(String type) {
        this.current_service_log_builder.setClient_type(type);
    }

    /**
     * 得到服务ID
     *
     * @return
     */
    public String getService_ID() {
        return this.serviceNode.getService_ID();
    }

    /**
     * 得到当前的环境ID
     *
     * @return
     */
    public String getEnvir_ID() {
        return this.serviceNode.getEnvir_ID();
    }

    /**
     * 设置从其他节点传递过来的环境ID
     *
     * @param envir_ID
     */
    public void setEnvir_ID(String envir_ID) {
        this.serviceNode.setEnvir_ID(envir_ID);
    }

    /**
     * 得到当前的服务节点ID
     *
     * @return
     */
    public String getCurrent_node_ID() {
        return this.current_service_log_builder.getNode_ID();
    }

    /**
     * 得到当前的服务节点名称
     *
     * @return
     */
    public String getCurrent_node_code() {
        return this.current_service_log_builder.getNode_code();
    }

    /**
     * 设置请求上一级节点ID
     *
     * @param last_node_ID
     */
    public void setLast_node_ID(String last_node_ID) {
        this.current_service_log_builder.setLast_node_ID(last_node_ID);
    }

    /**
     * 设置请求上一级节点名称
     *
     * @param last_node_code
     */
    public void setLast_node_code(String last_node_code) {
        this.current_service_log_builder.setLast_node_code(last_node_code);
    }

    /**
     * 设置请求上一级节点服务ID
     *
     * @param service_ID
     */
    public void setService_ID(String service_ID) {
        this.serviceNode.setService_ID(service_ID);
    }

    /**
     * 设置自定义属性
     *
     * @param custm_info
     */
    public void setCustm_Info(Map<String, String> custm_info) {
        this.current_service_log_builder.setCustm_propertys(custm_info);
    }

    /**
     * 设置客户端信息
     *
     * @param client_info
     */
    public void setClient_Infos(List<String> client_info) {
        this.current_service_log_builder.setClient_infos(client_info);
    }

    /**
     * 判断当前是否采集
     *
     * @return
     */
    public boolean isCollect() {
        return this.collect;
    }

    /**
     * 设置当前是否采集
     *
     * @param collect
     */
    public void setCollect(boolean collect) {
        this.collect = collect;
    }

}
