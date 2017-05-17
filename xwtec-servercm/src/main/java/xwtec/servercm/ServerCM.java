/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm;

import java.util.List;
import xwtec.servercm.collect.ServiceNodeContext;
import java.util.Map;
import xwtec.servercm.builder.CollectTaskFinder;
import xwtec.servercm.builder.LocalNodeException;
import xwtec.servercm.builder.StopCollectException;
import xwtec.servercm.core.CollectCtl;
import xwtec.servercm.core.Print;
import xwtec.servercm.core.ThreadManager;
import xwtec.servercm.heart.HeartManager;
import xwtec.servercm.init.ConfigException;
import xwtec.servercm.init.InitParamInfo;
import xwtec.servercm.collect.ServerInfo;
import xwtec.servercm.collect.ServiceNode;
import xwtec.servercm.init.Configer;
import xwtec.servercm.init.StatusException;
import xwtec.servercm.status.RunStatus;
import xwtec.servercm.status.StatusManager;
import xwtec.servercm.store.LogListener;
import xwtec.servercm.store.LogStore;

/**
 * 服务端采集管理器
 *
 * @author 张岩松
 */
public class ServerCM {

    //采集管理器的实例
    private static final ServerCM serverCM = new ServerCM();
    //调用链节点信息
    private static final ThreadLocal<ServiceNodeContext> threadLocal = new ThreadLocal<ServiceNodeContext>();
    //状态管理器
    public static final StatusManager statusManager = new StatusManager();

//启动方法----------------------------------------------------------------------/    
    /**
     * 启动采集管理器
     *
     * @param server_ip
     * @param server_port
     */
    public synchronized static void open(String server_ip, String server_port) {
        ServerInfo.setServer_ip(server_ip);
        ServerInfo.setServer_port(server_port);
        ThreadManager.instance().startStatusManager(new StatusManager());
        ThreadManager.instance().startLogListener(new LogListener());
        ThreadManager.instance().startCollectTaskFinder(new CollectTaskFinder());
    }

    /**
     * 设置Yaml的路径
     *
     * @param yamlPath
     */
    public static void setYamlPath(String yamlPath) {
        InitParamInfo.setYamlPath(yamlPath);
    }

    /**
     * 设置日志存储对象
     *
     * @param logStore
     */
    public static void setLogStore(LogStore logStore) {
        LogListener.logStore = logStore;
    }

    /**
     * 停止当前采集管理器
     */
    public synchronized static void close() {
        //关闭心跳管理器
        HeartManager.instance().close();
        //停止剩余的线程
        ThreadManager.instance().close();
    }

    /**
     * 获得服务端采集管理器的实例
     *
     * @return
     * @throws StatusException
     */
    public static ServerCM instance() throws StatusException {
        if (RunStatus.isDisable()) {
            Print.exception("采集管理器当前未启动");
            throw new StatusException();
        }
        if (RunStatus.isSleep()) {
            Print.message("采集管理器正在加载配置信息，当前采集暂时停止");
            throw new StatusException();
        }
        return serverCM;
    }

//API接口-----------------------------------------------------------------------/    
    /**
     * 在当前的方法中启动采集管理器
     *
     * @param methodName
     */
    public static void start(String methodName) {
        try {
            ServerCM.instance().collectMethod(methodName);
        } catch (StatusException ex) {
        } catch (LocalNodeException ex) {
        } catch (StopCollectException ex) {
        } catch (Exception ex) {
            Print.exception(ex.getMessage());
        }
    }

    /**
     * 在当前的方法中启动采集管理器
     *
     * @param methodName
     * @param requestParam
     */
    public static void start(String methodName, String requestParam) {
        try {
            ServerCM.instance().collectRequest(methodName, requestParam);
        } catch (StatusException ex) {
        } catch (LocalNodeException ex) {
        } catch (StopCollectException ex) {
        } catch (Exception ex) {
            Print.exception(ex.getMessage());
        }
    }

    /**
     * 在当前的请求处理服务接口方法中启动采集管理器
     *
     * @param methodName
     * @param client_type
     * @param client_ip
     * @param client_port
     */
    public static void start(String methodName, String client_type, String client_ip, String client_port) {
        try {
            ServerCM.instance().collectRequest(methodName, client_type, client_ip, client_port);
        } catch (StatusException ex) {
        } catch (LocalNodeException ex) {
        } catch (StopCollectException ex) {
        } catch (Exception ex) {
            Print.exception(ex.getMessage());
        }
    }

    /**
     * 获得远程调用请求参数
     *
     * @return
     */
    public static String httpRequestParam() {
        try {
            return ServerCM.instance().getHttpRequestParam();
        } catch (StatusException ex) {
        } catch (LocalNodeException ex) {
        } catch (ConfigException ex) {
        } catch (Exception ex) {
            Print.exception(ex.getMessage());
        }
        return "";
    }

    /**
     * 开始调用外部接口
     *
     * @param applicationName
     * @param serverName
     */
    public static void request(String applicationName, String serverName) {
        try {
            ServerCM.instance().invork(applicationName, serverName);
        } catch (StatusException ex) {
        } catch (LocalNodeException ex) {
        } catch (Exception ex) {
            Print.exception(ex.getMessage());
        }
    }

    /**
     * 结束调用外部接口，并通知调用结果
     *
     * @param resultCode
     * @param resultInfo
     */
    public static void response(String resultCode, String resultInfo) {
        try {
            ServerCM.instance().result(resultCode, resultInfo);
        } catch (StatusException ex) {
        } catch (LocalNodeException ex) {
        } catch (Exception ex) {
            Print.exception(ex.getMessage());
        }
    }

    /**
     * 结束调用外部接口，并通知调用结果
     *
     * @param httpStatusCode
     */
    public static void response(String httpStatusCode) {
        try {
            ServerCM.instance().result(httpStatusCode);
        } catch (StatusException ex) {
        } catch (LocalNodeException ex) {
        } catch (Exception ex) {
            Print.exception(ex.getMessage());
        }
    }

    /**
     * 外部请求异常结束
     *
     * @param exception
     */
    public static void response(Exception exception) {
        try {
            ServerCM.instance().result(exception);
        } catch (StatusException ex) {
        } catch (LocalNodeException ex) {
        } catch (Exception ex) {
            Print.exception(ex.getMessage());
        }
    }

    /**
     * 结束并退出对当前方法的采集
     */
    public static void exit() {
        try {
            ServerCM.instance().commit();
        } catch (StatusException ex) {
        } catch (Exception ex) {
            Print.exception(ex.getMessage());
        }
    }

    /**
     * 遇到异常，结束并退出对当前方法的采集
     *
     * @param exception
     */
    public static void exit(Exception exception) {
        try {
            ServerCM.instance().commit(exception);
        } catch (StatusException ex) {
        } catch (Exception ex) {
            Print.exception(ex.getMessage());
        }
    }

    /**
     * 为本地服务请求设置自定义属性
     *
     * @param custm_infos
     */
    public static void setCustmInfos(Map<String, String> custm_infos) {
        try {
            ServerCM.instance().setCustm_Infos(custm_infos);
        } catch (StatusException ex) {
        } catch (Exception ex) {
            Print.exception(ex.getMessage());
        }
    }

    /**
     * 手工为本地服务请求设置客户端信息
     *
     * @param client_infos
     */
    public static void setClientInfos(List<String> client_infos) {
        try {
            ServerCM.instance().setClient_infos(client_infos);
        } catch (StatusException ex) {
        } catch (Exception ex) {
            Print.exception(ex.getMessage());
        }
    }

//实现方法----------------------------------------------------------------------/   
    /**
     * 在当前的方法中启动采集管理器
     *
     * @param methodName
     * @throws LocalNodeException
     * @throws StopCollectException
     */
    public void collectMethod(String methodName) throws LocalNodeException, StopCollectException {
        ServiceNodeContext serviceNodeContext = threadLocal.get();
        if (serviceNodeContext == null) {
            serviceNodeContext = new ServiceNodeContext();
            threadLocal.set(serviceNodeContext);
            this.collectRequest(methodName, "", "", "");
        } else {
            if (serviceNodeContext.isCollect()) {
                serviceNodeContext.startMethod(methodName);
            }
        }
    }

    /**
     * 在当前的方法中启动采集管理器
     *
     * @param methodName
     * @param requestParam
     * @throws LocalNodeException
     * @throws StopCollectException
     */
    public void collectRequest(String methodName, String requestParam) throws LocalNodeException, StopCollectException {
        String[] requestParams = requestParam.split("\\|");
        ServiceNodeContext serviceNodeContext = this.getServiceNodeContext();
        this.openServiceNode(serviceNodeContext, methodName);
        serviceNodeContext.setService_ID(requestParams[0]);
        Configer.setLast_AppServer_name(requestParams[1]);
        serviceNodeContext.setLast_node_code(requestParams[2]);
        serviceNodeContext.setLast_node_ID(requestParams[3]);
        serviceNodeContext.setClientInfo("Server", requestParams[4], requestParams[5]);
        serviceNodeContext.setEnvir_ID(requestParams[6]);
        serviceNodeContext.startRequest();
    }

    /**
     * 在当前的方法中启动采集管理器
     *
     * @param methodName
     * @param client_type
     * @param client_ip
     * @param client_port
     * @throws LocalNodeException
     * @throws StopCollectException
     */
    public void collectRequest(String methodName, String client_type, String client_ip, String client_port) throws LocalNodeException, StopCollectException {
        ServiceNodeContext serviceNodeContext = this.getServiceNodeContext();
        this.openServiceNode(serviceNodeContext, methodName);
        serviceNodeContext.setClientInfo(client_type, client_ip, client_port);
        serviceNodeContext.startRequest();
    }

    /**
     * 在当前的方法中启动采集管理器
     *
     * @param custm_info
     */
    public void setCustm_Infos(Map<String, String> custm_info) {
        ServiceNodeContext serviceNodeContext = this.getServiceNodeContext();
        serviceNodeContext.setCustm_Info(custm_info);
    }

    /**
     * 在当前的方法中启动采集管理器
     *
     * @param client_info
     */
    public void setClient_infos(List<String> client_info) {
        ServiceNodeContext serviceNodeContext = this.getServiceNodeContext();
        serviceNodeContext.setClient_Infos(client_info);
    }

    /**
     * 获取节点上下文
     *
     * @param methodName
     */
    private ServiceNodeContext getServiceNodeContext() {
        ServiceNodeContext serviceNodeContext = threadLocal.get();
        if (serviceNodeContext == null) {
            serviceNodeContext = new ServiceNodeContext();
            threadLocal.set(serviceNodeContext);
        }
        return serviceNodeContext;
    }

    /**
     * 打开服务节点
     *
     * @param serviceNodeContext
     * @param methodName
     * @throws StopCollectException
     */
    private void openServiceNode(ServiceNodeContext serviceNodeContext, String methodName) throws StopCollectException {
        ServiceNode serviceNode = ServiceNode.instance(methodName);
        serviceNodeContext.setCollect(CollectCtl.isCollect(serviceNode.getEnvir_ID(), methodName));
        if (!serviceNodeContext.isCollect()) {
            throw new StopCollectException();
        }
        serviceNodeContext.open(methodName, serviceNode);
    }

    /**
     * 调用外部接口
     *
     * @param applicationName
     * @param serverName
     * @throws LocalNodeException
     */
    public void invork(String applicationName, String serverName) throws LocalNodeException {
        ServiceNodeContext serviceNodeContext = threadLocal.get();
        if (serviceNodeContext == null) {
            throw new LocalNodeException();
        }
        if (serviceNodeContext.isCollect()) {
            serviceNodeContext.startRemote(serverName);
            serviceNodeContext.setRemoteApplicationName(applicationName);
        }
    }

    /**
     * 调用外部接口返回
     *
     * @param resultCode
     * @param resultInfo
     * @throws LocalNodeException
     */
    public void result(String resultCode, String resultInfo) throws LocalNodeException {
        ServiceNodeContext serviceNodeContext = threadLocal.get();
        if (serviceNodeContext == null) {
            throw new LocalNodeException();
        }
        if (serviceNodeContext.isCollect()) {
            serviceNodeContext.setRemoteResult(resultCode, "", resultInfo);
            serviceNodeContext.endRemote();
        }
    }

    /**
     * 调用外部接口返回
     *
     * @param httpStatusCode
     * @throws LocalNodeException
     */
    public void result(String httpStatusCode) throws LocalNodeException {
        this.result(httpStatusCode, "");
    }

    /**
     * 调用外部接口遇到异常
     *
     * @param exception
     * @throws LocalNodeException
     */
    public void result(Exception exception) throws LocalNodeException {
        ServiceNodeContext serviceNodeContext = threadLocal.get();
        if (serviceNodeContext == null) {
            throw new LocalNodeException();
        }
        if (serviceNodeContext.isCollect()) {
            serviceNodeContext.setRemoteResult(exception);
            serviceNodeContext.endRemote();
        }
    }

    /**
     * 得到Http远程调用的请求参数
     *
     * @return
     * @throws LocalNodeException
     * @throws ConfigException
     */
    public String getHttpRequestParam() throws LocalNodeException, ConfigException {
        ServiceNodeContext serviceNodeContext = threadLocal.get();
        if (serviceNodeContext == null) {
            throw new LocalNodeException();
        }
        if (!serviceNodeContext.isCollect()) {
            return "";
        }
        serviceNodeContext.setRemoteMode("server");
        return serviceNodeContext.getService_ID()
                .concat("|").concat(Configer.getAppServer_name())
                .concat("|").concat(Configer.getAppServer_ver())
                .concat("|").concat(serviceNodeContext.getCurrent_node_code())
                .concat("|").concat(serviceNodeContext.getCurrent_node_ID())
                .concat("|").concat(ServerInfo.getServer_ip())
                .concat("|").concat(ServerInfo.getServer_port())
                .concat("|").concat(serviceNodeContext.getEnvir_ID());
    }

    /**
     * 将当前方法埋点采集的内容提交给采集管理器
     *
     */
    public void commit() {
        ServiceNodeContext serviceNodeContext = this.getServiceNodeContext();
        if (serviceNodeContext.isCollect()) {
            serviceNodeContext.endNode();
            serviceNodeContext.close();
        }
    }

    /**
     * 将当前方法埋点采集的内容提交给采集管理器
     *
     * @param exception
     */
    public void commit(Exception exception) {
        ServiceNodeContext serviceNodeContext = this.getServiceNodeContext();
        if (serviceNodeContext.isCollect()) {
            serviceNodeContext.endNode(exception);
            serviceNodeContext.close();
        }
    }

}
