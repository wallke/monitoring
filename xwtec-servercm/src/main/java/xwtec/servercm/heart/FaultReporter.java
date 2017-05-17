/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.heart;

import java.io.IOException;
import java.util.Date;
import xwtec.servercm.core.HTTP;
import xwtec.servercm.core.Jackson;
import xwtec.servercm.core.JsonException;
import xwtec.servercm.core.Print;
import xwtec.servercm.init.InitParamInfo;
import xwtec.servercm.collect.ServerInfo;
import xwtec.servercm.status.RunStatus;

/**
 * 报活管理器
 *
 * @author 张岩松
 */
public class FaultReporter {

    private final static FaultReporter faultReporter = new FaultReporter();
    private final HealthInfo healthInfo = new HealthInfo();

    public static FaultReporter instance() {
        return faultReporter;
    }

    /**
     * 报活
     */
    public static void report() {
        faultReporter.reportCommit();
    }

    /**
     * 提交健康信息
     */
    public void reportCommit() {
        String report_url = ":".concat(InitParamInfo.getInitParam().getEsPort()).concat("/")
                .concat(InitParamInfo.getInitParam().getEsIndex()).concat("/")
                .concat("health/").concat(ServerInfo.getServer_ip()).concat("_").concat(ServerInfo.getServer_port());
        for (String esIP : InitParamInfo.getInitParam().getEsIPs()) {
            try {
                String url = "http://".concat(esIP).concat(report_url);
                HTTP.postJson(url, this.getHealthInfoJson(), false);
                return;
            } catch (IOException ex) {
            } catch (JsonException ex) {
                System.out.println(ex);
            }
        }
        Print.exception("获取采集配置信息时，无法连接Elasticsearch集群");
    }

    /**
     * 得到Json结构的健康度请求信息
     *
     * @return
     */
    private String getHealthInfoJson() throws JsonException {
        this.setCm_status();
        Jackson.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        this.healthInfo.setTime(new Date());
        return Jackson.writeValueAsString(this.healthInfo);
    }

    public void setAppServer_name(String appServer_name) {
        this.healthInfo.setAppServer_name(appServer_name);
    }

    public void setAppServer_ver(String appServer_ver) {
        this.healthInfo.setAppServer_ver(appServer_ver);
    }

    public void setServer_ip(String server_ip) {
        this.healthInfo.setServer_ip(server_ip);
    }

    public void setServer_port(String server_port) {
        this.healthInfo.setServer_port(server_port);
    }

    /**
     * 设置采集状态
     */
    private void setCm_status() {
        if (RunStatus.isDisable()) {
            this.healthInfo.setCm_status("停止");
        } else if (RunStatus.isActive()) {
            this.healthInfo.setCm_status("激活");
        } else if (RunStatus.isSleep()) {
            this.healthInfo.setCm_status("正在更新配置信息");
        }
    }

    /**
     * 健康度信息
     */
    private class HealthInfo {

        private String appServer_name = "";
        private String appServer_ver = "";
        private Date time;
        private String server_ip = "";
        private String server_port = "";
        private String cm_status = "";

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

        public Date getTime() {
            return time;
        }

        public void setTime(Date time) {
            this.time = time;
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

        public String getCm_status() {
            return cm_status;
        }

        public void setCm_status(String cm_status) {
            this.cm_status = cm_status;
        }
    }

}
