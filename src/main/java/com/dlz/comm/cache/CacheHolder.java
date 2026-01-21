package com.dlz.comm.cache;

import com.dlz.comm.exception.SystemException;
import com.dlz.comm.json.JSONMap;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 缓存记录
 *
 * @author dk
 */
@Slf4j
public class CacheHolder {
    private static Map<String, ICache> CacheSet = new ConcurrentHashMap<>();


    public static Map<String, ICache> getCacheSet() {
        return CacheSet;
    }

    public static Set<String> cacheNames() {
        return CacheSet.entrySet().stream().map(item -> item.getKey()).collect(Collectors.toSet());
    }

    public static Set<String> keys(String cacheName, String keyPrefix) {
        return get(cacheName).keys(cacheName, keyPrefix);
    }

    public static List<JSONMap> cacheInfos() {
        return CacheSet.entrySet().stream()
                .map(item -> {
                    final String name = item.getKey();
                    final ICache cache = item.getValue();
                    final JSONMap map = new JSONMap();
                    map.put("name", name);
                    map.put("cla", cache.getClass().getName());
                    map.put("size", cache.keys(name).size());
                    return map;
                }).collect(Collectors.toList());

    }

    public static void clearAll() {
        for (Map.Entry<String, ICache> deal : CacheSet.entrySet()) {
            deal.getValue().removeAll(deal.getKey());
        }
    }

    public static void clear(String cacheName) {
        CacheSet.get(cacheName).removeAll(cacheName);
    }

    public static ICache get(String cacheName) {
        return CacheSet.get(cacheName);
    }

    public static ICache get(String cacheName, ICache cache) {
        ICache iCache = CacheSet.get(cacheName);
        if (iCache != null) {
            return iCache;
        }
        SystemException.notNull(cache, () -> "ICache不能为空：" + cacheName);
        CacheSet.put(cacheName, cache);
        return cache;
    }

    public static void add(String cacheName, ICache cache) {
        SystemException.isTrue(CacheSet.containsKey(cacheName), () -> "缓存已经存在，不能重复定义：" + cacheName);
        SystemException.isTrue(cache == null, () -> "ICache 未定义！");
        CacheSet.put(cacheName, cache);
    }
}