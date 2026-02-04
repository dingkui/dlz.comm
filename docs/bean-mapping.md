# 🔥 @SetValue 注解 - 扁平 Bean ↔ 嵌套 JSON 双向映射

[返回首页](../README.md) | [功能导航](./features.md)

---

## 🎯 核心能力

`@SetValue` 注解是一个**结构映射神器**，让你用**扁平的代码结构**操作**嵌套的数据结构**，兼顾开发体验和存储灵活性！

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│   扁平 Bean                          嵌套 JSON                  │
│   ┌─────────────┐                   ┌─────────────────────┐     │
│   │ name        │                   │ {                   │     │
│   │ phone ──────┼── @SetValue ────► │   "name": "...",    │     │
│   │ address ────┼── ("info")  ────► │   "info": {         │     │
│   └─────────────┘        ◄────────  │     "phone": "...", │     │
│                                     │     "address": "..." │     │
│                                     │   }                 │     │
│                                     │ }                   │     │
│                                     └─────────────────────┘     │
│                                                                 │
│   ★ 代码里用扁平结构，存储/传输用嵌套结构，完美兼得！★          │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 💡 应用场景大全

### 场景1：数据库 JSON 字段 + Excel 导入导出

```java
// 数据库表结构
// user 表: id, name, ext_info(JSON字段存动态扩展)

@Data
@TableName("user")
public class User {
    private Long id;
    private String name;
    
    // 这些字段存到 ext_info JSON 字段里
    @SetValue("ext_info")
    private String phone;      // ext_info.phone
    @SetValue("ext_info")
    private String address;    // ext_info.address
    @SetValue("ext_info")
    private String customField1; // 动态扩展字段
}

// ✅ Excel 导入：用户只看到扁平的列
// | id | name | phone | address | customField1 |

// ✅ 存数据库：自动转成 JSON
// { "id": 1, "name": "张三", "ext_info": {"phone": "13800138000", "address": "北京", "customField1": "xxx"} }
```

---

### 场景2：前后端结构差异适配

```java
// 前端需要的结构（嵌套）
{
    "basicInfo": { "name": "张三", "age": 25 },
    "contactInfo": { "phone": "138xxx", "email": "xxx@xx.com" }
}

// 后端 Bean（扁平，好处理）
@Data
public class UserDTO {
    @SetValue("basicInfo")
    private String name;
    @SetValue("basicInfo")
    private Integer age;
    
    @SetValue("contactInfo")
    private String phone;
    @SetValue("contactInfo")
    private String email;
}

// ✅ 代码里操作扁平结构，返回给前端自动嵌套！
UserDTO dto = new UserDTO();
dto.setName("张三");
dto.setAge(25);

JSONMap response = new JSONMap();
BeanUtil.copyAsSource(dto, response, false);
return response; // 自动变成前端要的嵌套结构
```

---

### 场景3：多表数据合并成一个对象

```java
// 主表：user (id, name, create_time)
// 扩展表：user_ext (user_id, vip_level, points) 或者就存 JSON

@Data
public class UserFullInfo {
    // 主表字段
    private Long id;
    private String name;
    private Date createTime;
    
    // 扩展表字段，映射到 ext 节点
    @SetValue("ext")
    private Integer vipLevel;
    @SetValue("ext")
    private Long points;
    @SetValue("ext")
    private String customData;
}

// ✅ 查询时自动合并，存储时自动拆分
```

---

### 场景4：API 版本兼容

```java
// V1 API 返回扁平结构
// V2 API 返回嵌套结构
// 用同一个 Bean，根据版本选择转换方式

@Data
public class OrderDTO {
    private Long orderId;
    private BigDecimal amount;
    
    @SetValue("buyer")
    private String buyerName;
    @SetValue("buyer")
    private String buyerPhone;
    
    @SetValue("shipping")
    private String address;
    @SetValue("shipping")
    private String trackingNo;
}

// V1: 直接返回 Bean（扁平）
// V2: BeanUtil.copyAsSource 转成嵌套 JSON
```

---

### 场景5：配置中心/动态配置

```java
// Nacos/Apollo 里的配置是嵌套 JSON
{
    "database": { "host": "localhost", "port": 3306 },
    "redis": { "host": "localhost", "port": 6379 },
    "features": { "enableCache": true, "maxRetry": 3 }
}

// 代码里用扁平 Bean，IDE 有提示，不容易写错
@Data
public class AppConfig {
    @SetValue("database")
    private String dbHost;
    @SetValue("database")
    private Integer dbPort;
    
    @SetValue("redis")
    private String redisHost;
    @SetValue("redis")
    private Integer redisPort;
    
    @SetValue("features")
    private Boolean enableCache;
    @SetValue("features")
    private Integer maxRetry;
}

// ✅ config.getDbHost() 比 config.get("database.host") 更安全！
```

---

### 场景6：ES/MongoDB 文档映射

```java
// ES 文档结构（嵌套）
{
    "title": "商品标题",
    "price": 99.9,
    "attributes": {
        "color": "红色",
        "size": "XL",
        "material": "棉"
    },
    "stats": {
        "views": 1000,
        "sales": 50
    }
}

// Java Bean（扁平）
@Data
public class ProductDoc {
    private String title;
    private BigDecimal price;
    
    @SetValue("attributes")
    private String color;
    @SetValue("attributes")
    private String size;
    @SetValue("attributes")
    private String material;
    
    @SetValue("stats")
    private Long views;
    @SetValue("stats")
    private Long sales;
}
```

---

### 场景7：数据迁移 / ETL

```java
// 老系统数据结构
{ "user_name": "张三", "user_age": 25, "addr_city": "北京", "addr_street": "xx路" }

// 新系统数据结构
{ "name": "张三", "age": 25, "address": { "city": "北京", "street": "xx路" } }

// 迁移 Bean
@Data
public class MigrationBean {
    @SetValue("")  // 根节点
    private String name;
    @SetValue("")
    private Integer age;
    
    @SetValue("address")
    private String city;
    @SetValue("address")
    private String street;
}

// ✅ 老数据 → Bean → 新结构，完美迁移
```

---

### 场景8：低代码/表单设计器

```java
// 表单设计器生成的动态表单
// 用户自定义了 3 个字段组：基本信息、联系方式、其他
// 存储时按分组嵌套，展示时扁平化

@Data
public class DynamicFormData {
    @SetValue("基本信息")
    private String field1;
    @SetValue("基本信息")
    private String field2;
    
    @SetValue("联系方式")
    private String field3;
    @SetValue("联系方式")
    private String field4;
    
    @SetValue("其他")
    private String field5;
}

// ✅ 完美支持动态分组！
```

---

## 🆚 这个特性的独特价值

| 传统方案 | 问题 | @SetValue 方案 |
|---------|------|---------------|
| 写两个 DTO（扁平+嵌套） | 代码冗余，维护成本高 | 一个 Bean 搞定 |
| 手动转换代码 | 每次都要写，容易出错 | 注解声明，自动转换 |
| 只用 Map 操作 | 没有类型提示，重构危险 | Bean 有 IDE 支持 |
| 数据库存多个字段 | 扩展字段要改表结构 | JSON 字段无限扩展 |

---

## 📖 使用方法

### 基本用法

```java
@Data
public class User {
    private String name;
    
    @SetValue("info")
    private String phone;
    
    @SetValue("info")
    private String address;
}

// Bean → JSONMap
User user = new User();
user.setName("张三");
user.setPhone("13800138000");
user.setAddress("北京");

JSONMap json = new JSONMap();
BeanUtil.copyAsSource(user, json, false);

// 结果：
// {
//   "name": "张三",
//   "info": {
//     "phone": "13800138000",
//     "address": "北京"
//   }
// }
```

### 支持多层嵌套

```java
@Data
public class ComplexBean {
    @SetValue("level1.level2.level3")
    private String deepValue;
}

// 自动创建多层嵌套结构
// {
//   "level1": {
//     "level2": {
//       "level3": "value"
//     }
//   }
// }
```

---

## 🔥 一句话总结

**@SetValue 让你用"扁平的代码结构"操作"嵌套的数据结构"，兼顾开发体验和存储灵活性！**

---

<div align="center">

**代码里扁平，存储时嵌套，两全其美！**

[返回首页](../README.md) | [功能导航](./features.md) | [完整文档](./jsonmap-jsonlist.md)

</div>
