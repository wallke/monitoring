/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.init;

/**
 * 采集管理器出现状态异常，没有激活
 *
 * @author 张岩松
 */
public class StatusException extends Exception {

    /**
     * Creates a new instance of <code>StatusException</code> without detail
     * message.
     */
    public StatusException() {
    }

    /**
     * Constructs an instance of <code>StatusException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public StatusException(String msg) {
        super(msg);
    }
}
