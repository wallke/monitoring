/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.builder;

/**
 * 本地节点无法找到
 *
 * @author 张岩松
 */
public class LocalNodeException extends Exception {

    /**
     * Creates a new instance of <code>LocalNodeException</code> without detail
     * message.
     */
    public LocalNodeException() {
    }

    /**
     * Constructs an instance of <code>LocalNodeException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public LocalNodeException(String msg) {
        super(msg);
    }
}
