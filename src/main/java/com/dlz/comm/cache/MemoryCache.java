package com.dlz.comm.cache;

import com.dlz.comm.util.JacksonUtil;
import com.dlz.comm.util.ValUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 内存缓存实现类
 * 
 * 使用内存实现缓存功能，支持设置过期时间、获取、设置、删除等操作
 * 
 * @author dk
 * @since 2023
 */
@Slf4j
public class MemoryCache implements ICache {
    /**
     * 缓存存储结构：外层Map的key为缓存名称，内层Map的key为缓存键，value为缓存元素
     */
    private final static Map<String, Map<Serializable, Element>> CACHE = new ConcurrentHashMap<>();
    
    /**
     * 过期处理线程
     */
    private static ExpiredRunnable Expired = null;
    
    /**
     * 缓存开始时间戳（秒）
     */
    private static Long BEGIN = System.currentTimeMillis() / 1000;

    /**
     * 构造函数，初始化过期处理线程
     */
    public MemoryCache() {
        if (Expired == null) {
            synchronized (MemoryCache.class) {
                if (Expired == null) {
                    Expired = new ExpiredRunnable();
                    new Thread(Expired).start();
                }
            }
        }
    }

    /**
     * 获取指定名称的缓存映射
     * 
     * @param name 缓存名称
     * @return 缓存映射
     */
    protected static Map<Serializable, Element> getCache(String name) {
        return CACHE.computeIfAbsent(name, key -> new ConcurrentHashMap<>());
    }

    /**
     * 获取缓存值
     * 
     * @param name 缓存名称
     * @param key 缓存键
     * @param tClass 缓存值类型
     * @param <T> 缓存值类型泛型
     * @return 缓存值
     */
    @Override
    public <T extends Serializable> T get(String name, Serializable key, Type tClass) {
        Element obj = getCache(name).get(key);
        if (obj == null) {
            return null;
        }
        if (tClass != null) {
            return ValUtil.toObj(obj.item, JacksonUtil.mkJavaType(tClass));
        }
        return (T) obj.item;
    }

    /**
     * 设置缓存值
     * 
     * @param name 缓存名称
     * @param key 缓存键
     * @param value 缓存值
     * @param seconds 过期时间（秒），-1表示永不过期
     */
    @Override
    public void put(String name, Serializable key, Serializable value, int seconds) {
        Element element = new Element(value);
        if (seconds > 0) {
            element.expired = System.currentTimeMillis() / 1000 + seconds - BEGIN;
            Expired.setExpiredRange(element.expired);
        }
        getCache(name).put(ValUtil.toStr(key), element);
    }

    /**
     * 删除缓存值
     * 
     * @param name 缓存名称
     * @param key 缓存键
     */
    @Override
    public void remove(String name, Serializable key) {
        getCache(name).remove(ValUtil.toStr(key));
    }

    /**
     * 删除所有缓存
     * 
     * @param name 缓存名称
     */
    @Override
    public void removeAll(String name) {
        getCache(name).clear();
    }

    /**
     * 获取指定前缀的缓存键集合
     * 
     * @param name 缓存名称
     * @param keyPrefix 键前缀，支持通配符*
     * @return 缓存键集合
     */
    @Override
    public Set<String> keys(String name, String keyPrefix) {
        // 获取缓存中的键流
        Stream<String> stringStream = getCache(name).keySet().stream()
                .map(key -> ValUtil.toStr(key));

        // 如果 keyPrefix 是 "*" 或 ".*"，直接返回所有键
        if ("*".equals(keyPrefix) || ".*".equals(keyPrefix)) {
            return stringStream.collect(Collectors.toSet());
        }

        // 将 keyPrefix 转换为正则表达式
        String regex = keyPrefix.replaceAll("\\.\\*", ".*").replaceAll("\\*", ".*");
        Pattern pattern = Pattern.compile(regex);

        // 过滤并返回匹配的键
        return stringStream
                .filter(pattern.asPredicate())
                .collect(Collectors.toSet());

    }

    /**
     * 获取指定前缀的所有缓存
     * 
     * @param name 缓存名称
     * @param keyPrefix 键前缀，支持通配符*
     * @return 缓存映射
     */
    @Override
    public Map<String, Serializable> all(String name, String keyPrefix) {
        Map<Serializable, Element> cache = getCache(name);
        Map<String, Serializable> map = new ConcurrentHashMap<>();
        if ("*".equals(keyPrefix) || ".*".equals(keyPrefix)) {
            cache.forEach((key, value) -> map.put(ValUtil.toStr(key), value.item));
        } else {
            Pattern p = Pattern.compile(keyPrefix.replaceAll("\\.\\*", "*").replaceAll("\\*", ".*"));
            cache.forEach((key, value) -> {
                String keyStr = ValUtil.toStr(key);
                if (p.matcher(keyStr).matches()) {
                    map.put(keyStr, value.item);
                }
            });
        }
        return map;
    }

    /**
     * 缓存元素内部类
     */
    class Element {
        /**
         * 过期时间戳
         */
        Long expired;
        
        /**
         * 缓存项
         */
        Serializable item;

        /**
         * 构造函数
         * 
         * @param item 缓存项
         */
        Element(Serializable item) {
            this.item = item;
        }
    }

    /**
     * 过期处理线程内部类
     */
    class ExpiredRunnable implements Runnable {
        /**
         * 过期开始时间
         */
        private volatile Long begin;
        
        /**
         * 过期结束时间
         */
        private volatile Long end;

        /**
         * 设置过期时间范围
         * 
         * @param expired 过期时间戳
         */
        public void setExpiredRange(Long expired) {//设置过期区间
            if (begin == null || expired < begin) {
                begin = expired;
            }
            if (end == null || expired > end) {
                end = expired;
            }
        }

        /**
         * 执行过期清理任务
         */
        public void run() {//重写run方法
            while (true) {
                try {
                    Thread.sleep(1000);//实现定时去删除过期
                    expired(System.currentTimeMillis() / 1000 - MemoryCache.BEGIN);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("ExpiredRunnable interrupted", e);
                }
            }
        }

        /**
         * 清理过期缓存
         * 
         * @param curr 当前时间戳
         * @return 是否执行了清理操作
         */
        private boolean expired(long curr) {
            if (begin == null || end == null || begin > curr || end < curr) {
                return false;
            }
            begin = null;
            end = null;
            MemoryCache.CACHE.entrySet().stream().parallel().forEach(item -> {
                Map<Serializable, Element> cache = item.getValue();
                cache.forEach((key, value) -> {
                    if (value.expired != null) {
                        if (value.expired < curr) {
                            cache.remove(key);
                        } else {
                            setExpiredRange(value.expired);
                        }
                    }
                });
            });
            return true;
        }

        /**
         * 带日志的过期处理
         * 
         * @param curr 当前时间戳
         */
        private void expiredWithLog(long curr) {
            long startTime = System.currentTimeMillis();
            long totalSize = MemoryCache.CACHE.entrySet().stream().mapToLong(item -> item.getValue().size()).sum();

            if (!expired(curr)) {
                log.debug("skip: {}", totalSize);
            }

            long newSize = MemoryCache.CACHE.entrySet().stream().mapToLong(item -> item.getValue().size()).sum();
            long memoryUsage = Runtime.getRuntime().totalMemory();

            log.debug("sum: {} {} memory: {} time: {} {} {} {}", totalSize, newSize, memoryUsage, System.currentTimeMillis() - startTime, begin, curr, end);
        }
    }
}