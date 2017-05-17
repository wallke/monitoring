package com.xwtech.admin.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;

/**
 * Created by zq on 16/8/15.
 */
public class RedisCache  <K, V> implements Cache<K, V> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * The wrapped Jedis instance.
     */

    private RedisTemplate redisTemplate;


    /**
     * The Redis key prefix for the sessions
     */
    private String keyPrefix = "shiro::";

    /**
     * Returns the Redis session keys
     * prefix.
     * @return The prefix
     */
    public String getKeyPrefix() {
        return keyPrefix;
    }

    /**
     * Sets the Redis sessions key
     * prefix.
     * @param keyPrefix The prefix
     */
    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }


    /**
     * Constructs a cache instance with the specified
     * Redis manager and using a custom key prefix.
     * @param prefix The Redis key prefix
     */
    public RedisCache(RedisTemplate redisTemplate,String prefix){
        // set the prefix
        this.redisTemplate = redisTemplate;
        this.keyPrefix = prefix + ":";
    }



    @Override
    public V get(K key) throws CacheException {
        if(key == null) {
            return null;
        }
        return (V) redisTemplate.opsForValue().get(getKey(key));

    }

    @Override
    public V put(K key, V value) throws CacheException {
        try {
            //redisTemplate.opsForValue().set(getKey(key), value,this.timeout, TimeUnit.MILLISECONDS);
            redisTemplate.opsForValue().set(getKey(key), value);
            return value;
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @Override
    public V remove(K key) throws CacheException {
        try {
            V previous = get(key);
            redisTemplate.delete(getKey(key));
            return previous;
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @Override
    public void clear() throws CacheException {
        try {
            redisTemplate.delete(redisTemplate.keys(getKey((K)"*")));
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @Override
    public int size() {
        try {
            return redisTemplate.keys(getKey((K)"*")).size();
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<K> keys() {
        try {
            Set keys = redisTemplate.keys(getKey((K)"*"));
            if (CollectionUtils.isEmpty(keys)) {
                return Collections.emptySet();
            }else{
                Set<K> newKeys = new HashSet<K>();
                for(Object key:keys){
                    newKeys.add((K)key);
                }
                return newKeys;
            }
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @Override
    public Collection<V> values() {
        try {
            Set<String> keys = redisTemplate.keys(getKey((K) "*"));
            if (!CollectionUtils.isEmpty(keys)) {
                List<V> values = new ArrayList<V>(keys.size());
                for (String key : keys) {
                    @SuppressWarnings("unchecked")
                    V value = (V) redisTemplate.opsForValue().get(key);
                    if (value != null) {
                        values.add(value);
                    }
                }
                return Collections.unmodifiableList(values);
            } else {
                return Collections.emptyList();
            }
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }



    private String getKey(K key){
        return this.keyPrefix + key;
    }




}
