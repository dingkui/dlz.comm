# 🧠 JSONMap 工具集：Java 数据处理的终极武器

> **一句话总结**：让 Java 拥有 JavaScript 般的数据处理能力，告别强转地狱，告别空指针噩梦。

[![JDK](https://img.shields.io/badge/JDK-8+-green.svg)](https://www.oracle.com/java/)

---

## 📚 文档导航

- **快速入门**：本文档（核心概念和快速上手）
- **功能导航**：[功能导航 - 从入门到精通](./features.md)（特色功能、实用功能、彩蛋功能完整索引）
- **完整指南**：[JSONMap & JSONList 详细文档](./jsonmap-jsonlist.md)（所有 API 和实战场景）
- **应用场景**：[应用场景与痛点分析](./scenarios.md) | [@SetValue 注解指南](./bean-mapping.md)
- **工具类**：[JacksonUtil](./jacksonutil.md) | [DateUtil](./dateutil.md) | [StringUtils](./stringutils.md) | [ValUtil](./valutil.md) | [Cache](./cache.md)

---
## 🎬 3 秒感受降维打击
### 😫 曾经的噩梦
```java
// 为了获取 user.profile.tags[0]，你需要写：
Map<String, Object> data = getData();
if (data != null) {
    Object userObj = data.get("user");
    if (userObj instanceof Map) {
        Map<String, Object> user = (Map<String, Object>) userObj;
        Object profileObj = user.get("profile");
        if (profileObj instanceof Map) {
            Map<String, Object> profile = (Map<String, Object>) profileObj;
            Object tagsObj = profile.get("tags");
            if (tagsObj instanceof List) {
                List<String> tags = (List<String>) tagsObj;
                if (!tags.isEmpty()) {
                    String tag = tags.get(0); // 终于拿到了...
                }
            }
        }
    }
}
```
### 😎 现在的优雅
```java
// 一行代码，搞定一切
String tag = new JSONMap(data).getStr("user.profile.tags[0]");
```
---
## 📦 三大神器
| 工具 | 定位 | 核心能力 |
|------|------|----------|
| **JSONMap** | JSON 对象处理 | 深层路径读写、自动类型转换、智能构建 |
| **JSONList** | JSON 数组处理 | 负索引访问、类型安全遍历、链式操作 |
| **ValUtil** | 值转换工具 | 万能类型转换、空值安全、日期处理 |
---
## 🗡️ JSONMap：数据处理的瑞士军刀
### ✨ 核心亮点
#### 1️⃣ 深层路径直达（独创）
**无需判空，无需强转，一步到位。**
```java
JSONMap data = new JSONMap("{\"user\":{\"profile\":{\"name\":\"张三\",\"age\":25}}}");
// ✅ 点号穿透嵌套对象
data.getStr("user.profile.name");     // → "张三"
data.getInt("user.profile.age");      // → 25
data.getMap("user.profile");          // → {"name":"张三","age":25}
// ✅ 支持数组下标
data.getStr("user.tags[0]");          // → 第一个标签
data.getStr("user.tags[-1]");         // → 最后一个标签（负索引！）
data.getStr("user.tags[-2]");         // → 倒数第二个标签
// ✅ 混合路径，任意组合
data.getStr("orders[0].items[-1].product.name");
```
#### 2️⃣ 意念构建（独创）
**想构造 `{"a":{"b":{"c":1}}}`？别再 new 三个 HashMap 了！**
```java
// ✅ 自动创建所有中间层级
JSONMap config = new JSONMap()
    .set("server.port", 8080)
    .set("server.host", "localhost")
    .set("db.master.url", "jdbc:mysql://...")
    .set("db.master.username", "root");
// 结果：
// {
//   "server": {"port": 8080, "host": "localhost"},
//   "db": {"master": {"url": "jdbc:mysql://...", "username": "root"}}
// }
```
#### 3️⃣ 智能数组追加
```java
JSONMap config = new JSONMap()
    .add("whitelist", "192.168.1.1")    // 自动创建数组
    .add("whitelist", "192.168.1.2")    // 自动追加到数组
    .add("whitelist", "192.168.1.3");
// 结果：{"whitelist": ["192.168.1.1", "192.168.1.2", "192.168.1.3"]}
```
#### 4️⃣ 全能类型转换
**不管数据源是什么类型，你想要什么就给你什么。**
```java
JSONMap data = new JSONMap();
data.put("score", "99.5");      // 存的是字符串
data.put("count", 100);         // 存的是整数
data.put("rate", 0.85);         // 存的是小数
// 随心所欲地取
data.getInt("score");           // → 99（自动转换并截断）
data.getDouble("score");        // → 99.5
data.getBigDecimal("score");    // → 99.5（精确计算用这个）
data.getStr("count");           // → "100"
data.getLong("count");          // → 100L
data.getFloat("rate");          // → 0.85f
```
---
### 🔧 构造方式大全
```java
// 1️⃣ 空对象
JSONMap map = new JSONMap();
// 2️⃣ JSON 字符串（标准格式）
JSONMap map = new JSONMap("{\"name\":\"张三\",\"age\":25}");
// 3️⃣ JSON 字符串（简化格式，key 可不加引号）
JSONMap map = new JSONMap("{name:'张三',age:25}");
// 4️⃣ JSON 字符串（支持注释！）
JSONMap map = new JSONMap("{\n" +
    "  name: '张三',  // 用户姓名\n" +
    "  age: 25        // 用户年龄\n" +
    "}");
// 5️⃣ 键值对构造（推荐！简洁直观）
JSONMap map = new JSONMap("name", "张三", "age", 25, "city", "北京");
// 6️⃣ 从 Map 转换
Map<String, Object> hashMap = new HashMap<>();
JSONMap map = new JSONMap(hashMap);
// 7️⃣ 从 Java Bean 转换
User user = new User();
JSONMap map = new JSONMap(user);
```
---
### 📖 API 速查表
#### 读取方法
| 方法 | 说明 | 示例 |
|------|------|------|
| `getStr(key)` | 获取字符串 | `map.getStr("user.name")` |
| `getInt(key)` | 获取整数 | `map.getInt("user.age")` |
| `getLong(key)` | 获取长整数 | `map.getLong("order.id")` |
| `getDouble(key)` | 获取双精度 | `map.getDouble("price")` |
| `getFloat(key)` | 获取单精度 | `map.getFloat("rate")` |
| `getBigDecimal(key)` | 获取精确数值 | `map.getBigDecimal("amount")` |
| `getBoolean(key)` | 获取布尔值 | `map.getBoolean("active")` |
| `getMap(key)` | 获取子对象 | `map.getMap("user.profile")` |
| `getList(key)` | 获取数组 | `map.getList("user.tags")` |
| `getDate(key)` | 获取日期 | `map.getDate("createTime")` |
#### 带默认值的读取
```java
map.getStr("name", "未知");           // 不存在时返回 "未知"
map.getInt("age", 0);                 // 不存在时返回 0
map.getBoolean("active", false);      // 不存在时返回 false
```
#### 写入方法
| 方法 | 说明 | 示例 |
|------|------|------|
| `put(key, value)` | 普通写入（key 不解析） | `map.put("a.b", 1)` → `{"a.b": 1}` |
| `set(key, value)` | 深层写入（自动建层级） | `map.set("a.b", 1)` → `{"a": {"b": 1}}` |
| `add(key, value)` | 数组追加（自动建数组） | `map.add("tags", "vip")` |
#### 类型转换
```java
// 转换为 Java Bean
User user = map.as(User.class);
// 转换为指定类型的 Map
Map<String, String> strMap = map.asMap(String.class);
```
---
### 💡 实战场景
#### 场景 1：API 响应解析
```java
JSONMap response = httpClient.getAsJSONMap("/api/user/info");
// 直接深层取值，不用层层判空
String avatar = response.getStr("data.user.profile.avatar", "default.png");
List<String> roles = response.getList("data.user.roles", String.class);
```
#### 场景 2：动态构建请求体
```java
JSONMap request = new JSONMap()
    .set("header.version", "1.0")
    .set("header.timestamp", System.currentTimeMillis())
    .set("body.user.name", userName)
    .set("body.user.email", email)
    .add("body.user.tags", "vip")
    .add("body.user.tags", "active");
httpClient.post("/api/register", request.toString());
```
#### 场景 3：配置文件处理
```java
JSONMap config = new JSONMap(FileUtils.readFileToString("config.json"));
int port = config.getInt("server.port", 8080);
String dbUrl = config.getStr("database.master.url");
List<String> whitelist = config.getList("security.whitelist", String.class);
```
---
## 📋 JSONList：数组处理专家
### ✨ 核心亮点
#### 1️⃣ 负索引支持（Python 风格）
```java
JSONList list = new JSONList("[1, 2, 3, 4, 5]");
list.getInt(0);    // → 1（第一个）
list.getInt(-1);   // → 5（最后一个）
list.getInt(-2);   // → 4（倒数第二个）
```
#### 2️⃣ 多种构造方式
```java
// JSON 字符串
JSONList list = new JSONList("[1, 2, 3]");
// 逗号分隔字符串（自动拆分！）
JSONList list = new JSONList("苹果,香蕉,橙子");
// 数组
JSONList list = new JSONList(new String[]{"a", "b", "c"});
// 集合
JSONList list = new JSONList(Arrays.asList(1, 2, 3));
// 带类型转换
JSONList list = new JSONList("1,2,3", Integer.class);
```
#### 3️⃣ 链式添加
```java
JSONList list = new JSONList()
    .adds("元素1")
    .adds("元素2")
    .adds("元素3");
```
#### 4️⃣ 类型安全访问
```java
JSONList list = new JSONList("[{\"name\":\"张三\"}, {\"name\":\"李四\"}]");
// 获取子对象
JSONMap first = list.getMap(0);
String name = first.getStr("name");
// 或者直接用路径
String name = list.getInt("[-1].name");  // 最后一个人的名字
```
---
### 📖 API 速查表
| 方法 | 说明 | 示例 |
|------|------|------|
| `getStr(index)` | 获取字符串 | `list.getStr(0)` |
| `getInt(index)` | 获取整数 | `list.getInt(-1)` |
| `getMap(index)` | 获取对象 | `list.getMap(0)` |
| `getList(index)` | 获取嵌套数组 | `list.getList(0)` |
| `adds(value)` | 链式添加 | `list.adds(1).adds(2)` |
| `asList(Class)` | 转换为类型列表 | `list.asList(User.class)` |
---
## 🔧 ValUtil：万能类型转换器
### ✨ 设计理念
> **不管输入什么，你想要什么类型，就给你什么类型。空值安全，绝不抛异常。**
### 📖 转换方法大全
#### 数值转换
```java
// 转 Integer
ValUtil.toInt("123");           // → 123
ValUtil.toInt(123.45);          // → 123（截断小数）
ValUtil.toInt(null);            // → null（安全返回）
ValUtil.toInt(null, 0);         // → 0（使用默认值）
// 转 Long
ValUtil.toLong("123");          // → 123L
ValUtil.toLong(null,0l);       // → 0L
// 转 Double
ValUtil.toDouble("123.45");     // → 123.45
ValUtil.toDouble(null,0.0);     // → 0.0
// 转 BigDecimal（金额计算必用）
ValUtil.toBigDecimal("99.99");  // → BigDecimal(99.99)
```
#### 布尔转换
```java
// 以下返回 false
ValUtil.toBoolean(null);
ValUtil.toBoolean("");
ValUtil.toBoolean("false");
ValUtil.toBoolean("FALSE");
ValUtil.toBoolean("0");
ValUtil.toBoolean(0);
// 其他都返回 true
ValUtil.toBoolean("true");
ValUtil.toBoolean("1");
ValUtil.toBoolean(1);
ValUtil.toBoolean("任意非空字符串");
```
#### 字符串转换
```java
ValUtil.toStr(123);             // → "123"
ValUtil.toStr(null);            // → null
ValUtil.toStr(null, "默认");    // → "默认"
// 处理 "null" 字符串
```
#### 日期转换
```java
// 字符串 → Date
ValUtil.toDate("2023-01-01");
ValUtil.toDate("2023-01-01 12:30:45");
// 时间戳 → Date
ValUtil.toDate(1672531200000L);
// Date → 字符串
ValUtil.toDateStr(new Date());                    // → "2023-01-01 12:30:45"
ValUtil.toDateStr(new Date(), "yyyy-MM-dd");      // → "2023-01-01"
// LocalDateTime 支持
ValUtil.toLocalDateTime("2023-01-01 12:30:45");
ValUtil.toLocalDateTime(new Date());
```
#### 集合转换
```java
// JSON 字符串 → List
ValUtil.toList("[1, 2, 3]");                      // → JSONList
ValUtil.toList("[1, 2, 3]", Integer.class);       // → List<Integer>
// 逗号分隔 → List
ValUtil.toList("1,2,3", Integer.class);           // → [1, 2, 3]
// 转数组
ValUtil.toArray("[1, 2, 3]", Integer.class);      // → Integer[]
ValUtil.toArray("a,b,c", String.class);           // → String[]
```
#### 空值判断
```java
ValUtil.isEmpty(null);              // → true
ValUtil.isEmpty("");                // → true
ValUtil.isEmpty(new ArrayList());  // → true
ValUtil.isEmpty(new HashMap());    // → true
ValUtil.isEmpty(new String[0]);    // → true
ValUtil.isEmpty("有内容");          // → false
ValUtil.isEmpty(Arrays.asList(1)); // → false
```
---
## 🆚 与其他方案对比
| 操作 | 原生 Java | FastJSON | Jackson | **JSONMap** |
|------|-----------|----------|---------|-------------|
| 深层取值 | 10+ 行代码 | 链式 get | 链式 get | **一行路径** |
| 空值安全 | 需手动判断 | 部分 | 部分 | **完全安全** |
| 类型转换 | 手动强转 | 需指定类型 | 需指定类型 | **自动推断** |
| 构建结构 | 多层 put | 多层 put | ObjectNode | **路径 set** |
| 负索引 | ❌ | ❌ | ❌ | **✅ 支持** |
| 带注释 JSON | ❌ | ❌ | 需配置 | **✅ 原生支持** |
---
## 🎯 最佳实践
### ✅ 推荐写法
```java
// 1. 使用路径表达式
String name = json.getStr("user.profile.name");
// 2. 提供默认值
int age = json.getInt("user.age", 0);
// 3. 链式构建
JSONMap request = new JSONMap()
    .set("header.token", token)
    .set("body.data", data);
// 4. 类型转换
User user = json.as(User.class);
```
### ❌ 不推荐写法
```java
// 1. 不要手动判空（JSONMap 已处理）
if (json.get("user") != null) {
    if (json.getMap("user").get("name") != null) {
        // ...
    }
}
// 2. 不要手动强转
(Map<String, Object>) json.get("user");  // ❌
json.getMap("user");                      // ✅
```

---

## 📚 更多资源

### 详细文档
- [📘 JSONMap & JSONList 完整指南](./jsonmap-jsonlist.md) - 所有 API 和实战场景
- [🔧 JacksonUtil 文档](./jacksonutil.md) - JSON 序列化与路径取值
- [📅 DateUtil 文档](./dateutil.md) - 日期时间处理
- [🔤 StringUtils 文档](./stringutils.md) - 字符串处理
- [🔄 ValUtil 文档](./valutil.md) - 类型转换工具
- [💾 Cache 文档](./cache.md) - 缓存工具

### 快速链接
- [返回首页](../README.md)
- [GitHub 仓库](https://github.com/dlz-xyz/dlz.comm)
- [问题反馈](https://github.com/dlz-xyz/dlz.comm/issues)

---

## 💡 常见问题

### Q: JSONMap 和普通 HashMap 有什么区别？
A: JSONMap 继承自 LinkedHashMap，完全兼容 Map 接口。额外提供深层路径访问、自动类型转换、链式构建等功能。

### Q: 路径访问失败会抛异常吗？
A: 不会。路径中任意一环为 null，直接返回 null，绝无 NPE。

### Q: 性能怎么样？
A: 底层就是 LinkedHashMap，性能与原生 Map 一致。路径解析有缓存优化。

### Q: 支持哪些 JSON 格式？
A: 支持标准 JSON、简化 JSON（key 不加引号）、带注释的 JSON。

### Q: 可以和 Jackson/FastJSON 一起用吗？
A: 可以。JSONMap 基于 Jackson 构建，与其他 JSON 库无冲突。

### Q: 线程安全吗？
A: JSONMap 和 JSONList 本身不是线程安全的。多线程环境下需要外部同步，或使用 `Collections.synchronizedMap()` 包装。

---

<div align="center">
**简单的事情简单做，复杂的事情也能简单做。**
如果觉得有帮助，请点个 ⭐ Star 支持一下！

[返回首页](../README.md)
</div>