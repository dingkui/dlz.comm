# EncryptUtil - 加密工具类

[返回首页](../README.md) / [加密工具](./encryptutil.md)

## 简介

`EncryptUtil` 是一个基于 Authcode 算法的加密解密工具类，提供了对称加密和解密功能。

## 功能特点

- 对称加密解密
- 支持过期时间设置
- 全局密钥管理
- 基于 Authcode 算法

## 主要方法

### 加密方法

```java
// 使用全局密钥加密
String encoded = EncryptUtil.encode(plaintext);

// 使用指定密钥加密
String encoded = EncryptUtil.encode(plaintext, key);

// 指定过期时间加密
String encoded = EncryptUtil.encode(plaintext, expiry);

// 指定密钥和过期时间加密
String encoded = EncryptUtil.encode(plaintext, key, expiry);
```

### 解密方法

```java
// 使用全局密钥解密
String decoded = EncryptUtil.decode(encodedText);

// 使用指定密钥解密
String decoded = EncryptUtil.decode(encodedText, key);
```

### 全局配置

```java
// 全局认证密钥
String globalKey = EncryptUtil.GLOBAL_AUTH_KEY;
```

## 使用示例

### 基本加密解密

```java
// 简单加密解密
String original = "Hello, World!";
String encrypted = EncryptUtil.encode(original);
String decrypted = EncryptUtil.decode(encrypted);

System.out.println("Original: " + original);      // Original: Hello, World!
System.out.println("Encrypted: " + encrypted);   // Encrypted: （加密后的内容）
System.out.println("Decrypted: " + decrypted);   // Decrypted: Hello, World!
```

### 使用自定义密钥

```java
// 使用自定义密钥
String customKey = "myCustomKey123";
String original = "Sensitive Data";
String encrypted = EncryptUtil.encode(original, customKey);
String decrypted = EncryptUtil.decode(encrypted, customKey);

System.out.println("Encrypted with custom key: " + encrypted);
System.out.println("Decrypted: " + decrypted);  // Sensitive Data
```

### 设置过期时间

```java
// 设置过期时间（秒）
String original = "Temporary Data";
Integer expiry = 3600; // 1小时后过期
String encrypted = EncryptUtil.encode(original, expiry);

// 解密（如果在过期时间内）
String decrypted = EncryptUtil.decode(encrypted);
System.out.println("Decrypted: " + decrypted);  // Temporary Data
```

### 混合参数使用

```java
// 同时指定密钥和过期时间
String original = "Secure Message";
String key = "secretKey";
Integer expiry = 7200; // 2小时后过期

String encrypted = EncryptUtil.encode(original, key, expiry);
String decrypted = EncryptUtil.decode(encrypted, key);

System.out.println("Message: " + decrypted);  // Secure Message
```

## 高级用法

### 用户会话管理

```java
// 创建用户会话令牌
public class SessionManager {
    public static String createSessionToken(String userId, String userData) {
        String sessionData = userId + "|" + userData + "|" + System.currentTimeMillis();
        return EncryptUtil.encode(sessionData, 3600); // 1小时过期
    }
    
    public static String validateSessionToken(String token) {
        try {
            String decoded = EncryptUtil.decode(token);
            // 解析会话数据
            String[] parts = decoded.split("\\|", 3);
            if (parts.length >= 3) {
                String userId = parts[0];
                String userData = parts[1];
                long timestamp = Long.parseLong(parts[2]);
                
                // 验证时间戳（这里假设已过期检查由加密算法处理）
                return userId;
            }
        } catch (Exception e) {
            // 解密失败或数据格式错误
            return null;
        }
        return null;
    }
}

// 使用示例
String token = SessionManager.createSessionToken("user123", "admin:true");
String userId = SessionManager.validateSessionToken(token);
```

### 数据传输加密

```java
// API 数据加密传输
public class ApiSecurity {
    public static String encryptApiData(Map<String, Object> data) {
        String jsonData = JacksonUtil.getJson(data);
        return EncryptUtil.encode(jsonData, 1800); // 30分钟过期
    }
    
    public static Map<String, Object> decryptApiData(String encryptedData) {
        String decrypted = EncryptUtil.decode(encryptedData);
        if (decrypted != null) {
            return JacksonUtil.readValue(decrypted, JSONMap.class);
        }
        return null;
    }
}

// 使用示例
Map<String, Object> sensitiveData = new HashMap<>();
sensitiveData.put("userId", 12345);
sensitiveData.put("balance", 1000.00);

String encrypted = ApiSecurity.encryptApiData(sensitiveData);
Map<String, Object> decrypted = ApiSecurity.decryptApiData(encrypted);
```

### 配置加密

```java
// 加密配置信息
public class ConfigEncryption {
    private static final String CONFIG_KEY = "config_encryption_key";
    
    public static String encryptConfigValue(String value) {
        return EncryptUtil.encode(value, CONFIG_KEY);
    }
    
    public static String decryptConfigValue(String encryptedValue) {
        return EncryptUtil.decode(encryptedValue, CONFIG_KEY);
    }
}

// 使用示例
String dbPassword = "secret_password";
String encryptedPassword = ConfigEncryption.encryptConfigValue(dbPassword);
String decryptedPassword = ConfigEncryption.decryptConfigValue(encryptedPassword);
```

## 全局密钥管理

### 默认密钥

```java
// 默认全局认证密钥
public static String GLOBAL_AUTH_KEY = "e317b362fafa0c96c20b8543d054b850";
```

### 自定义全局密钥

```java
// 在应用启动时设置全局密钥
static {
    // 从环境变量或配置文件读取
    String keyFromEnv = System.getenv("APP_ENCRYPT_KEY");
    if (keyFromEnv != null && !keyFromEnv.isEmpty()) {
        EncryptUtil.GLOBAL_AUTH_KEY = keyFromEnv;
    }
}
```

## 安全最佳实践

### 密钥管理

1. **密钥强度**：使用足够复杂的密钥
2. **密钥轮换**：定期更换加密密钥
3. **环境隔离**：不同环境使用不同密钥
4. **密钥保护**：避免在代码中硬编码密钥

### 使用场景

1. **临时数据**：适合加密短期使用的数据
2. **传输加密**：适合 API 数据传输加密
3. **会话管理**：适合用户会话令牌生成
4. **配置加密**：适合敏感配置信息加密

### 安全警告

- 不适用于长期存储的敏感数据
- 不适用于高强度安全要求场景
- 建议结合其他安全措施使用

## 性能优化建议

1. **密钥复用**：对于频繁操作，复用相同的密钥
2. **批量处理**：对于大量数据，考虑批量加密
3. **缓存策略**：对于不变数据，考虑缓存加密结果
4. **异步处理**：对于大量数据，考虑异步加密

## 注意事项

- 加密结果包含时间戳，相同输入会产生不同输出
- 过期时间从加密时刻开始计算
- 解密失败时返回 null
- 密钥为空时使用全局密钥
- 算法基于 Authcode，安全性取决于密钥强度
- 线程安全：`EncryptUtil` 的方法是线程安全的