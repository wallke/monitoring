/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.builder;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 服务接口日志建造者
 *
 * @author 张岩松
 */
public class RequestLogBuilder extends LogBuilder {

//埋点采集时获得-----------------------------------------------------------------/
    //调用模式
    private String mode = "";
    //客户端类型
    private String client_type = "";
    //客户端IP
    private String client_ip = "";
    //客户端端口号
    private String client_port = "";
    //终端信息
    private List<String> client_infos;
    //自定义属性
    private Map<String, String> custm_propertys;

    @Override
    protected String build(String separator_one, String separator_two) {
        String log = this.mode;
        log = log.concat(separator_one).concat(this.client_type);
        log = log.concat(separator_one).concat(this.client_ip);
        log = log.concat(separator_one).concat(this.client_port);
        log = log.concat(separator_one).concat(this.build_client_info(separator_two));
        log = log.concat(separator_one).concat(this.build_custm_property(separator_two));
        return log;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setClient_type(String client_type) {
        this.client_type = client_type;
    }

    public void setClient_ip(String client_ip) {
        this.client_ip = client_ip;
    }

    public void setClient_port(String client_port) {
        this.client_port = client_port;
    }

    public void setClient_infos(List<String> client_infos) {
        this.client_infos = client_infos;
    }

    public void setCustm_propertys(Map<String, String> custm_propertys) {
        this.custm_propertys = custm_propertys;
    }

    /**
     * 建造客户端信息
     *
     * @return
     */
    private String build_client_info(String separator_two) {
        if (this.client_infos != null) {
            return this.concatList(this.client_infos, separator_two);
        } else {
            return "";
        }
    }

    /**
     * 建造自定义属性
     *
     * @return
     */
    private String build_custm_property(String separator_two) {
        if (this.custm_propertys != null) {
            return this.concatSet(this.custm_propertys.entrySet(), separator_two);
        } else {
            return "";
        }
    }

    /**
     * 以指定自负分隔,连接字符串
     *
     * @param texts
     * @param splitChar
     * @return
     */
    private String concatList(List<String> texts, String splitChar) {
        String text = "";
        for (String string : texts) {
            if (!string.isEmpty()) {
                if (text.isEmpty()) {
                    text = string;
                } else {
                    text = text.concat(splitChar).concat(string);
                }
            }
        }
        return text;
    }

    /**
     * 以指定自负分隔,连接字符串
     *
     * @param texts
     * @param splitChar
     * @return
     */
    private String concatSet(Set<Map.Entry<String, String>> texts, String splitChar) {
        String text = "";
        for (Map.Entry<String, String> keyvalue : texts) {
            if (!keyvalue.getKey().isEmpty()) {
                if (text.isEmpty()) {
                    text = keyvalue.getKey().concat("=").concat(keyvalue.getValue());
                } else {
                    text = text.concat(splitChar).concat(keyvalue.getKey().concat("=").concat(keyvalue.getValue()));
                }
            }
        }
        return text;
    }

    public String getMode() {
        return mode;
    }
    
}
