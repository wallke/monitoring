/*
 * 随机ID生成器
 */
package xwtec.servercm.core;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author 张岩松
 */
public class IDCreater {

    /**
     * 生成UUID类型的节点ID
     *
     * @return
     */
    public static String createNodeID() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * ID生成器
     *
     * @param prefix
     * @return
     */
    public static String createID(String prefix) {
        Date createDate = new Date();
        String rootRandom = String.valueOf((int) (Math.random() * 100));
        rootRandom = "00".substring(0, 2 - rootRandom.length()).concat(rootRandom);
        String mainRandom = String.valueOf((int) (Math.random() * 1000));
        mainRandom = "000".substring(0, 3 - mainRandom.length()).concat(mainRandom);
        String date = new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date());
        return prefix.concat(date.substring(2, 8)).concat(date.substring(9, 15)).concat(rootRandom).concat(mainRandom);
    }

    /**
     * 创建简单ID
     *
     * @param prefix
     * @return
     */
    public static String createSimpleID(String prefix) {
        String random = String.valueOf((int) (Math.random() * 100000000));
        random = "00000000".substring(0, 8 - random.length()).concat(random);
        return prefix.concat(random);
    }

}
