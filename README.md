# DLZ COMM - 通用工具包

DLZ COMM 是一个功能丰富的通用工具包，提供了对象转换、JSON处理、加密解密、日期计算等多种实用功能。

## 目录

- [特色功能](#特色功能)
- [工具模块文档](#工具模块文档)
- [快速开始](#快速开始)
- [特性](#特性)
- [许可证](#许可证)

## 核心功能

### 1. 对象转换工具类 - ValUtil

`ValUtil` 是一个强大的类型转换工具类，支持多种数据类型之间的转换。

```java
// 参数可以是任意Object类型
Double aDouble = ValUtil.toDouble("3.35");

// 常用转换方法，覆盖大部分类型转换需求：
ValUtil.toBigDecimal(obj, defaultV);    // 转换为BigDecimal
ValUtil.toDouble(obj, defaultV);        // 转换为Double
ValUtil.toFloat(obj, defaultV);         // 转换为Float
ValUtil.toInt(obj, defaultV);           // 转换为Integer
ValUtil.toLong(obj, defaultV);          // 转换为Long
ValUtil.toArray(obj, clazz);            // 转换为数组
ValUtil.toArray(obj, defaultV);         // 转换为数组（带默认值）
ValUtil.toArrayObj(obj, clazz, clazzs); // 转换为对象数组
ValUtil.toList(obj, defaultV);          // 转换为列表（带默认值）
ValUtil.toList(obj, clazz);             // 转换为指定类型的列表
ValUtil.toStr(obj, defaultV);           // 转换为字符串（带默认值）
ValUtil.toBoolean(obj, defaultV);       // 转换为布尔值（带默认值）
ValUtil.toDate(obj);                    // 转换为日期,指定识别格式
ValUtil.toDate(obj, format);            // 转换为日期（指定格式）
ValUtil.toDateStr(obj);                 // 转换为日期字符串
ValUtil.toDateStr(obj, format);         // 转换为日期字符串（指定格式）
ValUtil.toObj(obj, classs);             // 转换为指定对象类型
```

### 2. JSON路径取值功能
**核心特色功能**：支持多层级取值——使用点号和数组下标组合，从结构化数据中获取所需值。

使用示例：
```json
{
  "info": {
    "a": [
      [{"b": 1}, {"c": 2}],
      [{"d": 3}, {"e": 4}, {"f": 5}]
    ]
  }
}
```

- 取出 **c** 属性的值，使用表达式：`info.a[0][1].c`
- 取出 **f** 属性的值，使用表达式：`info.a[1][2].f`
- 取出 **f** 所在对象，使用表达式：
  - 方式1：`info.a[1][2]`
  - 方式2：`info.a[1][-1]`

#### 关于负数下标的说明
负数表示从后往前数，-1表示最后一个，-2表示倒数第二个

使用示例：
```java
String data = "{\"info\":{\"a\":[[{\"b\":1},{\"c\":2}],[{\"d\":3},{\"e\":4},{\"f\":5}]]}}";
System.out.println("c的值：" + JacksonUtil.at(data, "info.a[0][1].c"));      // 输出：2
System.out.println("f的值：" + JacksonUtil.at(data, "info.a[1][2].f"));      // 输出：5
System.out.println("f所在对象：" + JacksonUtil.at(data, "info.a[1][2]"));    // 输出：{"f":5}
System.out.println("f所在对象：" + JacksonUtil.at(data, "info.a[1][-1]"));   // 输出：{"f":5}
```

## 工具模块文档

- [对象转换工具 - ValUtil](docs/valutil.md)
- [JSON处理工具 - JacksonUtil](docs/jacksonutil.md)
- [JSONMap/JSONList - JSON数据结构](docs/jsonmap-jsonlist.md)
- [字符串处理工具 - StringUtils](docs/stringutils.md)
- [日期处理工具 - DateUtil](docs/dateutil.md)
- [文件处理工具 - FileUtil](docs/fileutil.md)
- [XML处理工具 - XmlUtil](docs/xmlutil.md)
- [加密工具 - EncryptUtil](docs/encryptutil.md)
- [缓存工具 - Cache](docs/cache.md)
- [异常处理 - Exceptions](docs/exceptions.md)
- [常量定义 - Constants](docs/constants.md)
- [反射工具 - Reflections](docs/reflections.md)
- [树结构工具 - TreeUtil](docs/treeutil.md)
- [HTTP工具 - HttpUtil](docs/httputil.md)

## 快速开始

### Maven 依赖

```xml
<dependency>
    <groupId>com.chan3d</groupId>
    <artifactId>dlz.comm</artifactId>
    <version>6.3.3</version>
</dependency>
```

### 基本使用示例

```java
// 类型转换示例
String strValue = "123";
int intValue = ValUtil.toInt(strValue);  // 转换为整数

// JSON处理示例
String jsonStr = "{\"name\":\"John\", \"age\":30}";
JSONMap jsonMap = new JSONMap(jsonStr);
String name = jsonMap.getStr("name");  // 获取name字段

// 日期处理示例
Date now = DateUtil.now();  // 获取当前时间
String dateStr = DateUtil.getDateStr(now);  // 格式化为字符串
```

## 特性

- **轻量级**：专注于提供核心工具功能
- **高性能**：优化过的算法和数据结构
- **易用性**：提供简洁的API和链式调用
- **类型安全**：充分使用泛型确保类型安全
- **可扩展**：支持自定义实现和扩展

## 许可证

MIT License