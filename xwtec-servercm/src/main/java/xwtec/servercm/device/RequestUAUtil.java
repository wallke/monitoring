package xwtec.servercm.device;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangq on 2017/1/24.
 */
public class RequestUAUtil {


    public static void main(String[] a){
        String userAgent= "Mozilla/5.0 (Linux; U; Android 3.0; en-us; Xoom Build/HRI39) \n" +
                "AppleWebKit/534.13 (KHTML, like Gecko) Version/4.0 Safari/534.13";//request.getHeader("user-agent");
        DeviceInfo info= RequestUAUtil.getMobilOS(userAgent);
        System.out.println("device type:\t"+info.getDeviceType());
        System.out.println("type version:\t"+info.getOsType());
        System.out.println("version:\t"+info.getVersion());
        System.out.println("是移动设备吗:"+info.isMobile());
    }


    /**
     * 判断手机的操作系统 IOS/android/windows phone/BlackBerry
     *
     * @param UA
     * @return
     */
    public static DeviceInfo getMobilOS(String UA) {
        UA=UA.toUpperCase();
        if (UA == null) {
            return null;
        }
        DeviceInfo deviceInfo = new  DeviceInfo();
        // 存放正则表达式
        String rex = "";
        // IOS 判断字符串
        String iosString = " LIKE MAC OS X";
        if (UA.indexOf(iosString) != -1) {
            if(isMatch(UA, "\\([\\s]*iPhone[\\s]*;", Pattern.CASE_INSENSITIVE)){
                deviceInfo.setDeviceType("Phone");
            }else if(isMatch(UA, "\\([\\s]*iPad[\\s]*;", Pattern.CASE_INSENSITIVE)){
                deviceInfo.setDeviceType("Pad");
            }
            rex = ".*" + "[\\s]+(\\d[_\\d]*)" + iosString;
            Pattern p = Pattern.compile(rex, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(UA);
            boolean rs = m.find();
            if (rs) {
                String osVersion= m.group(1).replace("_", ".");
                deviceInfo.setVersion(osVersion);
                deviceInfo.setOsType("IOS");
                return deviceInfo;
            }
            deviceInfo.setOsType("IOS");
            return deviceInfo;
        }
        // Android 判断
        String androidString = "ANDROID";
        if (UA.indexOf(androidString) != -1) {
            if(isMatch(UA, "\\bMobi", Pattern.CASE_INSENSITIVE)){
                deviceInfo.setDeviceType("Phone");
            }else {
                deviceInfo.setDeviceType("Pad");
            }
            rex = ".*" + androidString + "[\\s]*(\\d*[\\._\\d]*)";
            Pattern p = Pattern.compile(rex, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(UA);
            boolean rs = m.find();
            if (rs) {
                String version=m.group(1).replace("_", ".");
                deviceInfo.setVersion(version);
                System.out.println("Mobil OS is " + "Android" + version);
                deviceInfo.setOsType("Android");
                return deviceInfo;
            }
            System.out.println("Android");
            deviceInfo.setOsType("Android");
            return deviceInfo;
        }
        // windows phone 判断
        String wpString = "WINDOWS PHONE";
        if (UA.indexOf(wpString) != -1) {
            rex = ".*" + wpString + "[\\s]*[OS\\s]*([\\d][\\.\\d]*)";
            Pattern p = Pattern.compile(rex, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(UA);
            boolean rs = m.find();
            if (rs) {
                String version=m.group(1);
                deviceInfo.setVersion(version);
                deviceInfo.setOsType("WINDOWS PHONE");
                return deviceInfo;
            }
            deviceInfo.setOsType("WINDOWS PHONE");
            return deviceInfo;
        }
        // BlackBerry 黑莓系统判断
        String bbString = "BLACKBERRY";
        if (UA.indexOf(bbString) != -1) {
            rex = ".*" + bbString + "[\\s]*([\\d]*)";
            Pattern p = Pattern.compile(rex, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(UA);
            boolean rs = m.find();
            if (rs) {
                System.out.println("Mobil OS is" + " BLACKBERRY " + m.group(1));
                String version=m.group(1);
                deviceInfo.setVersion(version);
                deviceInfo.setOsType("BLACKBERRY");
                return deviceInfo;
            }
            System.out.println("BLACKBERRY");
            deviceInfo.setOsType("BLACKBERRY");
            return deviceInfo;
        }
        if(UA.contains("LINUX")){//android
            if(isMatch(UA, "\\bMobi", Pattern.CASE_INSENSITIVE)){
                deviceInfo.setDeviceType("Phone");
            }else {
                deviceInfo.setDeviceType("Pad");
            }

            Pattern p = Pattern.compile("U;\\s*(Adr[\\s]*)?(\\d[\\.\\d]*\\d)[\\s]*;",Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(UA);
            boolean result = m.find();
            String find_result = null;
            if (result)
            {
                find_result = m.group(2);
            }
            deviceInfo.setVersion(find_result);
            deviceInfo.setOsType("Android");
            return deviceInfo;
        }

        //UCWEB/2.0 (iOS; U; iPh OS 4_3_2; zh-CN; iPh4)
        if(UA.matches(".*((IOS)|(iPAD)).*(IPH).*")){
            if(isMatch(UA, "[\\s]*iPh[\\s]*", Pattern.CASE_INSENSITIVE)){
                deviceInfo.setDeviceType("Phone");
            }else {
                deviceInfo.setDeviceType("Pad");
            }
            Pattern p = Pattern.compile("U;\\s*(IPH[\\s]*)?(OS[\\s]*)?(\\d[\\._\\d]*\\d)[\\s]*;",Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(UA);
            boolean result = m.find();
            String find_result = null;
            if (result)
            {
                find_result = m.group(3);
            }
            deviceInfo.setVersion(find_result);
            deviceInfo.setOsType("IOS");
            return deviceInfo;
        }
        return deviceInfo;
    }

    private static boolean isMatch(String ua, String s, int caseInsensitive) {
        return Pattern.compile(s,caseInsensitive).matcher(ua).find();
    }
}
