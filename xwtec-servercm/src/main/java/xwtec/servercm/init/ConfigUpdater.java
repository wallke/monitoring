/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.init;

import java.io.IOException;
import xwtec.servercm.core.HTTP;
import xwtec.servercm.core.Jackson;
import xwtec.servercm.core.JsonException;
import xwtec.servercm.core.Print;

/**
 * 配置信息更新者
 *
 * @author 张岩松
 */
public class ConfigUpdater {

    //本地保存的更新时间
    private static String update_time = "";

    /**
     * 更新配置信息
     *
     * @return
     * @throws ConfigException
     */
    public static boolean update() throws ConfigException {
        try {
            String configStr = getConfigInfo();
            ESLogDocument esLogDocument = Jackson.readValue(configStr, ESLogDocument.class);
            Configer.setConfigeInfo(esLogDocument.get_source());
            Configer.loadCollectCtl();
            return true;
        } catch (JsonException ex) {
            Print.exception("解析采集控制信息时,格式出错");
        }
        return false;
    }

    /**
     * 探测是否需要更新
     *
     * @return
     * @throws ConfigException
     */
    public static boolean checkUpdate() throws ConfigException {
        //开始检测是否有更新
        String newUpdateTime = getUpdateTime(InitParamInfo.getInitParam().getLog_ver());
        if (newUpdateTime.equals(update_time)) {
            return false;
        }
        update_time = newUpdateTime;
        return true;
    }

    /**
     * 将更新时间初始化
     */
    public static void init() {
        update_time = "";
    }

//Elasticsearch查询-------------------------------------------------------------/
    /**
     * 获取更新时间，若找不到，则返回曾经保存的时间
     *
     * @param log_ver
     * @return
     * @throws ConfigException
     */
    public static String getUpdateTime(String log_ver) throws ConfigException {
        String config_update_url = ":".concat(InitParamInfo.getInitParam().getEsPort()).concat("/")
                .concat(InitParamInfo.getInitParam().getEsIndex()).concat("/")
                .concat(InitParamInfo.getInitParam().getEsType()).concat("/")
                .concat(InitParamInfo.getInitParam().getEsConfigID());
        for (String esIP : InitParamInfo.getInitParam().getEsIPs()) {
            try {
                String url = "http://".concat(esIP).concat(config_update_url);
                String updateInfoStr = HTTP.getJson(url);
                ESUpdateDocument esUpdateDocument = Jackson.readValue(updateInfoStr, ESUpdateDocument.class);
                String updateTime = esUpdateDocument.get_source().getUpdateTime(log_ver);
                return updateTime == null ? update_time : updateTime;
            } catch (IOException ex) {
            } catch (JsonException ex) {
            }
        }
        Print.exception("获取采集配置信息时，无法连接Elasticsearch集群");
        throw new ConfigException();
    }

    /**
     * 获取采集配置参数内容
     *
     * @return
     * @throws ConfigException
     */
    public static String getConfigInfo() throws ConfigException {
        String config_url = ":".concat(InitParamInfo.getInitParam().getEsPort()).concat("/")
                .concat(InitParamInfo.getInitParam().getEsIndex()).concat("/")
                .concat(InitParamInfo.getInitParam().getEsType()).concat("/")
                .concat(InitParamInfo.getInitParam().getEsConfigID())
                .concat("_").concat(InitParamInfo.getInitParam().getLog_ver())
                .concat("_").concat(InitParamInfo.getInitParam().getApplication_name())
                .concat("_").concat(InitParamInfo.getInitParam().getApplication_ver());
        for (String esIP : InitParamInfo.getInitParam().getEsIPs()) {
            try {
                String url = "http://".concat(esIP).concat(config_url);
                return HTTP.getJson(url);
            } catch (IOException ex) {
            }
        }
        Print.exception("获取采集配置信息时，无法连接Elasticsearch集群");
        throw new ConfigException();
    }
}
