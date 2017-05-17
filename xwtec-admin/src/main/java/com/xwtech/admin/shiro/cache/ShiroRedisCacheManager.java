package com.xwtech.admin.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by zq on 16/8/15.
 */
public class ShiroRedisCacheManager implements CacheManager {

    private static final Logger logger = LoggerFactory
            .getLogger(ShiroRedisCacheManager.class);


    @Autowired
    private RedisTemplate redisTemplate;


    // fast lookup by name map
    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<String, Cache>();


    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        logger.debug("获取名称为: " + name + " 的RedisCache实例");

        Cache c = caches.get(name);
        if (c == null) {
            // create a new cache instance
            c = new RedisCache<K, V>(redisTemplate,name);
            // add it to the cache collection
            Cache existing =   caches.putIfAbsent(name, c);
            if (existing != null) {
                c = existing;
            }

        }
        return c;
    }


}