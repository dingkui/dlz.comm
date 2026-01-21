# FileUtil - 文件处理工具类

[返回首页](../README.md) / [文件工具](./fileutil.md)

## 简介

`FileUtil` 是一个功能全面的文件操作工具类，提供了文件遍历、读取、写入、移动、删除等多种操作。

## 功能特点

- 文件遍历和过滤
- 文件内容读取和写入
- 文件复制和移动
- 文件删除（支持目录递归删除）
- 文件信息获取
- 文件类型识别

## 主要方法

### 文件遍历

```java
// 遍历目录下的所有文件
List<File> files = FileUtil.list(directory);

// 使用文件过滤器遍历
List<File> filtered = FileUtil.list(directory, fileFilter);

// 使用 Ant 风格模式遍历
List<File> matched = FileUtil.list(directory, "*.txt");
```

### 文件信息获取

```java
// 获取文件扩展名
String extension = FileUtil.getFileExtension(filename);

// 获取不含扩展名的文件名
String nameWithoutExt = FileUtil.getNameWithoutExtension(filename);

// 获取系统临时目录
File tempDir = FileUtil.getTempDir();
String tempPath = FileUtil.getTempDirPath();
```

### 文件读取

```java
// 读取文件内容为字符串
String content = FileUtil.readToString(file);

// 指定编码读取
String contentWithEncoding = FileUtil.readToString(file, charset);

// 读取文件内容为字节数组
byte[] bytes = FileUtil.readToByteArray(file);
```

### 文件写入

```java
// 写入字符串到文件
FileUtil.writeToFile(file, content);

// 指定编码写入
FileUtil.writeToFile(file, content, charset);

// 追加写入
FileUtil.writeToFile(file, content, append);
```

### 文件操作

```java
// 移动文件
FileUtil.moveFile(srcFile, destFile);

// 复制文件
int copiedBytes = FileUtil.copy(srcFile, destFile);

// 删除文件（静默模式，不抛异常）
boolean deleted = FileUtil.deleteQuietly(file);

// 流转换为文件
FileUtil.toFile(inputStream, file);
```

## 使用示例

### 文件遍历

```java
// 遍历目录
File directory = new File("path/to/directory");
List<File> allFiles = FileUtil.list(directory);

// 使用过滤器遍历
List<File> txtFiles = FileUtil.list(directory, pathname -> 
    pathname.getName().endsWith(".txt"));

// 使用 Ant 风格模式
List<File> javaFiles = FileUtil.list(directory, "**/*.java");
```

### 文件读写

```java
// 读取文件
File inputFile = new File("input.txt");
String content = FileUtil.readToString(inputFile, StandardCharsets.UTF_8);

// 写入文件
File outputFile = new File("output.txt");
FileUtil.writeToFile(outputFile, content, StandardCharsets.UTF_8);

// 追加写入
FileUtil.writeToFile(outputFile, "additional content", true);
```

### 文件信息处理

```java
// 获取文件信息
File file = new File("document.pdf");
String ext = FileUtil.getFileExtension(file.getPath());  // "pdf"
String name = FileUtil.getNameWithoutExtension(file.getPath());  // "document"

// 临时目录操作
File tempDir = FileUtil.getTempDir();
File tempFile = new File(tempDir, "temp.txt");
FileUtil.writeToFile(tempFile, "temporary content");
```

### 文件复制和移动

```java
// 复制文件
File src = new File("source.txt");
File dest = new File("destination.txt");
int bytesCopied = FileUtil.copy(src, dest);

// 移动文件
File newDest = new File("moved.txt");
FileUtil.moveFile(dest, newDest);

// 删除文件
FileUtil.deleteQuietly(newDest);
```

## 高级功能

### 文件过滤

```java
// 自定义文件过滤器
FileFilter filter = pathname -> {
    return pathname.isFile() && 
           pathname.getName().endsWith(".log") && 
           pathname.length() > 1024; // 大于1KB的日志文件
};

List<File> largeLogFiles = FileUtil.list(directory, filter);
```

### 递归目录操作

```java
// 遍历整个目录树
File rootDir = new File("root/");
List<File> allFiles = FileUtil.list(rootDir);

// 查找特定类型的文件
List<File> imageFiles = FileUtil.list(rootDir, "**/*.{jpg,jpeg,png,gif}");
```

### 批量文件操作

```java
// 批量处理文件
List<File> files = FileUtil.list(new File("documents/"), "**/*.txt");
for (File file : files) {
    String content = FileUtil.readToString(file);
    // 处理内容
    FileUtil.writeToFile(file, processedContent);
}
```

## 实际应用示例

### 日志文件清理

```java
// 清理超过30天的日志文件
File logDir = new File("/var/log/myapp");
List<File> logFiles = FileUtil.list(logDir, "*.log");

long thirtyDaysAgo = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000;
for (File logFile : logFiles) {
    if (logFile.lastModified() < thirtyDaysAgo) {
        FileUtil.deleteQuietly(logFile);
    }
}
```

### 配置文件备份

```java
// 备份配置文件
File configDir = new File("config/");
List<File> configFiles = FileUtil.list(configDir, "**/*.properties");

for (File configFile : configFiles) {
    File backupFile = new File(configFile.getPath() + ".backup");
    FileUtil.copy(configFile, backupFile);
}
```

### 文件类型统计

```java
// 统计目录中各种文件类型的数量
File directory = new File("downloads/");
Map<String, Integer> typeCount = new HashMap<>();

List<File> files = FileUtil.list(directory);
for (File file : files) {
    if (file.isFile()) {
        String ext = FileUtil.getFileExtension(file.getPath()).toLowerCase();
        typeCount.merge(ext, 1, Integer::sum);
    }
}

typeCount.forEach((ext, count) -> 
    System.out.println(ext + ": " + count + " files"));
```

## 性能优化建议

1. **批量操作**：对于大量文件操作，使用批量方法而非单个处理
2. **流式处理**：对于大文件，考虑使用流式处理避免内存溢出
3. **过滤器优化**：使用高效的过滤器逻辑，避免复杂计算
4. **资源管理**：确保正确关闭文件流资源（工具类已自动处理）

## 安全注意事项

- 使用 `deleteQuietly` 方法进行安全删除，不会抛出异常
- 验证文件路径，防止路径穿越攻击
- 限制文件操作范围，避免意外影响系统文件
- 检查文件大小，防止读取过大文件导致内存问题

## 注意事项

- `deleteQuietly` 方法不会抛出异常，返回布尔值表示是否成功
- 文件路径操作时注意跨平台兼容性
- 大文件操作时考虑使用流式处理
- 线程安全：`FileUtil` 的方法是线程安全的
- 字符编码：默认使用系统编码，建议显式指定编码