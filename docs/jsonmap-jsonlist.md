# JSONMap/JSONList - JSON数据结构工具

[返回首页](../README.md) / [JSON工具](./jsonmap-jsonlist.md)

## 简介

`JSONMap` 和 `JSONList` 是两个核心的数据结构类，分别继承自 `HashMap<String, Object>` 和 `ArrayList<Object>`，提供了便捷的 JSON 数据操作功能。

## JSONMap

### 功能特点

- 继承自 `HashMap<String, Object>`
- 实现了 `IUniversalVals` 接口
- 提供便捷的数据访问方法
- 支持层次化数据设置
- 支持动态数据类型转换

### 构造方法

```java
// 无参构造
JSONMap map1 = new JSONMap();

// 从对象构造
JSONMap map2 = new JSONMap(obj);

// 从 JSON 字符串构造
JSONMap map3 = new JSONMap("{\"name\":\"John\",\"age\":30}");

// 从键值对构造
JSONMap map4 = new JSONMap("name", "John", "age", 30);
```

### 主要方法

```java
// 基本操作
map.put("key", "value");

// 类型安全的获取
String name = map.getStr("name");
Integer age = map.getInt("age");
Double score = map.getDouble("score");
Boolean flag = map.getBoolean("flag");
Date birth = map.getDate("birth");

// 层次化数据设置
map.set("user.profile.name", "John");
// 等价于: {"user": {"profile": {"name": "John"}}}

// 添加数据
map.add("tags", "tag1");
map.add("tags", "tag2", 1); // 指定合并方式

// 转换为其他类型
Map<String, Person> personMap = map.asMap(Person.class);
```

### 使用示例

```java
// 创建 JSONMap
JSONMap user = new JSONMap();
user.put("name", "John")
     .put("age", 30)
     .put("active", true);

// 类型安全获取
String name = user.getStr("name");           // "John"
int age = user.getInt("age");                // 30
boolean active = user.getBoolean("active");  // true

// 层次化数据
user.set("address.city", "Beijing");
user.set("contact.email", "john@example.com");
// 结果: {"name":"John","age":30,"active":true,"address":{"city":"Beijing"},"contact":{"email":"john@example.com"}}

// 从 JSON 字符串创建
String json = "{\"name\":\"Jane\",\"age\":25,\"scores\":[85,90,78]}";
JSONMap student = new JSONMap(json);
List<Integer> scores = student.getList("scores", Integer.class);
```

## JSONList

### 功能特点

- 继承自 `ArrayList<Object>`
- 实现了 `IUniversalVals` 和 `IUniversalVals4List` 接口
- 提供便捷的列表操作方法
- 支持类型安全的元素访问

### 构造方法

```java
// 无参构造
JSONList list1 = new JSONList();

// 从集合构造
JSONList list2 = new JSONList(collection);

// 从数组构造
JSONList list3 = new JSONList(array);

// 从对象构造
JSONList list4 = new JSONList(obj, ItemClass.class);
```

### 主要方法

```java
// 基本操作
list.add("item");
list.add(123);

// 类型安全获取
JSONMap item = list.getMap(0);
String str = list.getStr(1);
Integer num = list.getInt(2);

// 转换为其他类型
List<Person> personList = list.asList(Person.class);
```

### 使用示例

```java
// 从数组创建
String[] items = {"apple", "banana", "orange"};
JSONList fruits = new JSONList(items);

// 从 JSON 字符串创建
String jsonArray = "[{\"name\":\"John\",\"age\":30},{\"name\":\"Jane\",\"age\":25}]";
JSONList people = new JSONList(jsonArray);

// 访问元素
JSONMap firstPerson = people.getMap(0);
String name = firstPerson.getStr("name");  // "John"
Integer age = firstPerson.getInt("age");   // 30

// 类型转换
List<Person> personList = people.asList(Person.class);
```

## IUniversalVals 接口

### 功能概述

`IUniversalVals` 接口提供了类型安全的数据访问方法，所有获取方法都有默认值支持。

### 主要方法

```java
// 数值类型
BigDecimal bigDecimal = obj.getBigDecimal("key", defaultValue);
Double doubleValue = obj.getDouble("key", defaultValue);
Float floatValue = obj.getFloat("key", defaultValue);
Integer intValue = obj.getInt("key", defaultValue);
Long longValue = obj.getLong("key", defaultValue);

// 集合类型
JSONList list = obj.getList("key", defaultValue);
JSONMap map = obj.getMap("key");

// 字符串和布尔类型
String str = obj.getStr("key", defaultValue);
Boolean bool = obj.getBoolean("key", defaultValue);

// 日期类型
Date date = obj.getDate("key");
Date dateWithFormat = obj.getDate("key", "yyyy-MM-dd");

// 通用类型转换
T obj = obj.getObj("key", Class.class);
T obj2 = obj.at("key", Class.class);

// 整体转换
T converted = obj.as(Class.class);
```

## 实际应用示例

### 配置管理

```java
// 从配置文件加载
String configJson = readFile("config.json");
JSONMap config = new JSONMap(configJson);

// 安全获取配置值
String dbHost = config.getStr("database.host", "localhost");
int dbPort = config.getInt("database.port", 3306);
boolean debugMode = config.getBoolean("debug", false);

// 层次化配置
config.set("server.host", "127.0.0.1");
config.set("server.port", 8080);
```

### API 数据处理

```java
// 处理 API 响应
String apiResponse = callApi("/users");
JSONList users = new JSONList(apiResponse);

// 提取特定字段
List<String> usernames = new ArrayList<>();
for (int i = 0; i < users.size(); i++) {
    JSONMap user = users.getMap(i);
    usernames.add(user.getStr("username"));
}

// 批量转换
List<User> userList = users.asList(User.class);
```

### 数据验证

```java
// 验证和清理数据
JSONMap inputData = new JSONMap(requestBody);
inputData.clearEmptyProp(); // 移除空属性

// 验证必需字段
if (inputData.getStr("email") == null) {
    throw new ValidationException("Email is required");
}
```

## 性能优化建议

1. **复用实例**：对于频繁操作，考虑复用 JSONMap/JSONList 实例
2. **批量操作**：使用 `putAll`、`addAll` 进行批量数据操作
3. **类型安全**：利用泛型转换避免运行时类型转换错误
4. **避免深层嵌套**：过于复杂的层次结构会影响性能

## 注意事项

- JSONMap 和 JSONList 是可变的，注意并发访问安全
- 在序列化时，确保对象结构不会产生循环引用
- 对于大量数据操作，考虑使用流式处理
- 空值处理：方法会返回 null 而不是抛出异常