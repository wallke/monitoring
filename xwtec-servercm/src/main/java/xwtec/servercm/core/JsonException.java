/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.core;

/**
 * Json解析异常
 *
 * @author 张岩松
 */
public class JsonException extends Exception {

    /**
     * Creates a new instance of <code>JsonException</code> without detail
     * message.
     */
    public JsonException() {
    }

    /**
     * Constructs an instance of <code>JsonException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public JsonException(String msg) {
        super(msg);
    }
}
