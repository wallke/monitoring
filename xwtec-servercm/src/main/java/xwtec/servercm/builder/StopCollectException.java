/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.builder;

/**
 * 禁止采集异常
 *
 * @author 张岩松
 */
public class StopCollectException extends Exception {

    /**
     * Creates a new instance of <code>StopCollectException</code> without
     * detail message.
     */
    public StopCollectException() {
    }

    /**
     * Constructs an instance of <code>StopCollectException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public StopCollectException(String msg) {
        super(msg);
    }
}
