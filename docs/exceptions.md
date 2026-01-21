# Exceptions - 异常处理工具

[返回首页](../README.md) / [异常处理](./exceptions.md)

## 简介

DLZ COMM 提供了一个完善的异常处理体系，基于继承链结构设计，便于分类管理和统一处理。

## 异常体系结构

```
BaseException
├── SystemException
├── BussinessException
├── CredentialsException
├── DbException
├── HttpException
├── RemoteException
└── ValidateException
```

## BaseException - 基础异常类

### 功能特点

- 统一的异常基类
- 支持错误码管理
- 包含错误信息和消息

### 主要属性

```java
private int code;     // 错误码
private String info;  // 错误信息
private String msg;   // 错误消息
```

### 构造方法

```java
// 使用错误码和异常
BaseException(int code, Throwable exception);

// 使用错误码、消息和原因
BaseException(int code, String message, Throwable cause);

// 使用错误码和消息
BaseException(int code, String message);
```

### 主要方法

```java
// 获取错误码
int getCode();

// 获取错误信息
String getInfo();

// 获取错误消息
String getMsg();

// 检查错误码
boolean is(int code);
```

## SystemException - 系统异常

### 功能特点

- 系统级别异常处理
- 提供便捷的断言方法
- 自动异常类型识别

### 主要方法

```java
// 构造方法
SystemException(String message);
SystemException(String message, Throwable cause);

// 异常构建
static SystemException build(Throwable cause);
static SystemException build(String message);
static SystemException build(String message, Throwable cause);

// 断言方法
static void isTrue(boolean expression, String message);
static void notNull(Object object, String message);
static void notEmpty(Object value, String message);
```

### 使用示例

```java
// 基本使用
throw new SystemException("系统错误");

// 包含原因
throw new SystemException("操作失败", cause);

// 自动构建
SystemException ex = SystemException.build(runtimeException);

// 断言使用
SystemException.isTrue(user != null, "用户不能为空");
SystemException.notNull(user, "用户不能为空");
SystemException.notEmpty(username, "用户名不能为空");
```

## ValidateException - 验证异常

### 功能特点

- 专门用于数据验证
- 提供验证相关的断言方法

### 主要方法

```java
// 构造方法
ValidateException(String message);

// 异常构建
static ValidateException build(String message);

// 验证断言
static void isTrue(boolean expression, String message);
static void notNull(Object object, String message);
static void notEmpty(Object value, String message);
```

### 使用示例

```java
// 数据验证
public void validateUser(User user) {
    ValidateException.notNull(user, "用户对象不能为空");
    ValidateException.notEmpty(user.getName(), "用户名不能为空");
    ValidateException.notEmpty(user.getEmail(), "邮箱不能为空");
    
    if (!isValidEmail(user.getEmail())) {
        throw ValidateException.build("邮箱格式不正确");
    }
}
```

## 其他异常类型

### BussinessException - 业务异常

```java
// 用于业务逻辑错误
throw new BussinessException("订单状态不允许取消");
```

### CredentialsException - 凭证异常

```java
// 用于认证授权错误
throw new CredentialsException("用户名或密码错误");
```

### DbException - 数据库异常

```java
// 用于数据库操作错误
throw new DbException("数据库连接失败");
```

### HttpException - HTTP异常

```java
// 用于HTTP请求错误
throw new HttpException("请求超时");
```

### RemoteException - 远程调用异常

```java
// 用于远程服务调用错误
throw new RemoteException("远程服务不可用");
```

## 实际应用示例

### 统一异常处理

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setCode(ex.getCode());
        error.setMessage(ex.getMsg());
        error.setInfo(ex.getInfo());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ErrorResponse> handleSystemException(SystemException ex) {
        // 系统异常特殊处理
        log.error("系统异常", ex);
        ErrorResponse error = new ErrorResponse();
        error.setCode(ex.getCode());
        error.setMessage("系统内部错误");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    @ExceptionHandler(ValidateException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidateException ex) {
        // 验证异常处理
        ErrorResponse error = new ErrorResponse();
        error.setCode(400);
        error.setMessage("数据验证失败: " + ex.getMsg());
        
        return ResponseEntity.badRequest().body(error);
    }
}
```

### 服务层异常处理

```java
@Service
public class UserService {
    
    public User createUser(User user) {
        try {
            // 验证数据
            validateUser(user);
            
            // 检查用户是否存在
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new BussinessException("邮箱已被注册");
            }
            
            // 保存用户
            return userRepository.save(user);
            
        } catch (ValidateException e) {
            // 验证异常直接抛出
            throw e;
        } catch (DbException e) {
            // 数据库异常包装
            throw new SystemException("保存用户失败", e);
        } catch (Exception e) {
            // 其他异常包装为系统异常
            throw new SystemException("创建用户失败", e);
        }
    }
    
    private void validateUser(User user) {
        ValidateException.notNull(user, "用户对象不能为空");
        ValidateException.notEmpty(user.getName(), "用户名不能为空");
        ValidateException.notEmpty(user.getEmail(), "邮箱不能为空");
        
        if (!isValidEmail(user.getEmail())) {
            throw ValidateException.build("邮箱格式不正确");
        }
    }
    
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@");
    }
}
```

### 数据访问层异常处理

```java
@Repository
public class UserRepository {
    
    public User findByEmail(String email) {
        try {
            // 数据库查询
            return jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE email = ?", 
                User.class, 
                email
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new DbException("查询用户失败", e);
        }
    }
    
    public boolean existsByEmail(String email) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email = ?", 
                Integer.class, 
                email
            );
            return count != null && count > 0;
        } catch (DataAccessException e) {
            throw new DbException("检查用户存在性失败", e);
        }
    }
}
```

### 控制器层异常处理

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (ValidateException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(400, "数据验证失败: " + e.getMsg()));
        } catch (BussinessException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(400, e.getMsg()));
        } catch (SystemException e) {
            log.error("创建用户时发生系统异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, "系统内部错误"));
        }
    }
}
```

## 异常码管理

### 错误码定义

```java
public class ExceptionCodes {
    // 系统错误
    public static final int SYSTEM_ERROR = 6001;
    public static final int VALIDATION_ERROR = 3003;
    
    // 业务错误
    public static final int BUSINESS_ERROR = 4001;
    public static final int CREDENTIALS_ERROR = 5001;
    public static final int DATABASE_ERROR = 7001;
    public static final int HTTP_ERROR = 8001;
    public static final int REMOTE_ERROR = 9001;
}
```

### 使用错误码

```java
// 使用预定义错误码
throw new SystemException(ExceptionCodes.SYSTEM_ERROR, "系统错误");

// 检查错误码
if (exception.is(ExceptionCodes.VALIDATION_ERROR)) {
    // 处理验证错误
}
```

## 最佳实践

### 异常捕获

1. **明确捕获**：捕获具体异常类型而非通用 Exception
2. **适当包装**：将底层异常包装为业务异常
3. **保留原因**：保留原始异常信息用于调试

### 异常抛出

1. **选择合适类型**：根据错误性质选择合适的异常类型
2. **提供有用信息**：包含足够的上下文信息
3. **避免信息泄露**：生产环境避免暴露敏感信息

### 异常处理

1. **统一处理**：使用全局异常处理器
2. **记录日志**：记录异常信息用于问题排查
3. **用户友好**：向用户返回友好的错误信息

## 注意事项

- BaseException 继承自 RuntimeException
- 所有异常类都是可序列化的
- 错误码用于区分不同类型的错误
- 异常消息应简洁明了
- 遵循异常处理的最佳实践
- 避免过度包装异常
- 适当使用断言方法进行验证