package com.dlz.comm.cache;

import com.dlz.comm.util.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 缓存工具类
 *
 * @author dk
 */
@Slf4j
public class CacheUtil {
    private static ICache cache;
    public static void init(ICache c) {
        if (c != null) {
            cache = c;
        }
    }

    /**
     * 获取缓存对象
     *
     * @return Cache
     */
    public static ICache getMemoCache() {
        return getCache("memo",null);
    }

    /**
     * 获取缓存对象
     *
     * @return Cache
     */
    public static ICache getCache(String cacheName) {
        return getCache(cacheName,cache);
    }

    /**
     * 获取缓存对象
     *
     * @return Cache
     */
    public static ICache getCache(String cacheName,ICache cache) {
        if(cache == null){
            cache = new MemoryCache();
        }
        return CacheHolder.get(cacheName,cache);
    }

    /**
     * 获取缓存
     */
    public static Serializable get(String cacheName, String key) {
        return getCache(cacheName).get(cacheName, key, null);
    }

    /**
     * 获取缓存
     */
    public static <T> T get(String cacheName, String key, Class<T> type) {
        return getCache(cacheName).get(cacheName, key, type);
    }

    /**
     * 获取缓存
     */
    public static <T> T get(String cacheName, String key, Callable<T> valueLoader) {
        try {
            return getCache(cacheName).getAndSetForever(cacheName, key, valueLoader);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }
    /**
     * 获取缓存
     */
    public static <T> List<T> getList(String cacheName, String key, Callable<List<T>> valueLoader, Class<T> type) {
        try {
            return getCache(cacheName).getAndSetListForever(cacheName, key, valueLoader,type);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * 设置缓存
     *
     * @param cacheName 缓存名
     * @param key       缓存键值
     * @param value     缓存值
     */
    public static void put(String cacheName, String key, Serializable value, int second) {
        getCache(cacheName).put(cacheName, key, value, second);
    }

    /**
     * 清除缓存
     *
     * @param cacheName 缓存名
     * @param key       缓存键值
     */
    public static void evict(String cacheName, String key) {
        getCache(cacheName).remove(cacheName, key);
    }

    /**
     * 清空缓存
     *
     * @param cacheName 缓存名
     */
    public static void clear(String cacheName) {
        getCache(cacheName).removeAll(cacheName);
    }
}
