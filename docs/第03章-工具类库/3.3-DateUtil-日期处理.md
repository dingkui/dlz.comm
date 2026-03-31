# DateUtil - 日期处理工具类

[返回首页](../README.md) / [日期工具](./dateutil.md)

## 简介

`DateUtil` 是一个功能全面的日期处理工具类，提供了日期格式化、解析、转换等多种操作。

## 功能特点

- 丰富的日期格式常量
- 智能日期解析
- 日期时间格式化
- 日期计算功能
- UTC 时间处理
- LocalDateTime 支持

## 预定义格式常量

### 日期格式

- `PATTERN_YEAR`: "yyyy" - 年份格式
- `PATTERN_MONTH`: "yyyy-MM" - 年月格式
- `PATTERN_MONTH_MINI`: "yyyyMM" - 简化年月格式
- `PATTERN_DATE`: "yyyy-MM-dd" - 日期格式
- `PATTERN_DATE_MINI`: "yyyyMMdd" - 简化日期格式

### 时间格式

- `PATTERN_TIME`: "HH:mm:ss" - 时间格式
- `PATTERN_DATETIME`: "yyyy-MM-dd HH:mm:ss" - 日期时间格式
- `PATTERN_DATETIME_MINI`: "yyyyMMddHHmmss" - 简化日期时间格式
- `PATTERN_UTC`: "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" - UTC 格式

### 预定义格式器

- `YEAR`, `MONTH`, `DATE`, `TIME`, `DATETIME` 等静态格式器实例

## 主要方法

### 获取当前时间

```java
// 获取当前日期
Date now = DateUtil.now();
```

### 日期格式化

```java
// 格式化指定日期
String formatted = DateUtil.format(date, "yyyy-MM-dd HH:mm:ss");

// 格式化当前时间
String nowStr = DateUtil.formatNow("yyyy-MM-dd");
```

### 日期解析

```java
// 智能日期解析
Date parsed = DateUtil.getDate("2023-12-25 14:30:00");

// 指定格式解析
Date specific = DateUtil.getDate("2023/12/25", "yyyy/MM/dd");

// UTC 时间解析
Date utcDate = DateUtil.parseUTCDate("2023-12-25T14:30:00.000Z");
```

### 日期转换

```java
// Date 转 LocalDateTime
LocalDateTime ldt = DateUtil.getLocalDateTime(date);

// Date 转 LocalDate
Date localDate = DateUtil.getDate(localDate);

// LocalDateTime 转 Date
Date date = DateUtil.getDate(localDateTime);
```

## 使用示例

### 基本格式化操作

```java
// 获取当前时间
Date now = DateUtil.now();
System.out.println(DateUtil.getDateStr(now));        // 2023-12-25
System.out.println(DateUtil.getDateTimeStr(now));    // 2023-12-25 14:30:00

// 格式化指定日期
Date customDate = new Date();
String formatted = DateUtil.format(customDate, "yyyy年MM月dd日 HH时mm分ss秒");
```

### 日期解析

```java
// 智能解析不同格式的日期
String[] dateStrs = {
    "2023-12-25 14:30:00",  // 标准日期时间
    "2023-12-25",           // 仅日期
    "2023-12",              // 仅年月
    "14:30",                // 仅时间
    "2023-12-25T14:30:00.000Z" // UTC 格式
};

for (String dateStr : dateStrs) {
    Date parsed = DateUtil.getDate(dateStr);
    if (parsed != null) {
        System.out.println(dateStr + " -> " + DateUtil.getDateTimeStr(parsed));
    }
}
```

### UTC 时间处理

```java
// 解析 UTC 时间
String utcStr = "2023-12-25T14:30:00.000Z";
Date utcDate = DateUtil.parseUTCDate(utcStr);

// 转换为 LocalDateTime（使用系统默认时区）
LocalDateTime localDT = DateUtil.parseUTCLocalDateTime(utcStr);
```

### LocalDateTime 操作

```java
// 解析字符串为 LocalDateTime
LocalDateTime ldt = DateUtil.getLocalDateTime("2023-12-25 14:30:00");

// 转换 Date 为 LocalDateTime
Date date = new Date();
LocalDateTime ldt2 = DateUtil.getLocalDateTime(date);

// 转换 LocalDateTime 为 Date
Date date2 = DateUtil.getDate(ldt);
```

## 高级功能

### 日期格式化器

```java
// 获取指定格式的格式化器
DateFormat formatter = DateUtil.formatter("yyyy-MM-dd HH:mm:ss");

// 使用预定义格式器
String yearStr = DateUtil.YEAR.format(new Date());
String dateStr = DateUtil.DATE.format(new Date());
String timeStr = DateUtil.TIME.format(new Date());
```

### 智能解析机制

`DateUtil` 具有智能解析能力，能够自动识别以下格式：

- `yyyy-MM-dd HH:mm:ss` - 完整日期时间
- `yyyy-MM-dd` - 仅日期
- `yyyy-MM` - 仅年月
- `HH:mm` - 仅时间（自动补充日期为当天）
- `HH:mm:ss` - 仅时间（自动补充日期为当天）
- `yyyy-MM-ddTHH:mm:ss.SSSZ` - UTC 格式

## 实际应用示例

### 日志时间处理

```java
// 解析日志中的时间戳
String logTimestamp = "2023-12-25 14:30:05";
Date logTime = DateUtil.getDate(logTimestamp);
String readable = DateUtil.format(logTime, "yyyy年MM月dd日 HH:mm:ss");
```

### 时间戳转换

```java
// 获取当前时间戳（秒）
long timestamp = DateUtil.dateline();
System.out.println("当前时间戳: " + timestamp);
```

### 日期范围验证

```java
// 验证日期是否在有效范围内
Date startDate = DateUtil.getDate("2023-01-01");
Date endDate = DateUtil.getDate("2023-12-31");
Date checkDate = DateUtil.getDate("2023-06-15");

if (checkDate.after(startDate) && checkDate.before(endDate)) {
    System.out.println("日期在有效范围内");
}
```

## 性能优化建议

1. **复用格式化器**：使用预定义的格式化器实例而非每次都创建
2. **缓存常用格式**：对于频繁使用的格式，可以预先获取格式化器
3. **选择合适方法**：根据需要选择 Date 或 LocalDateTime 相关方法
4. **避免频繁转换**：尽量减少 Date 和 LocalDateTime 之间的频繁转换

## 注意事项

- 日期解析失败时返回 null，需要进行空值检查
- UTC 时间解析会自动转换为系统默认时区
- 智能解析支持多种常见格式
- 线程安全：`DateUtil` 的方法都是线程安全的
- 时区处理：默认使用系统时区，UTC 时间会转换为本地时间