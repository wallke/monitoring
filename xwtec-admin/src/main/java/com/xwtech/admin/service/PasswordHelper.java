package com.xwtech.admin.service;

import com.xwtech.framework.shiro.properties.ServerProperties;
import com.xwtech.framework.shiro.model.auth.User;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
* @ClassName: PasswordHelper 
* @Description: 密码助手
* @author zhangq
* @date 2016年8月20日 下午2:04:55
*
 */
@Service
public class PasswordHelper {

    private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();

    @Autowired
    ServerProperties serverProperties;

    public void setRandomNumberGenerator(RandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
    }


    public void encryptPassword(User user) {

        if(user.getSalt() == null || "".equals(user.getSalt())){
            user.setSalt(randomNumberGenerator.nextBytes().toHex());
        }

        String newPassword = new SimpleHash(
                serverProperties.getHashAlgorithmName(),
                user.getPassword(),
                ByteSource.Util.bytes(user.getCredentialsSalt()),
                serverProperties.getHashIterations()).toHex();

        user.setPassword(newPassword);
    }

}
