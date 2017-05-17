/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.core;

/**
 * 捕获运行时异常
 *
 * @author 张岩松
 */
public class RuntimeExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println(t.getName());
        System.out.println(e.getMessage());
    }

}
