package com.dlz.comm.cache;

import com.dlz.comm.util.JacksonUtil;
import com.dlz.comm.util.VAL;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * 缓存接口
 * 
 * 定义缓存的基本操作，包括获取、设置、删除等操作
 * 
 * @author dingkui
 * @since 2023
 */
public interface ICache {
    /**
     * 获取缓存值
     * 
     * @param name 缓存名称
     * @param key 缓存键
     * @param tClass 缓存值类型
     * @param <T> 缓存值类型泛型
     * @return 缓存值
     */
    <T extends Serializable> T get(String name, Serializable key, Type tClass);

    /**
     * 获取缓存值，如果不存在则加载并设置
     * 
     * @param name 缓存名称
     * @param key 缓存键
     * @param valueLoader 值加载器
     * @param <T> 缓存值类型泛型
     * @return 缓存值
     */
    default <T> T getAndSet(String name, Serializable key, Callable<VAL<T, Integer>> valueLoader) {
        try {
            T re = get(name, key, null);
            if (re == null && valueLoader != null) {
                VAL<T, Integer> v = valueLoader.call();
                if (v != null && v.v1 != null) {
                    re = v.v1;
                    put(name, key, (Serializable) re, v.v2);
                }
            }
            return re;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取缓存列表，如果不存在则加载并设置
     * 
     * @param name 缓存名称
     * @param key 缓存键
     * @param valueLoader 值加载器
     * @param tClass 列表元素类型
     * @param <T> 列表元素类型泛型
     * @return 缓存列表
     */
    default <T> List<T> getAndSetList(String name, Serializable key, Callable<VAL<List<T>, Integer>> valueLoader, Class<T> tClass) {
        try {
            List<T> re = get(name, key, JacksonUtil.mkJavaType(List.class, tClass));
            if (re == null && valueLoader != null) {
                VAL<List<T>, Integer> v = valueLoader.call();
                if (v != null && v.v1 != null) {
                    re = v.v1;
                    put(name, key, (Serializable) re, v.v2);
                }
            }
            return re;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取缓存列表，永久有效
     * 
     * @param name 缓存名称
     * @param key 缓存键
     * @param valueLoader 值加载器
     * @param tClass 列表元素类型
     * @param <T> 列表元素类型泛型
     * @return 缓存列表
     */
    default <T> List<T> getAndSetListForever(String name, Serializable key, Callable<List<T>> valueLoader, Class<T> tClass) {
        try {
            return getAndSetList(name, key, () -> VAL.of(valueLoader.call(), -1), tClass);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取缓存值，永久有效
     * 
     * @param name 缓存名称
     * @param key 缓存键
     * @param valueLoader 值加载器
     * @param <T> 缓存值类型泛型
     * @return 缓存值
     */
    default <T> T getAndSetForever(String name, Serializable key, Callable<T> valueLoader) {
        try {
            return getAndSet(name, key, () -> VAL.of(valueLoader.call(), -1));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取缓存值
     * 
     * @param name 缓存名称
     * @param key 缓存键
     * @param <T> 缓存值类型泛型
     * @return 缓存值
     */
    default <T extends Serializable> T get(String name, Serializable key) {
        return get(name, key, null);
    }

    /**
     * 设置缓存值
     * 
     * @param name 缓存名称
     * @param key 缓存键
     * @param value 缓存值
     */
    default void put(String name, Serializable key, Serializable value) {
        put(name, key, value, -1);
    }

    /**
     * 设置缓存值
     * 
     * @param name 缓存名称
     * @param key 缓存键
     * @param value 缓存值
     * @param seconds 过期时间（秒），-1表示永不过期
     */
    void put(String name, Serializable key, Serializable value, int seconds);

    /**
     * 删除缓存值
     * 
     * @param name 缓存名称
     * @param key 缓存键
     */
    void remove(String name, Serializable key);

    /**
     * 删除所有缓存
     * 
     * @param name 缓存名称
     */
    void removeAll(String name);

    /**
     * 获取缓存键集合
     * 
     * @param name 缓存名称
     * @return 缓存键集合
     */
    default Set<String> keys(String name) {
        return keys(name, "*");
    }

    /**
     * 获取缓存键集合
     * 
     * @param name 缓存名称
     * @param keyPrefix 键前缀
     * @return 缓存键集合
     */
    default Set<String> keys(String name, String keyPrefix) {
        return Collections.emptySet();
    }

    /**
     * 获取所有缓存
     * 
     * @param name 缓存名称
     * @return 缓存映射
     */
    default Map<String, Serializable> all(String name) {
        return all(name, "*");
    }

    /**
     * 获取所有缓存
     * 
     * @param name 缓存名称
     * @param keyPrefix 键前缀
     * @return 缓存映射
     */
    default Map<String, Serializable> all(String name, String keyPrefix) {
        Map<String, Serializable> map = new HashMap<>();
        Set<String> keys = keys(name, keyPrefix);
        keys.forEach(key -> map.put(key, get(name, key)));
        return map;
    }
}