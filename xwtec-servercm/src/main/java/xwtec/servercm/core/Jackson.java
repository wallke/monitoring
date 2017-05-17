/*
 * 南京新网互联网络科技有效公司
 */
package xwtec.servercm.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Jackson解析器
 *
 * @author 张岩松
 */
public class Jackson {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 设置日期格式
     *
     * @param dateFormat
     */
    public static void setDateFormat(String dateFormat) {
        SimpleDateFormat fmt = new SimpleDateFormat(dateFormat);
        objectMapper.setDateFormat(fmt);
    }

    /**
     * 读出Json文档到指定对象
     *
     * @param <T>
     * @param jsonString
     * @param valueType
     * @return
     * @throws xwtec.servercm.core.JsonException
     */
    public static <T extends Object> T readValue(String jsonString, Class<T> valueType) throws JsonException {
        try {
            return objectMapper.readValue(jsonString, valueType);
        } catch (IOException ex) {
            throw new JsonException("读取Json错误[Json->Object]");
        }
    }

    /**
     * 将对象转换成JSON
     *
     * @param value
     * @return
     * @throws xwtec.servercm.core.JsonException
     */
    public static String writeValueAsString(Object value) throws JsonException {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new JsonException("Json转换错误[Object->Json]");
        }
    }
}
