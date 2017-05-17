/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.core;

/**
 * 打印输出信息
 *
 * @author 张岩松
 */
public class Print {

    private static final String exception_perfix = "wxtec.cm.Exception:";
    private static final String info_perfix = "wxtec.cm.info:";

    /**
     * 打印异常信息
     *
     * @param message
     */
    public static void exception(String message) {
        if (message != null) {
            System.out.println(exception_perfix.concat(message));
        }
    }

    /**
     * 打印信息
     *
     * @param message
     */
    public static void message(String message) {
        if (message != null) {
            System.out.println(exception_perfix.concat(message));
        }
    }

}
