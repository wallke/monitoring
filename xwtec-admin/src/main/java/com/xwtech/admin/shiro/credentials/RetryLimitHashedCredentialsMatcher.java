package com.xwtech.admin.shiro.credentials;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 登录验证重试限制
 *
 */
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {


    private final static String SHIRO_PASSWORD_RETRY_CACHE = "shiro::retry::";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Cache<String, AtomicInteger> passwordRetryCache;

    public RetryLimitHashedCredentialsMatcher(CacheManager cacheManager) {
        passwordRetryCache = cacheManager.getCache(SHIRO_PASSWORD_RETRY_CACHE);
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String username = (String)token.getPrincipal();
        //retry count + 1
        AtomicInteger retryCount = passwordRetryCache.get(username);

        if(retryCount == null) {
            retryCount = new AtomicInteger(0);
            passwordRetryCache.put(username, retryCount);
        }
        logger.debug("RetryLimit:{}",retryCount.get());
        if(retryCount.incrementAndGet() > 10) {
            //if retry count > 5 throw
            throw new ExcessiveAttemptsException();
        }

        boolean matches = super.doCredentialsMatch(token, info);
        if(matches) {
            //clear retry count
            logger.debug("RetryLimit:matches clear retry count ");
            passwordRetryCache.remove(username);
        }else{
            passwordRetryCache.put(username, retryCount);
        }
        return matches;
    }
}
