# Constants - 常量定义

[返回首页](../README.md) / [常量定义](./constants.md)

## 简介

DLZ COMM 提供了几组常用的常量定义，包括字符串常量、字符常量和字符集常量，用于避免硬编码并提高代码可读性。

## Str - 字符串常量

### 功能特点

- 定义常用的字符串常量
- 避免硬编码
- 提高代码可读性

### 主要常量

#### 基本符号

- `AMPERSAND` = "&" - 和号
- `AT` = "@" - 电子邮件符号
- `ASTERISK` = "*" - 星号
- `SLASH` = "/" - 正斜杠
- `BACK_SLASH` = "\\" - 反斜杠
- `COLON` = ":" - 冒号
- `COMMA` = "," - 逗号
- `DASH` = "-" - 破折号
- `DOLLAR` = "$" - 美元符号
- `DOT` = "." - 点号

#### 逻辑值

- `EMPTY` = "" - 空字符串
- `EMPTY_JSON` = "{}" - 空JSON对象
- `EQUALS` = "=" - 等号
- `FALSE` = "false" - 小写false
- `TRUE` = "true" - 小写true
- `NULL` = "null" - null字符串
- `YES` = "yes" - 小写yes
- `NO` = "no" - 小写no

#### 特殊符号

- `LEFT_BRACE` = "{" - 左花括号
- `RIGHT_BRACE` = "}" - 右花括号
- `LEFT_BRACKET` = "(" - 左圆括号
- `RIGHT_BRACKET` = ")" - 右圆括号
- `LEFT_CHEV` = "<" - 左尖括号
- `RIGHT_CHEV` = ">" - 右尖括号
- `NEWLINE` = "\n" - 换行符
- `SPACE` = " " - 空格
- `TAB` = "\t" - 制表符

#### 数值常量

- `ONE` = "1" - 数字1
- `ZERO` = "0" - 数字0
- `MINUS_ONE` = "-1" - 数字-1

#### HTTP相关

- `GET` = "GET" - GET请求方法
- `POST` = "POST" - POST请求方法

#### 文件扩展名

- `DOT_JAVA` = ".java" - Java文件扩展名

### 使用示例

```java
import static com.dlz.comm.consts.Str.*;

public class Example {
    public void useConstants() {
        // 使用符号常量
        String path = "folder" + SLASH + "file.txt";
        String email = "user" + AT + "example.com";
        String param = "key" + EQUALS + "value";
        
        // 使用逻辑值常量
        if (status.equals(TRUE)) {
            System.out.println("Success");
        }
        
        // 使用特殊符号
        String json = LEFT_BRACE + "name" + COLON + "value" + RIGHT_BRACE;
        
        // 检查空值
        if (str.equals(EMPTY)) {
            // 处理空字符串
        }
    }
}
```

## Char - 字符常量

### 功能特点

- 定义常用的字符常量
- 避免硬编码字符
- 提高代码可读性

### 主要常量

#### 字母

- `UPPER_A` = 'A' - 大写字母A
- `LOWER_A` = 'a' - 小写字母a
- `UPPER_Z` = 'Z' - 大写字母Z
- `LOWER_Z` = 'z' - 小写字母z

#### 符号

- `DOT` = '.' - 点号
- `AT` = '@' - 电子邮件符号
- `LEFT_BRACE` = '{' - 左花括号
- `RIGHT_BRACE` = '}' - 右花括号
- `LEFT_BRACKET` = '(' - 左圆括号
- `RIGHT_BRACKET` = ')' - 右圆括号
- `DASH` = '-' - 破折号
- `PERCENT` = '%' - 百分号
- `PIPE` = '|' - 管道符
- `PLUS` = '+' - 加号
- `QUESTION_MARK` = '?' - 问号
- `EXCLAMATION_MARK` = '!' - 感叹号
- `EQUALS` = '=' - 等号
- `AMPERSAND` = '&' - 和号
- `ASTERISK` = '*' - 星号
- `BACK_SLASH` = '\\' - 反斜杠
- `COLON` = ':' - 冒号
- `COMMA` = ',' - 逗号
- `DOLLAR` = '$' - 美元符号
- `SLASH` = '/' - 正斜杠
- `HASH` = '#' - 井号
- `HAT` = '^' - 插入符号
- `LEFT_CHEV` = '<' - 左尖括号
- `RIGHT_CHEV` = '>' - 右尖括号

#### 控制字符

- `NEWLINE` = '\n' - 换行符
- `RETURN` = '\r' - 回车符
- `TAB` = '\t' - 制表符
- `SPACE` = ' ' - 空格

#### 数字

- `ONE` = '1' - 数字1
- `ZERO` = '0' - 数字0

### 使用示例

```java
import static com.dlz.comm.consts.Char.*;

public class Example {
    public void useConstants() {
        // 字符比较
        if (ch == DOT) {
            // 处理点号
        }
        
        // 字符范围检查
        if (ch >= LOWER_A && ch <= LOWER_Z) {
            // 小写字母
        }
        
        // 特殊字符处理
        if (ch == NEWLINE) {
            // 换行处理
        }
    }
    
    public boolean isLetter(char ch) {
        return (ch >= LOWER_A && ch <= LOWER_Z) || 
               (ch >= UPPER_A && ch <= UPPER_Z);
    }
    
    public boolean isDigit(char ch) {
        return ch >= ZERO && ch <= '9';
    }
}
```

## Charsets - 字符集常量

### 功能特点

- 定义常用的字符集常量
- 提供字符集转换方法
- 避免硬编码字符集名称

### 主要常量

#### 字符集定义

- `ISO_8859_1` - ISO-8859-1字符集
- `ISO_8859_1_NAME` - ISO-8859-1字符集名称
- `GBK` - GBK字符集
- `GBK_NAME` - GBK字符集名称
- `UTF_8` - UTF-8字符集
- `UTF_8_NAME` - UTF-8字符集名称

#### 头部常量

- `CONTENT_TYPE_NAME` - "Content-type" 头部名称

### 主要方法

#### 字符集转换

```java
// 转换为Charset对象
static Charset charset(String charsetName);

// 编码转换
static String convert(String value, String inputCharset, String outCharset);

// 转换为UTF-8
static String toUtf8(String value, String inputCharset);

// ISO-8859-1转UTF-8
static String iso2Utf8(String value);

// GBK转UTF-8
static String gbk2Utf8(String value);
```

### 使用示例

```java
import static com.dlz.comm.consts.Charsets.*;

public class EncodingExample {
    
    public void useCharsets() {
        // 使用预定义字符集
        Charset utf8 = UTF_8;
        Charset gbk = GBK;
        Charset iso = ISO_8859_1;
        
        // 获取字符集名称
        String utf8Name = UTF_8_NAME;
        String gbkName = GBK_NAME;
        String isoName = ISO_8859_1_NAME;
    }
    
    public void convertEncoding() {
        String gbkText = "中文";
        
        // GBK转UTF-8
        String utf8Text = gbk2Utf8(gbkText);
        
        // 通用转换
        String converted = convert(gbkText, GBK_NAME, UTF_8_NAME);
        
        // 使用指定字符集转换
        String toUtf8 = toUtf8(gbkText, GBK_NAME);
    }
    
    public void dynamicCharset() {
        // 动态获取字符集
        Charset cs = charset("UTF-8");
        Charset defaultCs = charset(null); // 使用默认字符集
    }
}
```

## 实际应用示例

### URL构建

```java
import static com.dlz.comm.consts.Str.*;

public class UrlBuilder {
    public String buildUrl(String baseUrl, Map<String, String> params) {
        StringBuilder url = new StringBuilder(baseUrl);
        boolean hasParams = baseUrl.contains(QUESTION_MARK);
        
        for (Map.Entry<String, String> entry : params.entrySet()) {
            url.append(hasParams ? AMPERSAND : QUESTION_MARK)
               .append(entry.getKey())
               .append(EQUALS)
               .append(entry.getValue());
            hasParams = true;
        }
        
        return url.toString();
    }
}
```

### JSON处理

```java
import static com.dlz.comm.consts.Str.*;

public class JsonHelper {
    public String createSimpleJson(String key, String value) {
        return LEFT_BRACE + 
               DOUBLE_QUOTE + key + DOUBLE_QUOTE + 
               COLON + 
               DOUBLE_QUOTE + value + DOUBLE_QUOTE + 
               RIGHT_BRACE;
    }
    
    public boolean isEmptyJson(String json) {
        return json != null && json.equals(EMPTY_JSON);
    }
}
```

### 文件操作

```java
import static com.dlz.comm.consts.Str.*;

public class FileUtils {
    public String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf(DOT);
        return dotIndex > 0 ? filename.substring(dotIndex) : EMPTY;
    }
    
    public String changeExtension(String filename, String newExt) {
        int dotIndex = filename.lastIndexOf(DOT);
        if (dotIndex > 0) {
            return filename.substring(0, dotIndex) + DOT + newExt;
        }
        return filename + DOT + newExt;
    }
}
```

### 编码转换

```java
import static com.dlz.comm.consts.Charsets.*;

public class EncodingConverter {
    public String convertToUtf8(String input, String sourceEncoding) {
        try {
            return convert(input, sourceEncoding, UTF_8_NAME);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("编码转换失败", e);
        }
    }
    
    public String gbkToUtf8(String gbkText) {
        return gbk2Utf8(gbkText);
    }
    
    public String isoToUtf8(String isoText) {
        return iso2Utf8(isoText);
    }
}
```

## 最佳实践

### 导入常量

```java
// 静态导入，提高可读性
import static com.dlz.comm.consts.Str.*;
import static com.dlz.comm.consts.Charsets.*;

public class BestPractice {
    public void example() {
        // 使用常量而非硬编码
        String path = "folder" + SLASH + "file.txt";
        Charset cs = UTF_8;
    }
}
```

### 性能考虑

- 常量定义为静态final，编译时常量优化
- 字符集常量预定义，避免重复创建
- 字符串常量池化，减少内存占用

### 可维护性

- 集中定义，便于修改
- 语义明确，便于理解
- 遵循命名规范，便于查找

## 注意事项

- 所有常量都是静态final的
- Str和Char接口定义的常量不可修改
- 字符集转换可能抛出UnsupportedEncodingException
- 线程安全：所有常量都是线程安全的
- 内存效率：常量在类加载时创建，占用少量内存