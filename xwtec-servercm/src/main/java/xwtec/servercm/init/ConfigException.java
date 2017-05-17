/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.init;

/**
 * 加载配置信息时异常
 *
 * @author 张岩松
 */
public class ConfigException extends Exception {

    /**
     * Creates a new instance of <code>ConfigException</code> without detail
     * message.
     */
    public ConfigException() {
    }

    /**
     * Constructs an instance of <code>ConfigException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public ConfigException(String msg) {
        super(msg);
    }
}
