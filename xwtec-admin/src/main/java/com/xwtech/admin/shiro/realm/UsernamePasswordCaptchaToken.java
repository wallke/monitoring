package com.xwtech.admin.shiro.realm;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * Created by zq on 16/8/22.
 */
public class UsernamePasswordCaptchaToken extends UsernamePasswordToken {

    private static final long serialVersionUID = 1L;

    private String captcha;

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public UsernamePasswordCaptchaToken() {
        super();

    }

    public UsernamePasswordCaptchaToken(String username, String password, String captcha) {
        super(username, password);
        this.captcha = captcha;
    }



    public UsernamePasswordCaptchaToken(String username, String password,
                                        boolean rememberMe, String host, String captcha) {
        super(username, password, rememberMe, host);
        this.captcha = captcha;
    }



    public UsernamePasswordCaptchaToken(String username, String password,
                                        boolean rememberMe, String captcha) {
        super(username, password, rememberMe);
        this.captcha = captcha;
    }


}