package com.dlz.comm.cache;

import com.dlz.comm.exception.SystemException;
import com.dlz.comm.json.JSONMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CacheHolder类的单元测试
 * 
 * 测试缓存管理器的各种功能，包括缓存注册、获取、清除等操作
 * 
 * @author test
 */
public class CacheHolderTest {
    @BeforeEach
    public void setUp() {
        // 清理缓存状态
        clearAllCaches();
    }

    @AfterEach
    public void tearDown() {
        // 测试结束后清理
        clearAllCaches();
    }

    private void clearAllCaches() {
        // 使用反射清理静态缓存
        try {
            java.lang.reflect.Field cacheSetField = CacheHolder.class.getDeclaredField("CacheSet");
            cacheSetField.setAccessible(true);
            Map<String, ICache> cacheSet = (Map<String, ICache>) cacheSetField.get(null);
            cacheSet.clear();
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear cache", e);
        }
    }

    @Test
    @DisplayName("测试获取缓存集合")
    public void testGetCacheSet() {
        Map<String, ICache> cacheSet = CacheHolder.getCacheSet();
        assertNotNull(cacheSet);
        assertTrue(cacheSet.isEmpty());
    }

    @Test
    @DisplayName("测试获取缓存名称集合")
    public void testCacheNames() {
        Set<String> names = CacheHolder.cacheNames();
        assertNotNull(names);
        assertTrue(names.isEmpty());

        // 添加缓存后测试
        new MemoryCache("test1");
        new MemoryCache("test2");
        
        names = CacheHolder.cacheNames();
        assertEquals(2, names.size());
        assertTrue(names.contains("test1"));
        assertTrue(names.contains("test2"));
    }

    @Test
    @DisplayName("测试获取缓存实例")
    public void testGetCache() {
        ICache mockCache = new MemoryCache("testCache");
        ICache result = CacheHolder.get("testCache", mockCache);

        assertSame(mockCache, result);
        assertSame(mockCache, CacheHolder.get("testCache"));
    }

    @Test
    @DisplayName("测试重复获取同一缓存")
    public void testGetSameCacheTwice() {
        ICache mockCache = new MemoryCache("testCache");
        ICache result1 = CacheHolder.get("testCache", mockCache);
        ICache result2 = CacheHolder.get("testCache", mockCache);
        
        assertSame(result1, result2);
        assertSame(mockCache, result1);
    }

    @Test
    @DisplayName("测试获取不存在的缓存")
    public void testGetNonExistingCache() {
        ICache result = CacheHolder.get("nonExisting");
        assertNull(result);
    }

    @Test
    @DisplayName("测试添加缓存")
    public void testAddCache() {
        ICache mockCache = new MemoryCache("testCache");
        assertThrows(SystemException.class, () -> CacheHolder.add("testCache", new MemoryCache()));
        ICache result = CacheHolder.get("testCache");
        assertSame(mockCache, result);
    }

    @Test
    @DisplayName("测试重复添加缓存抛出异常")
    public void testAddDuplicateCache() {
        ICache mockCache1 = new MemoryCache("testCache");
        assertThrows(SystemException.class, () -> new MemoryCache("testCache"));
        assertThrows(SystemException.class, () -> CacheHolder.add("testCache", mockCache1));

        SystemException exception = assertThrows(SystemException.class, () -> {
            CacheHolder.add("testCache", mockCache1);
        });
        
        assertTrue(exception.getMessage().contains("缓存已经存在"));
    }

    @Test
    @DisplayName("测试添加null缓存抛出异常")
    public void testAddNullCache() {
        SystemException exception = assertThrows(SystemException.class, () -> {
            CacheHolder.add("testCache", null);
        });
        
        assertTrue(exception.getMessage().contains("ICache 未定义"));
    }

    @Test
    @DisplayName("测试获取缓存键集合")
    public void testKeys() {
        MemoryCache mockCache = new MemoryCache("testCache");
        mockCache.put("testCache", "key1", "value1");
        mockCache.put("testCache", "key2", "value2");
        mockCache.put("testCache", "otherKey", "value3");
        
        CacheHolder.get("testCache", mockCache);
        
        Set<String> keys = CacheHolder.keys("testCache", "key*");
        assertEquals(2, keys.size());
        assertTrue(keys.contains("key1"));
        assertTrue(keys.contains("key2"));
        assertFalse(keys.contains("otherKey"));
    }

    @Test
    @DisplayName("测试获取所有缓存信息")
    public void testCacheInfos() {
        MemoryCache mockCache1 = new MemoryCache("cache1");
        MemoryCache mockCache2 = new MemoryCache("cache2");
        
        mockCache1.put("cache1", "key1", "value1");
        mockCache1.put("cache1", "key2", "value2");
        mockCache2.put("cache2", "key3", "value3");
        
        CacheHolder.get("cache1", mockCache1);
        CacheHolder.get("cache2", mockCache2);
        
        List<JSONMap> infos = CacheHolder.cacheInfos();
        assertEquals(2, infos.size());
        
        // 验证每个缓存的信息
        for (JSONMap info : infos) {
            String name = info.getStr("name");
            String className = info.getStr("cla");
            Integer size = info.getInt("size");
            
            assertNotNull(name);
            assertNotNull(className);
            assertNotNull(size);
            
            if ("cache1".equals(name)) {
                assertEquals(2, size.intValue());
            } else if ("cache2".equals(name)) {
                assertEquals(1, size.intValue());
            }
        }
    }

    @Test
    @DisplayName("测试清除指定缓存")
    public void testClearSpecificCache() {
        MemoryCache mockCache = new MemoryCache("testCache");
        mockCache.put("testCache", "key1", "value1");
        mockCache.put("testCache", "key2", "value2");
        
        CacheHolder.get("testCache", mockCache);
        assertEquals("value1", mockCache.get("testCache", "key1", String.class));
        assertEquals("value2", mockCache.get("testCache", "key2"));
        mockCache.remove("testCache", "key1");
        assertNull( mockCache.get("testCache", "key1"));

    }

    @Test
    @DisplayName("测试清除所有缓存")
    public void testClearAll() {
        MemoryCache cache1 = new MemoryCache("cache1");
        MemoryCache cache2 = new MemoryCache("cache2");
        
        cache1.put("cache1", "key1", "value1");
        cache2.put("cache2", "key2", "value2");
        
        CacheHolder.get("cache1", cache1);
        CacheHolder.get("cache2", cache2);
        

        
        CacheHolder.clearAll();

    }

    @Test
    @DisplayName("测试并发访问")
    public void testConcurrentAccess() throws InterruptedException {
        final int threadCount = 10;
        final int operationsPerThread = 100;
        Thread[] threads = new Thread[threadCount];
        
        // 创建共享缓存
        MemoryCache sharedCache = new MemoryCache("sharedCache");
        CacheHolder.get("sharedCache", sharedCache);
        
        // 启动多个线程同时操作缓存
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    String key = "thread" + threadId + "_key" + j;
                    String value = "value_" + threadId + "_" + j;
                    
                    // 写入操作
                    CacheHolder.get("sharedCache").put("sharedCache", key, value);
                    
                    // 读取操作
                    String retrieved = CacheHolder.get("sharedCache").get("sharedCache", key, String.class);
                    assertEquals(value, retrieved);
                }
            });
            threads[i].start();
        }
        
        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }
        
        // 验证最终状态
        assertEquals(threadCount * operationsPerThread, sharedCache.all("sharedCache", "*").size());
    }

    @Test
    @DisplayName("测试边界条件：空缓存名称")
    public void testEmptyCacheName() {
        ICache mockCache = new MemoryCache("");
        ICache result = CacheHolder.get("", mockCache);
        assertSame(mockCache, result);
        
        Set<String> names = CacheHolder.cacheNames();
        assertTrue(names.contains(""));
    }

    @Test
    @DisplayName("测试边界条件：特殊字符缓存名称")
    public void testSpecialCharacterCacheName() {
        String specialName = "cache-with.special_chars@123";
        ICache mockCache = new MemoryCache(specialName);
        ICache result = CacheHolder.get(specialName, mockCache);
        assertSame(mockCache, result);
        
        Set<String> names = CacheHolder.cacheNames();
        assertTrue(names.contains(specialName));
    }
}