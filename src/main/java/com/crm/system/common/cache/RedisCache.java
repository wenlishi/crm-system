package com.crm.system.common.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存工具类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Component
public class RedisCache {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 从缓存获取数据
     */
    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value != null && clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        return null;
    }

    /**
     * 设置缓存（默认 30 分钟）
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value, 30, TimeUnit.MINUTES);
    }

    /**
     * 设置缓存（自定义过期时间）
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 删除缓存
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 判断缓存是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 获取缓存剩余时间（秒）
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
}
