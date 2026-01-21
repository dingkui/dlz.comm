# Cache - 缓存工具类

[返回首页](../README.md) / [缓存工具](./cache.md)

## 简介

DLZ COMM 提供了完整的缓存解决方案，包括缓存接口定义和内存缓存实现。

## 功能特点

- 统一的缓存接口
- 内存缓存实现
- 支持过期时间
- 支持批量操作
- 自动过期清理
- 线程安全

## ICache 接口

### 接口定义

```java
public interface ICache {
    // 基本操作
    <T extends Serializable> T get(String name, Serializable key, Type tClass);
    void put(String name, Serializable key, Serializable value, int seconds);
    void remove(String name, Serializable key);
    void removeAll(String name);
    
    // 批量操作
    Set<String> keys(String name, String keyPrefix);
    Map<String, Serializable> all(String name, String keyPrefix);
    
    // 便捷方法
    default <T> T getAndSet(String name, Serializable key, Callable<VAL<T, Integer>> valueLoader);
    default <T> List<T> getAndSetList(String name, Serializable key, Callable<VAL<List<T>, Integer>> valueLoader, Class<T> tClass);
}
```

### 主要方法

#### 获取缓存

```java
// 基本获取
T value = cache.get(cacheName, key, type);

// 获取或设置
T value = cache.getAndSet(cacheName, key, () -> VAL.of(expensiveOperation(), 300)); // 5分钟过期
```

#### 设置缓存

```java
// 设置缓存（永不过期）
cache.put(cacheName, key, value);

// 设置缓存（指定过期时间）
cache.put(cacheName, key, value, seconds);

// 设置缓存（-1表示永不过期）
cache.put(cacheName, key, value, -1);
```

#### 删除操作

```java
// 删除单个缓存
cache.remove(cacheName, key);

// 删除所有缓存
cache.removeAll(cacheName);
```

#### 批量操作

```java
// 获取缓存键
Set<String> keys = cache.keys(cacheName, "prefix*");

// 获取所有缓存
Map<String, Serializable> all = cache.all(cacheName, "prefix*");
```

## MemoryCache 实现

### 特性

- 基于内存的缓存实现
- 支持自动过期清理
- 线程安全
- 高性能

### 使用示例

```java
ICache cache = new MemoryCache();

// 基本操作
cache.put("user", "123", userObject);
User user = cache.get("user", "123", User.class);

// 设置过期时间（300秒）
cache.put("session", "token123", sessionData, 300);

// 删除操作
cache.remove("user", "123");
cache.removeAll("user");
```

## 实际应用示例

### 用户信息缓存

```java
public class UserService {
    private ICache cache = new MemoryCache();
    private static final String USER_CACHE = "user_cache";
    private static final int USER_EXPIRY = 3600; // 1小时
    
    public User getUserById(Long userId) {
        String key = "user_" + userId;
        
        // 先从缓存获取
        User user = cache.get(USER_CACHE, key, User.class);
        if (user != null) {
            return user;
        }
        
        // 缓存未命中，从数据库获取
        user = database.getUserById(userId);
        if (user != null) {
            // 存入缓存
            cache.put(USER_CACHE, key, user, USER_EXPIRY);
        }
        
        return user;
    }
    
    public void updateUser(User user) {
        String key = "user_" + user.getId();
        
        // 更新数据库
        database.updateUser(user);
        
        // 更新缓存
        cache.put(USER_CACHE, key, user, USER_EXPIRY);
    }
    
    public void deleteUser(Long userId) {
        String key = "user_" + userId;
        
        // 删除数据库记录
        database.deleteUser(userId);
        
        // 删除缓存
        cache.remove(USER_CACHE, key);
    }
}
```

### 会话管理

```java
public class SessionManager {
    private ICache cache = new MemoryCache();
    private static final String SESSION_CACHE = "session";
    
    public void createSession(String sessionId, SessionData data) {
        // 会话有效期30分钟
        cache.put(SESSION_CACHE, sessionId, data, 1800);
    }
    
    public SessionData getSession(String sessionId) {
        return cache.get(SESSION_CACHE, sessionId, SessionData.class);
    }
    
    public void extendSession(String sessionId) {
        SessionData data = getSession(sessionId);
        if (data != null) {
            // 延长会话有效期
            cache.put(SESSION_CACHE, sessionId, data, 1800);
        }
    }
    
    public void invalidateSession(String sessionId) {
        cache.remove(SESSION_CACHE, sessionId);
    }
}
```

### 配置缓存

```java
public class ConfigService {
    private ICache cache = new MemoryCache();
    private static final String CONFIG_CACHE = "config";
    
    public <T> T getConfig(String key, Class<T> type) {
        return cache.get(CONFIG_CACHE, key, JacksonUtil.mkJavaType(type));
    }
    
    public void setConfig(String key, Object value) {
        cache.put(CONFIG_CACHE, key, value, -1); // 永不过期
    }
    
    public Map<String, Object> getAllConfigs(String prefix) {
        return cache.all(CONFIG_CACHE, prefix);
    }
    
    public void refreshConfigs() {
        cache.removeAll(CONFIG_CACHE);
        // 重新加载配置
        loadConfigsFromSource();
    }
}
```

### 统计数据缓存

```java
public class StatsService {
    private ICache cache = new MemoryCache();
    private static final String STATS_CACHE = "stats";
    
    public Map<String, Integer> getDailyStats(String date) {
        String key = "daily_stats_" + date;
        
        return cache.getAndSet(STATS_CACHE, key, () -> {
            // 计算统计数据（耗时操作）
            Map<String, Integer> stats = calculateDailyStats(date);
            // 缓存1小时
            return VAL.of(stats, 3600);
        });
    }
    
    public List<UserStat> getUserStats(List<Long> userIds) {
        String key = "user_stats_" + String.join(",", userIds.toString());
        
        return cache.getAndSetList(STATS_CACHE, key, () -> {
            List<UserStat> stats = calculateUserStats(userIds);
            return VAL.of(stats, 1800); // 缓存30分钟
        }, UserStat.class);
    }
}
```

## 高级功能

### 通配符查询

```java
// 获取特定前缀的所有缓存键
Set<String> userKeys = cache.keys("user_cache", "user_*");

// 获取特定前缀的所有缓存数据
Map<String, Serializable> userCache = cache.all("user_cache", "user_*");
```

### 批量操作

```java
// 批量删除
public void batchDelete(String cacheName, String prefix) {
    Set<String> keys = cache.keys(cacheName, prefix + "*");
    for (String key : keys) {
        cache.remove(cacheName, key);
    }
}
```

## 过期机制

### 自动清理

MemoryCache 实现了自动过期清理机制：

1. **后台线程**：启动一个清理线程，每秒执行一次清理
2. **时间范围**：跟踪缓存的过期时间范围
3. **批量清理**：一次性清理所有过期的缓存项

### 过期策略

- `seconds = -1`：永不过期
- `seconds > 0`：指定秒数后过期
- `seconds = 0`：立即过期

## 性能优化建议

### 缓存键设计

1. **命名规范**：使用有意义的缓存名和键名
2. **层次结构**：使用分隔符组织缓存键
3. **长度控制**：避免过长的缓存键

### 过期时间设置

1. **合理设置**：根据数据变化频率设置过期时间
2. **分级缓存**：不同类型数据设置不同过期时间
3. **动态调整**：根据访问模式调整过期时间

### 内存管理

1. **监控使用**：监控缓存内存使用情况
2. **清理策略**：及时清理不需要的缓存
3. **容量规划**：根据可用内存规划缓存容量

## 安全注意事项

### 数据安全

- 只缓存非敏感数据或已加密数据
- 避免缓存包含个人身份信息的数据
- 定期清理缓存以防止数据泄露

### 访问控制

- 限制对缓存的直接访问
- 实现适当的权限控制
- 监控缓存访问模式

## 注意事项

- 所有缓存键和值必须实现 `Serializable` 接口
- 过期时间单位为秒
- 空间复杂度：O(n)，其中 n 为缓存项数量
- 时间复杂度：获取 O(1)，设置 O(1)，删除 O(1)
- 线程安全：MemoryCache 是线程安全的
- 序列化开销：注意序列化/反序列化对性能的影响