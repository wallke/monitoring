/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.init;

/**
 * 加载并解析采集管理器初始化配置文件出错
 *
 * @author 张岩松
 */
public class InitException extends Exception {

    /**
     * Creates a new instance of <code>InitException</code> without detail
     * message.
     */
    public InitException() {
    }

    /**
     * Constructs an instance of <code>InitException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public InitException(String msg) {
        super(msg);
    }
}
