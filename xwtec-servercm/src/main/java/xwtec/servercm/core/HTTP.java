/*
 * Http请求
 */
package xwtec.servercm.core;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author 张岩松
 */
public class HTTP {

    //上传内容类型
    public static final String contentType = "application/x-www-form-urlencoded";

    /**
     * 通过JSON格式POST方式提交
     *
     * @param requestURL
     * @param requestString
     * @param isGzip
     * @return
     * @throws IOException
     */
    public static String postJson(String requestURL, String requestString, boolean isGzip) throws IOException {
        return httpPost(requestURL, requestString, "application/json", isGzip);
    }

    public static String getJson(String requestURL) throws IOException {
        return httpGet(requestURL, "application/json");
    }

    /**
     * 通过POST方式提交
     *
     * @param requestURL 请求地址
     * @param requestString 提交的内容
     * @param contentType 内容类型
     * @param isGzip 是否采用GZIP压缩
     * @return
     * @throws IOException
     */
    public static String httpPost(String requestURL, String requestString, String contentType, boolean isGzip) throws IOException {
        //创建连接
        URL url = new URL(requestURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Content-Type", contentType);
        if (isGzip) {
            connection.setRequestProperty("Content-Encoding", "gzip");
        }
        connection.connect();
        //POST请求
        OutputStream outputStream = connection.getOutputStream();
        if (isGzip) {
            outputStream.write(HTTP.gzip(requestString));
        } else {
            outputStream.write(requestString.getBytes("utf-8"));
        }
        outputStream.flush();
        outputStream.close();
        //读取响应
        StringBuffer resultText = new StringBuffer("");
        int responseCode = connection.getResponseCode();
        if (HttpURLConnection.HTTP_OK == responseCode) {
            String lines;
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                resultText.append(lines);
            }
            reader.close();
        }
        // 断开连接
        connection.disconnect();
        return resultText.toString();
    }

    /**
     * 通过GET方式提交
     *
     * @param requestURL
     * @param contentType
     * @return
     * @throws IOException
     */
    public static String httpGet(String requestURL, String contentType) throws IOException {
        //创建连接
        URL url = new URL(requestURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setRequestMethod("GET");
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Content-Type", contentType);
        connection.connect();
        //读取响应
        StringBuffer resultText = new StringBuffer("");
        String lines;
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while ((lines = reader.readLine()) != null) {
            lines = new String(lines.getBytes(), "utf-8");
            resultText.append(lines);
        }
        reader.close();
        // 断开连接
        connection.disconnect();
        return resultText.toString();
    }

    /**
     * 用gzip压缩
     *
     * @param text
     * @return
     */
    public static byte[] gzip(String text) {
        if ((text == null) || (text.isEmpty())) {
            return null;
        }
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(outStream);
            gzip.write(text.getBytes("utf-8"));
        } catch (IOException ex) {
        } finally {
            if (gzip != null) {
                try {
                    gzip.close();
                } catch (IOException ex) {
                }
            }
        }
        return outStream.toByteArray();
    }

    /**
     * 解压gZip字符串
     *
     * @param gzipText
     * @return
     */
    public static String gunzip(byte[] gzipText) {
        GZIPInputStream ginzip = null;
        ByteArrayOutputStream outStream = null;
        ByteArrayInputStream inStream = null;
        String gunzipText = null;
        try {
            if (gzipText == null) {
                return null;
            }
            outStream = new ByteArrayOutputStream();
            inStream = new ByteArrayInputStream(gzipText);
            ginzip = new GZIPInputStream(inStream);
            byte[] buffer = new byte[1024];
            int offset;
            while ((offset = ginzip.read(buffer)) != -1) {
                outStream.write(buffer, 0, offset);
            }
            gunzipText = new String(outStream.toByteArray(), "utf-8");
        } catch (IOException ex) {
        } finally {
            if (ginzip != null) {
                try {
                    ginzip.close();
                } catch (IOException ex) {
                }
            }
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException ex) {
                }
            }
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException ex) {
                }
            }
        }
        return gunzipText;
    }

}
