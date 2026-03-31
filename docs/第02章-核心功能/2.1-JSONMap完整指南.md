# JSONMap & JSONList - JSON 数据处理核心工具

[返回首页](../README.md) / [JSONMap & JSONList](./jsonmap-jsonlist.md)

---

## 📖 目录

- [简介](#简介)
- [JSONMap - JSON 对象处理](#jsonmap---json-对象处理)
  - [核心特性](#核心特性)
  - [构造方式](#构造方式)
  - [深层路径访问](#深层路径访问)
  - [层级构建](#层级构建)
  - [类型转换](#类型转换)
  - [API 参考](#api-参考)
- [JSONList - JSON 数组处理](#jsonlist---json-数组处理)
  - [核心特性](#核心特性-1)
  - [构造方式](#构造方式-1)
  - [负索引访问](#负索引访问)
  - [API 参考](#api-参考-1)
- [实战场景](#实战场景)
- [最佳实践](#最佳实践)

---

## 简介

`JSONMap` 和 `JSONList` 是 dlz.comm 工具包的核心组件，提供了强大的 JSON 数据处理能力。它们不仅仅是简单的 Map 和 List 包装，而是集成了深层路径访问、智能类型转换、链式构建等多种高级特性。

### 设计理念

> **让 Java 拥有 JavaScript 般的数据处理能力，告别强转地狱，告别空指针噩梦。**

### 核心优势

- 🎯 **深层路径直达**：一行代码穿透任意层级，无需判空
- 🏗️ **智能构建**：自动创建中间层级，链式操作
- 🧬 **全能转换**：自动类型转换，容错性极强
- 🔗 **无缝集成**：继承自标准集合类，完全兼容现有代码

---

## JSONMap - JSON 对象处理

### 核心特性

#### 1. 深层路径访问（独创）

**无需判空，无需强转，一步到位。**

```java
JSONMap data = new JSONMap("{\"user\":{\"profile\":{\"name\":\"张三\",\"age\":25}}}");

// ✅ 点号穿透嵌套对象
String name = data.getStr("user.profile.name");     // → "张三"
Integer age = data.getInt("user.profile.age");      // → 25
JSONMap profile = data.getMap("user.profile");      // → {"name":"张三","age":25}

// ✅ 支持数组下标
String firstTag = data.getStr("user.tags[0]");      // → 第一个标签
String lastTag = data.getStr("user.tags[-1]");      // → 最后一个标签（负索引！）

// ✅ 混合路径，任意组合
String productName = data.getStr("orders[0].items[-1].product.name");
```

**传统方式 vs JSONMap：**

```java
// ❌ 传统方式：20 行代码，层层判空
Map<String, Object> data = getData();
String city = null;
if (data != null) {
    Object userObj = data.get("user");
    if (userObj instanceof Map) {
        Map<String, Object> user = (Map<String, Object>) userObj;
        Object profileObj = user.get("profile");
        if (profileObj instanceof Map) {
            Map<String, Object> profile = (Map<String, Object>) profileObj;
            Object addressesObj = profile.get("addresses");
            if (addressesObj instanceof List) {
                List<Object> addresses = (List<Object>) addressesObj;
                if (!addresses.isEmpty()) {
                    Object firstAddr = addresses.get(0);
                    if (firstAddr instanceof Map) {
                        city = (String) ((Map) firstAddr).get("city");
                    }
                }
            }
        }
    }
}

// ✅ JSONMap 方式：1 行代码
String city = new JSONMap(data).getStr("user.profile.addresses[0].city");
```

#### 2. 智能层级构建（独创）

**想构造复杂嵌套结构？自动铺路，无需手动创建中间对象。**

```java
// ✅ 自动创建所有中间层级
JSONMap config = new JSONMap()
    .set("server.port", 8080)
    .set("server.host", "localhost")
    .set("db.master.url", "jdbc:mysql://...")
    .set("db.master.username", "root")
    .set("db.slave[0].url", "jdbc:mysql://slave1")
    .set("db.slave[1].url", "jdbc:mysql://slave2");

// 结果：
// {
//   "server": {"port": 8080, "host": "localhost"},
//   "db": {
//     "master": {"url": "jdbc:mysql://...", "username": "root"},
//     "slave": [
//       {"url": "jdbc:mysql://slave1"},
//       {"url": "jdbc:mysql://slave2"}
//     ]
//   }
// }
```

**支持的路径格式：**

- `a.b.c.d` - 多级对象嵌套
- `arr[0]` - 数组索引
- `arr[5]` - 自动补齐中间元素（填充 null）
- `arr[-1]` - 负数索引（倒数第一个）
- `a[0].b[1].c` - 混合嵌套
- `a[0][1].c` - 多维数组

```java
JSONMap json = new JSONMap();

// 普通嵌套
json.set("user.profile.name", "张三");

// 数组索引
json.set("users[0].name", "张三");
json.set("users[1].name", "李四");

// 多维数组
json.set("matrix[0][0]", 1);
json.set("matrix[0][1]", 2);
json.set("matrix[1][0]", 3);

// 混合路径
json.set("config.servers[0].ports[0]", 8080);
json.set("config.servers[0].ports[1]", 8081);

// 负数索引（先有数据才能用）
json.set("users[0].name", "张三");
json.set("users[1].name", "李四");
json.set("users[-1].name", "王五");  // 修改最后一个
```

#### 3. 智能数组追加

```java
JSONMap config = new JSONMap()
    .add("whitelist", "192.168.1.1")    // 自动创建数组
    .add("whitelist", "192.168.1.2")    // 自动追加到数组
    .add("whitelist", "192.168.1.3");

// 结果：{"whitelist": ["192.168.1.1", "192.168.1.2", "192.168.1.3"]}
```

#### 4. 全能类型转换

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

### 构造方式

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

// 8️⃣ 静态工厂方法
JSONMap map = JSONMap.createJsonMap(data);
```

---

### 深层路径访问

#### 路径语法

- `.` - 访问对象属性
- `[n]` - 访问数组索引 n 的元素
- `[-n]` - 访问倒数第 n 个元素
- 可以任意组合

#### 示例

```json
{
  "user": {
    "name": "张三",
    "profile": {
      "email": "zhangsan@example.com",
      "addresses": [
        {"city": "北京", "street": "朝阳路"},
        {"city": "上海", "street": "南京路"}
      ]
    },
    "tags": ["vip", "active", "verified"]
  }
}
```

```java
JSONMap data = new JSONMap(jsonString);

// 简单路径
data.getStr("user.name");                           // → "张三"

// 嵌套路径
data.getStr("user.profile.email");                  // → "zhangsan@example.com"

// 数组索引
data.getStr("user.profile.addresses[0].city");      // → "北京"
data.getStr("user.profile.addresses[1].street");    // → "南京路"

// 负数索引
data.getStr("user.tags[-1]");                       // → "verified"（最后一个）
data.getStr("user.tags[-2]");                       // → "active"（倒数第二个）

// 获取子对象
JSONMap profile = data.getMap("user.profile");
JSONList addresses = data.getList("user.profile.addresses");
```

---

### 层级构建

#### set() 方法 - 深层设置

```java
JSONMap json = new JSONMap();

// 自动创建中间层级
json.set("a.b.c", "value");
// 结果：{"a":{"b":{"c":"value"}}}

// 数组自动创建
json.set("users[0].name", "张三");
json.set("users[1].name", "李四");
// 结果：{"users":[{"name":"张三"},{"name":"李四"}]}

// 数组自动补齐
json.set("arr[5]", "value");
// 结果：{"arr":[null,null,null,null,null,"value"]}

// 多维数组
json.set("matrix[0][0]", 1);
json.set("matrix[0][1]", 2);
// 结果：{"matrix":[[1,2]]}
```

#### put() 方法 - 普通设置

```java
JSONMap json = new JSONMap();

// key 不解析，直接作为键名
json.put("a.b", 1);
// 结果：{"a.b":1}  （注意：不是嵌套对象）

json.put("arr[0]", 1);
// 结果：{"arr[0]":1}  （注意：不是数组）
```

#### add() 方法 - 数组追加

```java
JSONMap json = new JSONMap();

// 自动创建数组并追加
json.add("tags", "tag1");
json.add("tags", "tag2");
json.add("tags", "tag3");
// 结果：{"tags":["tag1","tag2","tag3"]}

// 追加集合
json.add("numbers", Arrays.asList(1, 2, 3));
json.add("numbers", Arrays.asList(4, 5));
// 结果：{"numbers":[1,2,3,4,5]}
```

---

### 类型转换

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
| `getArray(key, Class)` | 获取数组 | `map.getArray("ids", Integer.class)` |

#### 带默认值的读取

```java
map.getStr("name", "未知");           // 不存在时返回 "未知"
map.getInt("age", 0);                 // 不存在时返回 0
map.getBoolean("active", false);      // 不存在时返回 false
```

#### 对象转换

```java
// 转换为 Java Bean
User user = map.as(User.class);

// 转换为指定类型的 Map
Map<String, String> strMap = map.asMap(String.class);
Map<String, JSONMap> mapMap = map.asMap();
Map<String, JSONList> listMap = map.asMapList();
```

---

### API 参考

#### 构造方法

```java
JSONMap()                                    // 空对象
JSONMap(Object obj)                          // 从对象构造
JSONMap(CharSequence json)                   // 从 JSON 字符串构造
JSONMap(String key, Object val, Object...)   // 键值对构造
```

#### 读取方法

```java
// 基本类型
String getStr(String key)
String getStr(String key, String defaultValue)
Integer getInt(String key)
Integer getInt(String key, Integer defaultValue)
Long getLong(String key)
Double getDouble(String key)
Float getFloat(String key)
BigDecimal getBigDecimal(String key)
Boolean getBoolean(String key)
Date getDate(String key)

// 复杂类型
JSONMap getMap(String key)
JSONList getList(String key)
<T> List<T> getList(String key, Class<T> clazz)
<T> T[] getArray(String key, Class<T> clazz)
<T> T getObj(String key, Class<T> clazz)

// 对象转换
<T> T as(Class<T> clazz)
<T> Map<String, T> asMap(Class<T> clazz)
Map<String, JSONMap> asMap()
Map<String, JSONList> asMapList()
```

#### 写入方法

```java
JSONMap put(String key, Object value)              // 普通写入
JSONMap set(String key, Object value)              // 深层写入
JSONMap add(String key, Object obj)                // 数组追加
JSONMap add(String key, Object obj, int mode)      // 数组追加（指定模式）
JSONMap add2List(String key, Object obj)           // 添加到列表
```

#### 工具方法

```java
JSONMap clearEmptyProp()                           // 清除空属性
String toString()                                  // 转 JSON 字符串
static JSONMap createJsonMap(Object json)          // 静态工厂方法
```

---

## JSONList - JSON 数组处理

### 核心特性

#### 1. 负索引支持（Python 风格）

```java
JSONList list = new JSONList("[1, 2, 3, 4, 5]");

list.getInt(0);    // → 1（第一个）
list.getInt(-1);   // → 5（最后一个）
list.getInt(-2);   // → 4（倒数第二个）
```

#### 2. 类型安全访问

```java
JSONList list = new JSONList("[{\"name\":\"张三\"}, {\"name\":\"李四\"}]");

// 获取子对象
JSONMap first = list.getMap(0);
String name = first.getStr("name");

// 直接用路径
String lastName = list.getStr("[-1].name");  // 最后一个人的名字
```

#### 3. 链式操作

```java
JSONList list = new JSONList()
    .adds("元素1")
    .adds("元素2")
    .adds("元素3");
```

---

### 构造方式

```java
// 1️⃣ 空列表
JSONList list = new JSONList();

// 2️⃣ JSON 字符串
JSONList list = new JSONList("[1, 2, 3]");

// 3️⃣ 逗号分隔字符串（自动拆分！）
JSONList list = new JSONList("苹果,香蕉,橙子");

// 4️⃣ 数组
JSONList list = new JSONList(new String[]{"a", "b", "c"});

// 5️⃣ 集合
JSONList list = new JSONList(Arrays.asList(1, 2, 3));

// 6️⃣ 带类型转换
JSONList list = new JSONList("1,2,3", Integer.class);

// 7️⃣ 指定初始容量
JSONList list = new JSONList(100);
```

---

### 负索引访问

```java
JSONList list = new JSONList("[10, 20, 30, 40, 50]");

// 正向索引
list.getInt(0);     // → 10（第一个）
list.getInt(1);     // → 20（第二个）
list.getInt(4);     // → 50（第五个）

// 负向索引
list.getInt(-1);    // → 50（最后一个）
list.getInt(-2);    // → 40（倒数第二个）
list.getInt(-5);    // → 10（倒数第五个）
```

---

### API 参考

#### 构造方法

```java
JSONList()                                   // 空列表
JSONList(int initialCapacity)                // 指定初始容量
JSONList(Object obj)                         // 从对象构造
JSONList(Object obj, Class<?> clazz)         // 从对象构造（指定类型）
JSONList(Collection<?> collection)           // 从集合构造
JSONList(Object[] objs)                      // 从数组构造
```

#### 读取方法

```java
// 基本类型
String getStr(int index)
String getStr(int index, String defaultValue)
Integer getInt(int index)
Long getLong(int index)
Double getDouble(int index)
Float getFloat(int index)
BigDecimal getBigDecimal(int index)
Boolean getBoolean(int index)
Date getDate(int index)

// 复杂类型
JSONMap getMap(int index)
JSONList getList(int index)
<T> T getObj(int index, Class<T> clazz)

// 列表转换
<T> List<T> asList(Class<T> clazz)
```

#### 写入方法

```java
JSONList adds(Object obj)                    // 链式添加
JSONList set(String key, Object value)       // 深层设置
```

---

## 实战场景

### 场景 1：API 响应解析

```java
// 调用第三方 API
String response = httpClient.get("/api/user/info");
JSONMap data = new JSONMap(response);

// 直接深层取值，不用层层判空
String avatar = data.getStr("data.user.profile.avatar", "default.png");
List<String> roles = data.getList("data.user.roles", String.class);
Integer age = data.getInt("data.user.age", 0);

// 获取嵌套数组
JSONList orders = data.getList("data.user.orders");
for (int i = 0; i < orders.size(); i++) {
    String orderId = orders.getStr(i + ".id");
    Double amount = orders.getDouble(i + ".amount");
    System.out.println("订单: " + orderId + ", 金额: " + amount);
}
```

### 场景 2：动态构建请求体

```java
// 构建复杂的请求体
JSONMap request = new JSONMap()
    .set("header.version", "1.0")
    .set("header.timestamp", System.currentTimeMillis())
    .set("header.sign", generateSign())
    .set("body.user.name", userName)
    .set("body.user.email", email)
    .set("body.user.phone", phone)
    .add("body.user.tags", "vip")
    .add("body.user.tags", "active")
    .set("body.config.timeout", 30)
    .set("body.config.retry", 3);

// 发送请求
httpClient.post("/api/register", request.toString());
```

### 场景 3：配置文件处理

```java
// 读取配置文件
String configJson = FileUtils.readFileToString("config.json");
JSONMap config = new JSONMap(configJson);

// 读取配置
int port = config.getInt("server.port", 8080);
String host = config.getStr("server.host", "localhost");
String dbUrl = config.getStr("database.master.url");
String dbUser = config.getStr("database.master.username");
List<String> whitelist = config.getList("security.whitelist", String.class);

// 读取数组配置
JSONList servers = config.getList("servers");
for (int i = 0; i < servers.size(); i++) {
    String serverHost = servers.getStr(i + ".host");
    Integer serverPort = servers.getInt(i + ".port");
    System.out.println("Server: " + serverHost + ":" + serverPort);
}
```

### 场景 4：数据转换

```java
// 从数据库查询结果转换
List<Map<String, Object>> dbResults = jdbcTemplate.queryForList(sql);
JSONList users = new JSONList(dbResults);

// 转换为 Bean 列表
List<User> userList = users.asList(User.class);

// 或者逐个处理
for (int i = 0; i < users.size(); i++) {
    JSONMap user = users.getMap(i);
    String name = user.getStr("name");
    Integer age = user.getInt("age");
    // 处理...
}
```

### 场景 5：表单数据处理

```java
// 接收前端表单数据
@PostMapping("/submit")
public Result submit(@RequestBody String formData) {
    JSONMap form = new JSONMap(formData);
    
    // 即使前端传的类型不对，也能自动纠正
    // 前端传 {"age": "25"}（字符串），自动转成 Integer
    String name = form.getStr("name");
    Integer age = form.getInt("age");  // 自动从字符串转整数
    Boolean active = form.getBoolean("active");
    
    // 转换为 Bean
    User user = form.as(User.class);
    
    // 保存...
    return Result.success();
}
```

### 场景 6：日志数据分析

```java
// 解析日志 JSON
String logLine = "{\"timestamp\":1234567890,\"level\":\"ERROR\",\"message\":\"...\",\"context\":{\"user\":{\"id\":123}}}";
JSONMap log = new JSONMap(logLine);

// 提取关键信息
Long timestamp = log.getLong("timestamp");
String level = log.getStr("level");
String message = log.getStr("message");
Integer userId = log.getInt("context.user.id");

// 格式化输出
System.out.println(String.format("[%s] %s - User:%d - %s", 
    DateUtil.format(new Date(timestamp)), level, userId, message));
```

---

## 最佳实践

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

// 5. 使用负索引
String lastItem = list.getStr(-1);
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

// 3. 不要混用 put 和 set
json.put("a.b", 1);   // 这会创建键名为 "a.b" 的条目
json.set("a.b", 1);   // 这会创建嵌套对象 {"a":{"b":1}}

// 4. 不要在循环中频繁创建 JSONMap
for (String item : items) {
    new JSONMap(item);  // ❌ 性能差
}
// 应该复用或批量处理
```

### 性能优化建议

1. **复用对象**：对于频繁操作的数据，复用 JSONMap 对象
2. **批量操作**：使用 `putAll()` 而不是多次 `put()`
3. **选择合适方法**：明确知道类型时，直接使用对应的 get 方法
4. **避免深拷贝**：`getMap()` 返回的是引用，修改会影响原对象

### 线程安全

- `JSONMap` 和 `JSONList` 本身不是线程安全的
- 多线程环境下需要外部同步
- 或使用 `Collections.synchronizedMap()` 包装

---

## 常见问题

### Q: JSONMap 和普通 HashMap 有什么区别？

A: JSONMap 继承自 HashMap，完全兼容 Map 接口。额外提供：
- 深层路径访问
- 自动类型转换
- 链式构建
- 智能数组处理

### Q: 路径访问失败会抛异常吗？

A: 不会。路径中任意一环为 null，直接返回 null，绝无 NPE。

### Q: 性能怎么样？

A: 底层就是 HashMap，性能与原生 Map 一致。路径解析有缓存优化。

### Q: 支持哪些 JSON 格式？

A: 支持标准 JSON、简化 JSON（key 不加引号）、带注释的 JSON。

### Q: 可以和 Jackson/FastJSON 一起用吗？

A: 可以。JSONMap 基于 Jackson 构建，与其他 JSON 库无冲突。

---

<div align="center">

**简单的事情简单做，复杂的事情也能简单做。**

如果觉得有帮助，请点个 ⭐ Star 支持一下！

[返回首页](../README.md)

</div>
