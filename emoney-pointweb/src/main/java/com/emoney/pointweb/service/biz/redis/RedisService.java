package com.emoney.pointweb.service.biz.redis;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisService {


    private RedisTemplate redisTemplate;

    public RedisService(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        return operations.get(key);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public <T> T get(String key, Class<T> clazz) {
        Object result;
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        if (result == null) {
            return null;
        }
        if (result instanceof JSONObject) {
            return JSON.parseObject(result.toString(), clazz);
        } else {
            return (T) result;
        }
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public <T> List<T> getList(String key, Class<T> clazz) {
        Object result;
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        if (result == null) {
            return null;
        }

        if (result instanceof JSONArray) {
            return JSON.parseArray(result.toString(), clazz);
        } else {
            return (List<T>) result;
        }
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            log.error("Error occurs with:" + ExceptionUtils.getStackTrace(e));
        }
        return result;
    }

    /**
     * 写入缓存设置时效时间
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(String key, Object value, Long expireTime, TimeUnit timeUnit) {
        boolean result = false;
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, timeUnit);
            result = true;
        } catch (Exception e) {
            log.error("Error occurs with:" + ExceptionUtils.getStackTrace(e));
        }
        return result;
    }

    /**
     * @param key
     * @param value
     * @param expireTime 默认为秒
     * @return
     */
    public boolean set(String key, Object value, Long expireTime) {
        return set(key, value, expireTime, TimeUnit.SECONDS);

    }

    /**
     * 删除对应的value
     *
     * @param key
     */
    public void remove(String key) {
        if (exists(key)) {
            log.info("===redis remove {}", key);
            redisTemplate.delete(key);
        }
    }

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public void remove(String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除key
     *
     * @param pattern
     */
    public void removePattern(String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 哈希获取数据
     *
     * @param key
     * @param hashKey
     * @return
     */
    public Object hashGet(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.get(key, hashKey);
    }

    public <T> List<T> hashGetList(String key, Object hashKey, Class<T> clazz) {
        Object result;
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        result =  hash.get(key, hashKey);
        if (result == null) {
            return null;
        }

        if (result instanceof JSONArray) {
            return JSON.parseArray(result.toString(), clazz);
        } else {
            return (List<T>) result;
        }
    }

    /**
     * 哈希 添加
     *
     * @param key
     * @param hashKey
     * @param value
     */
    public void hashSet(String key, Object hashKey, Object value) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.put(key, hashKey, value);
    }

    /**
     * 列表获取
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<Object> listGet(String key, long start, long end) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.range(key, start, end);
    }

    /**
     * 列表添加
     *
     * @param key
     * @param value
     */
    public void listAdd(String key, Object value) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.rightPush(key, value);
    }

    /**
     * 集合获取
     *
     * @param key
     * @return
     */
    public Set<Object> setGet(String key) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        return set.members(key);
    }

    /**
     * 集合添加
     *
     * @param key
     * @param value
     */
    public void setAdd(String key, Object value) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add(key, value);
    }

    /**
     * 有序集合获取
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<Object> zGet(String key, double min, double max) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        return zset.rangeByScore(key, min, max);
    }

    /**
     * 有序集合添加
     *
     * @param key
     * @param value
     * @param scoure
     */
    public void zAdd(String key, Object value, double scoure) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        zset.add(key, value, scoure);
    }


}