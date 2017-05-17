/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.init;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.ho.yaml.Yaml;
import org.ho.yaml.exception.YamlException;
import static xwtec.servercm.init.InitParamInfo.getInitParam;

/**
 * 线程安全的初始化参数访问
 *
 * @author 张岩松
 */
public class InitParamInfo {

    //初始化参数
    private static InitParam initParam;
    //启动状态读写锁
    private final static ReentrantReadWriteLock initParamLock = new ReentrantReadWriteLock();
    //Yaml路径
    private static String yamlPath;

    /**
     * 设置Yaml配置文件的路径
     *
     * @param yamlPath
     */
    public static void setYamlPath(String yamlPath) {
        InitParamInfo.yamlPath = yamlPath;
    }

    /**
     * 获取初始化参数
     *
     * @return
     */
    public static InitParam getInitParam() {
        InitParam init;
        try {
            initParamLock.readLock().lock();
            init = initParam;
        } finally {
            initParamLock.readLock().unlock();
        }
        return init;
    }

    /**
     * 设置初始化参数
     *
     * @param initParam
     */
    public static void setInitParam(InitParam initParam) {
        try {
            initParamLock.writeLock().lock();
            InitParamInfo.initParam = initParam;
        } finally {
            initParamLock.writeLock().unlock();
        }
    }

    /**
     * 初始化采集管理器
     *
     * @throws InitException
     */
    public static void init() throws InitException {
        if (getInitParam() == null) {
            try {
                setInitParam(Yaml.loadType(new File(yamlPath + "ServerCM.yml"), InitParam.class));
            } catch (YamlException ex) {
                throw new InitException("配置文件语法错:" + "\n" + ex.getMessage());
            } catch (FileNotFoundException ex) {
                throw new InitException("启动配置文件[" + yamlPath + "ServerCM.yml]没有找到");
            }
        }
    }

    /**
     * 当前初始化参数是否有效
     *
     * @return
     */
    public static boolean isValid() {
        return getInitParam() != null;
    }
}
