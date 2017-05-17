package xwtec.servercm.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by zhangq on 2017/1/19.
 */
public class Log {

    static Logger logger = LoggerFactory.getLogger("XWTEC_ServerCMLogger");

    /**
     * 将日志记录存储到文件
     *
     * @param log
     */
    public static void saveToFile(String log) {
        logger.info(log);
    }

}
