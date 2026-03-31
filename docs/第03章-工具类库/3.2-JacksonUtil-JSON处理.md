# JacksonUtil - JSON处理工具类

[返回首页](../README.md) / [Jackson工具](./jacksonutil.md)

## 简介

`JacksonUtil` 是一个功能强大的 JSON 处理工具类，基于 Jackson 库构建，提供了便捷的 JSON 序列化、反序列化和类型转换等功能。

## 功能特点

- 支持复杂的 JSON 序列化和反序列化
- 提供 JSON 路径取值功能
- 自定义对象与 JSON 字符串互转
- 支持复杂类型的反序列化
- 统一的日期时间格式处理
- 支持多种数据源的 JSON 解析

## 主要方法

### 序列化方法

```java
// 将对象转换为 JSON 字符串
String jsonStr = JacksonUtil.getJson(obj);

// 将对象转换为字节数组
byte[] bytes = JacksonUtil.toJsonAsBytes(obj);
```

### 反序列化方法

```java
// 将 JSON 字符串转换为 JSONMap
JSONMap jsonMap = JacksonUtil.readValue(jsonStr);

// 将 JSON 字符串转换为指定类型对象
T obj = JacksonUtil.readValue(jsonStr, clazz);

// 将 JSON 字符串转换为列表
List<T> list = JacksonUtil.readList(jsonStr);

// 将 JSON 字符串转换为指定类型列表
List<T> typedList = JacksonUtil.readListValue(jsonStr, clazz);
```

### 类型转换方法

```java
// 类型转换
T convertedObj = JacksonUtil.convertValue(sourceObj, targetType);

// 从 JSON 节点转换为对象
T obj = JacksonUtil.treeToValue(jsonNode, clazz);

// 对象转换为 JSON 节点
JsonNode jsonNode = JacksonUtil.valueToTree(obj);
```

### JSON 路径取值

```java
// 从对象中按路径取值
Object value = JacksonUtil.at(data, "path.expression");

// 从对象中按路径取值并转换类型
T typedValue = JacksonUtil.at(data, "path.expression", type);
```

## 使用示例

### 基本序列化和反序列化

```java
// 序列化示例
Person person = new Person("John", 30);
String jsonStr = JacksonUtil.getJson(person);
// 结果: {"name":"John","age":30}

// 反序列化示例
String json = "{\"name\":\"Jane\",\"age\":25}";
Person person2 = JacksonUtil.readValue(json, Person.class);
```

### JSON 路径取值

```java
// 复杂 JSON 结构示例
String complexJson = "{\"info\":{\"users\":[{\"name\":\"Alice\",\"age\":25},{\"name\":\"Bob\",\"age\":30}]}}";

// 获取第一个用户的姓名
String firstName = JacksonUtil.at(complexJson, "info.users[0].name", String.class);
// 结果: "Alice"

// 获取第二个用户的年龄
Integer age = JacksonUtil.at(complexJson, "info.users[1].age", Integer.class);
// 结果: 30

// 使用负数下标获取最后一个用户
Object lastUser = JacksonUtil.at(complexJson, "info.users[-1]");
// 结果: {"name":"Bob","age":30}
```

### 类型转换

```java
// 对象类型转换
Map<String, Object> map = new HashMap<>();
map.put("name", "John");
map.put("age", 30);

// 转换为 Person 对象
Person person = JacksonUtil.convertValue(map, Person.class);

// 转换为 JSONMap
JSONMap jsonMap = JacksonUtil.convertValue(person, JSONMap.class);
```

### JSON 节点操作

```java
// 读取 JSON 树
JsonNode tree = JacksonUtil.readTree(jsonStr);

// 从树节点转换为对象
Person person = JacksonUtil.treeToValue(tree, Person.class);

// 对象转换为节点
JsonNode node = JacksonUtil.valueToTree(person);
```

## JSON 路径语法

### 基本语法

- `.` 用于访问对象属性
- `[]` 用于访问数组元素
- `[n]` 访问索引为 n 的元素
- `[-n]` 从末尾倒数第 n 个元素

### 示例

```json
{
  "store": {
    "book": [
      { "category": "reference", "author": "Nigel Rees" },
      { "category": "fiction", "author": "Evelyn Waugh" }
    ],
    "bicycle": {
      "color": "red",
      "price": 19.95
    }
  }
}
```

- `store.book[0].author` → "Nigel Rees"
- `store.bicycle.color` → "red"
- `store.book[-1].category` → "fiction"

## 配置和特性

### 日期格式处理

- 默认日期格式：`yyyy-MM-dd HH:mm:ss`
- 支持 Java 8 时间 API
- 自动处理时区转换

### 安全特性

- 忽略未知属性
- 支持单引号和无引号字段名
- 防止循环引用

## 最佳实践

1. **复用 ObjectMapper**：`JacksonUtil` 内部使用单例的 `ObjectMapper` 实例
2. **合理使用路径取值**：对于复杂的嵌套结构，使用路径取值比手动解析更高效
3. **类型安全**：使用泛型确保类型安全
4. **错误处理**：注意处理转换失败的情况

## 注意事项

- 对于大数据量的 JSON 操作，考虑使用流式 API
- 避免在 JSON 中包含敏感信息
- 注意处理 null 值和缺失字段的情况
- 确保日期格式的一致性