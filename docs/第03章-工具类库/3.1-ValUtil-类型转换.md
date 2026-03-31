# ValUtil - 对象转换工具类

[返回首页](../README.md) / [值工具](./valutil.md)

## 简介

`ValUtil` 是一个强大的类型转换工具类，支持多种数据类型之间的转换。它是 dlz.comm 中最常用的核心工具之一。

## 功能特点

- 支持多种基本数据类型转换
- 提供安全的类型转换（带默认值）
- 支持集合和数组转换
- 支持日期和时间转换
- 支持对象转换

## 主要方法

### 基本类型转换

```java
// 转换为整数
int intValue = ValUtil.toInt(obj, defaultValue);
// 转换为长整数
long longValue = ValUtil.toLong(obj, defaultValue);
// 转换为浮点数
double doubleValue = ValUtil.toDouble(obj, defaultValue);
float floatValue = ValUtil.toFloat(obj, defaultValue);
// 转换为字符串
String stringValue = ValUtil.toStr(obj, defaultValue);
// 转换为布尔值
boolean boolValue = ValUtil.toBoolean(obj, defaultValue);
// 转换为大数
BigDecimal bigDecimalValue = ValUtil.toBigDecimal(obj, defaultValue);
```

### 集合和数组转换

```java
// 转换为列表
List<T> list = ValUtil.toList(obj, defaultList);
List<T> listWithType = ValUtil.toList(obj, clazz);
// 转换为数组
T[] array = ValUtil.toArray(obj, clazz);
T[] arrayWithDefault = ValUtil.toArray(obj, defaultArray);
```

### 日期转换

```java
// 转换为日期
Date date = ValUtil.toDate(obj);
Date dateWithFormat = ValUtil.toDate(obj, format);
// 转换为日期字符串
String dateStr = ValUtil.toDateStr(obj);
String dateStrWithFormat = ValUtil.toDateStr(obj, format);
```

### 对象转换

```java
// 转换为指定类型对象
T obj = ValUtil.toObj(sourceObj, targetClass);
```

## 使用示例

### 基本类型转换

```java
// 将字符串转换为整数
String str = "123";
int num = ValUtil.toInt(str, 0);  // 结果: 123

// 将对象转换为字符串
Object obj = 456;
String result = ValUtil.toStr(obj, "default");  // 结果: "456"

// 将字符串转换为布尔值
String boolStr = "true";
boolean boolValue = ValUtil.toBoolean(boolStr, false);  // 结果: true
```

### 集合转换

```java
// 将数组转换为列表
String[] arr = {"a", "b", "c"};
List<String> list = ValUtil.toList(arr);

// 将字符串转换为列表（逗号分隔）
String csv = "1,2,3,4";
List<Integer> intList = ValUtil.toList(csv, Integer.class);
```

### 日期转换

```java
// 将字符串转换为日期
String dateStr = "2023-12-25 14:30:00";
Date date = ValUtil.toDate(dateStr);

// 指定格式转换
String customDate = "2023/12/25";
Date custom = ValUtil.toDate(customDate, "yyyy/MM/dd");
```

### 安全转换

```java
// 转换失败时使用默认值
Object invalidObj = "not_a_number";
int safeInt = ValUtil.toInt(invalidObj, -1);  // 结果: -1

// 转换null值
Object nullObj = null;
String safeStr = ValUtil.toStr(nullObj, "default");  // 结果: "default"
```

## 最佳实践

1. **使用默认值**：在可能转换失败的情况下，提供合适的默认值
2. **类型安全**：确保转换的目标类型与源数据兼容
3. **性能考虑**：对于大量数据转换，考虑缓存转换结果

## 注意事项

- 转换失败时，数值类型默认返回0，字符串类型返回null或提供的默认值
- 日期格式转换时，请确保格式字符串与输入数据匹配
- 数组和集合转换时，注意类型擦除的影响