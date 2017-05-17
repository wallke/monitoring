package com.xwtech.hm;


        import java.util.Date;
        import java.util.Properties;
        import javax.mail.Session;
        import javax.mail.Transport;
        import javax.mail.internet.InternetAddress;
        import javax.mail.internet.MimeMessage;

/**
 * 电子邮件通知任务
 *
 * @author 张岩松,仲学野
 */
public class EmailNoticeTask {

    //发件邮箱
    private String emailAccount;
    private String emailPassword;
    private String emailSMTPHost;
    private String emailSMTPPort;
    //收件人
    private String receiver;
    //电子邮件地址
    private String address;

    public void setEmailAccount(String emailAccount) {
        this.emailAccount = emailAccount;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    public void setEmailSMTPHost(String emailSMTPHost) {
        this.emailSMTPHost = emailSMTPHost;
    }

    public void setEmailSMTPPort(String emailSMTPPort) {
        this.emailSMTPPort = emailSMTPPort;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void noticeMessage(String message) {
        String channelMessage = message.replace("${receiver}", this.receiver);
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", this.emailSMTPHost);
        props.setProperty("mail.smtp.auth", "true");
        // PS: 某些邮箱服务器要求 SMTP 连接需要使用 SSL 安全认证 (为了提高安全性, 邮箱支持SSL连接, 也可以自己开启),
        //     如果无法连接邮件服务器, 仔细查看控制台打印的 log, 如果有有类似 “连接失败, 要求 SSL 安全连接” 等错误,
        // SMTP 服务器的端口 (非 SSL 连接的端口一般默认为 25, 可以不添加, 如果开启了 SSL 连接,
        //                  需要改为对应邮箱的 SMTP 服务器的端口, 具体可查看对应邮箱服务的帮助,
        //                  QQ邮箱的SMTP(SLL)端口为465或587, 其他邮箱自行去查看)
        props.setProperty("mail.smtp.port", this.emailSMTPPort);
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "true");
        props.setProperty("mail.smtp.socketFactory.port", this.emailSMTPPort);
        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getDefaultInstance(props);
        // 设置为debug模式, 可以查看详细的发送 log
        session.setDebug(true);
        try {
            // 3. 创建一封邮件
            MimeMessage mimeMessage = createMimeMessage(session, this.emailAccount, this.address, channelMessage);
            // 4. 根据 Session 获取邮件传输对象
            Transport transport = session.getTransport();
            transport.connect(this.emailAccount, this.emailPassword);
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            // 7. 关闭连接
            transport.close();
        } catch (Exception e) {
            System.out.print(e);
        }
    }

    /**
     * 创建一封只包含文本的简单邮件
     *
     * @param session 和服务器交互的会话
     * @param sendMail 发件人邮箱
     * @param receiveMail 收件人邮箱
     * @param messageTemplate
     * @return
     * @throws Exception
     */
    public MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail, String messageTemplate) throws Exception {
        MimeMessage message = new MimeMessage(session);
        // 2. From: 发件人
        message.setFrom(new InternetAddress(sendMail, "运维系统", "UTF-8"));
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, this.receiver, "UTF-8"));
        // 4. Subject: 邮件主题
        message.setSubject("告警信息", "UTF-8");
        // 5. Content: 邮件正文（可以使用html标签）
        message.setContent(messageTemplate, "text/html;charset=UTF-8");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

}
