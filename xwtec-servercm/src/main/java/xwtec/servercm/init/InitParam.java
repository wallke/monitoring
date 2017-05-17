/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.init;

/**
 * 初始化参数
 *
 * @author 张岩松
 */
public class InitParam {

    private String log_ver;
    private String application_name;
    private String application_ver;
    private String[] esIPs;
    private String esPort;
    private String esIndex;
    private String esType;
    private String esConfigID;

    public String getLog_ver() {
        return log_ver;
    }

    public void setLog_ver(String log_ver) {
        this.log_ver = log_ver;
    }

    public String getApplication_name() {
        return application_name;
    }

    public void setApplication_name(String application_name) {
        this.application_name = application_name;
    }

    public String getApplication_ver() {
        return application_ver;
    }

    public void setApplication_ver(String application_ver) {
        this.application_ver = application_ver;
    }

    public String[] getEsIPs() {
        return esIPs;
    }

    public void setEsIPs(String[] esIPs) {
        this.esIPs = esIPs;
    }

    public String getEsPort() {
        return esPort;
    }

    public void setEsPort(String esPort) {
        this.esPort = esPort;
    }

    public String getEsIndex() {
        return esIndex;
    }

    public void setEsIndex(String esIndex) {
        this.esIndex = esIndex;
    }

    public String getEsType() {
        return esType;
    }

    public void setEsType(String esType) {
        this.esType = esType;
    }

    public String getEsConfigID() {
        return esConfigID;
    }

    public void setEsConfigID(String esConfigID) {
        this.esConfigID = esConfigID;
    }

}
