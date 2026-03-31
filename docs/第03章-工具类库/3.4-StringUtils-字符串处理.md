# StringUtils - 字符串处理工具类

[返回首页](../README.md) / [字符串工具](./stringutils.md)

## 简介

`StringUtils` 是一个功能丰富的字符串处理工具类，提供了字符串判断、格式化、拼接、大小写转换等多种操作。

## 功能特点

- 字符串判空功能
- 字符串格式化
- 字符串拼接
- 大小写转换
- 补零功能
- 模式匹配

## 主要方法

### 字符串判空

```java
// 判断字符串是否为空
boolean empty = StringUtils.isEmpty(str);
boolean notEmpty = StringUtils.isNotEmpty(str);

// 判断多个字符串是否任一为空
boolean anyEmpty = StringUtils.isAnyEmpty(str1, str2, str3);

// 判断字符串是否为空白（仅包含空白字符）
boolean blank = StringUtils.isBlank(str);
boolean notBlank = StringUtils.isNotBlank(str);
```

### 字符串格式化

```java
// 格式化文本，支持位置占位符
String formatted = StringUtils.formatMsg("Hello {}, welcome to {}", "John", "our site");
// 结果: "Hello John, welcome to our site"

// 格式化带索引的文本
String indexed = StringUtils.formatMsg("Value {0} is {1}", "name", "John");
// 结果: "Value John is name"
```

### 字符串拼接

```java
// 使用分隔符合并字符串
String joined = StringUtils.join(",", "a", "b", "c");  // "a,b,c"

// 合并集合
List<String> list = Arrays.asList("a", "b", "c");
String joinedList = StringUtils.join(list, ",");  // "a,b,c"
```

### 大小写转换

```java
// 首字母大写
String capitalized = StringUtils.capitalize("hello");  // "Hello"
```

### 补零功能

```java
// 在数字前补零至指定长度
String padded = StringUtils.addZeroBefor(42, 5);  // "00042"
```

### 字符串填充

```java
// 在左侧填充指定字符至指定长度
String paddedLeft = StringUtils.leftPad("text", 10, '0');  // "000000text"
```

### 字符串匹配

```java
// 检查字符串是否以指定字符串开头
boolean starts = StringUtils.startsWith("hello world", "hello");  // true

// 检查字符串是否以指定字符串数组中任一元素开头
boolean startsAny = StringUtils.startsWithAny("hello world", "hi", "hello");  // true

// 检查是否为数值
boolean isNum = StringUtils.isNumber("123.45");  // true
boolean isInt = StringUtils.isLongOrInt("123");  // true
```

## 使用示例

### 基本字符串操作

```java
// 判空操作
String str1 = "";
String str2 = null;
String str3 = "  ";
String str4 = "hello";

System.out.println(StringUtils.isEmpty(str1));   // true
System.out.println(StringUtils.isEmpty(str2));   // true
System.out.println(StringUtils.isBlank(str3));   // true
System.out.println(StringUtils.isNotEmpty(str4)); // true

// 多个字符串判空
System.out.println(StringUtils.isAnyEmpty(str1, str4)); // true
```

### 字符串格式化

```java
// 基本格式化
String msg1 = StringUtils.formatMsg("Hello {}!", "World");
// 结果: "Hello World!"

// 索引格式化
String msg2 = StringUtils.formatMsg("First: {0}, Second: {1}", "A", "B");
// 结果: "First: A, Second: B"

// 混合格式化
String msg3 = StringUtils.formatMsg("Name: {name0}, Age: {age1}", "John", "25");
// 结果: "Name: John, Age: 25"
```

### 字符串拼接

```java
// 拼接数组
String[] items = {"apple", "banana", "orange"};
String result = StringUtils.join(",", items);
// 结果: "apple,banana,orange"

// 拼接集合
List<String> list = Arrays.asList("red", "green", "blue");
String colors = StringUtils.join(list, "|");
// 结果: "red|green|blue"
```

### 数值判断

```java
// 检查是否为数值
System.out.println(StringUtils.isNumber("123"));      // true
System.out.println(StringUtils.isNumber("123.45"));   // true
System.out.println(StringUtils.isNumber("-123"));     // true
System.out.println(StringUtils.isNumber("abc"));      // false

// 检查是否为整数或长整数
System.out.println(StringUtils.isLongOrInt("123"));   // true
System.out.println(StringUtils.isLongOrInt("123.45"));// false
```

### 字符串处理

```java
// 首字母大写
System.out.println(StringUtils.capitalize("hello"));    // "Hello"
System.out.println(StringUtils.capitalize("HELLO"));    // "HELLO"

// 左侧填充
System.out.println(StringUtils.leftPad("5", 3, '0'));   // "005"
System.out.println(StringUtils.leftPad("hello", 10, '-'));// "-----hello"

// 补零
System.out.println(StringUtils.addZeroBefor(42, 5));    // "00042"
```

### Bean ID 生成

```java
// 根据类名生成 Bean ID
String beanId1 = StringUtils.getBeanId("com.example.UserService");
// 结果: "userService"

String beanId2 = StringUtils.getBeanId(UserService.class);
// 结果: "userService"
```

## 高级功能

### 模板替换

```java
// 使用 JSONMap 进行模板替换
JSONMap params = new JSONMap("name", "John", "age", "30");
String template = "Hello ${name}, you are ${age} years old";
String result = StringUtils.formatMsg(template, params);
// 结果: "Hello John, you are 30 years old"
```

### 占位符替换

```java
// 递归占位符替换
JSONMap context = new JSONMap(
    "greeting", "Hello ${name}",
    "name", "World",
    "punctuation", "!"
);
String msg = StringUtils.formatMsg("${greeting}${punctuation}", context);
// 结果: "Hello World!"
```

## 性能优化建议

1. **复用操作**：对于频繁的相同操作，考虑缓存结果
2. **选择合适方法**：根据具体需求选择最合适的判断方法
3. **避免不必要的操作**：在已知字符串非空的情况下，跳过判空检查
4. **批量处理**：使用集合操作方法处理多个字符串

## 注意事项

- `isEmpty()` 检查 null、空字符串和空集合
- `isBlank()` 检查空白字符串（仅包含空白字符）
- 格式化方法支持多种占位符语法
- 数值判断方法只检查格式，不进行实际转换
- 在多线程环境中使用时注意线程安全