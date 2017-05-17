package com.xwtech.hm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jason on 2017/1/12.
 */
public class Application {

    private static Logger LOGGER = LoggerFactory.getLogger(Application.class);



//    主机:10.19.241.47
//    SMTP 端口:7025
//    帐号:14706407549@139.com
//    密码：sd@xwtec'

    public static void main(String[] args) {
        LOGGER.info("Test");

        EmailNoticeTask emailNoticeTask = new EmailNoticeTask();
        emailNoticeTask.setEmailAccount("14706407549@139.com");
        emailNoticeTask.setEmailPassword("sd@xwtec");
        emailNoticeTask.setEmailSMTPHost("10.19.241.47");
        emailNoticeTask.setEmailSMTPPort("7025");
        emailNoticeTask.setAddress("6128806@qq.com");
        emailNoticeTask.setReceiver("6128806@qq.com");
        emailNoticeTask.noticeMessage("${receiver}dasjhdskdjaj张强111");


    }

}
