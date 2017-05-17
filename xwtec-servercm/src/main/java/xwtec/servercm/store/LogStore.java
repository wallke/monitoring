/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.store;

/**
 * 日志存储接口
 *
 * @author 张岩松
 */
public interface LogStore {

    /**
     * 将日志记录存储到文件
     *
     * @param log
     */
    public void saveToFile(String log);
}
