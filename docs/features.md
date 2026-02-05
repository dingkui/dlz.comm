# 🎯 JSONMap 功能导航 - 从入门到精通

[返回首页](../README.md)

---

## 📊 功能分级

### 🔥 核心特色功能（引流必看）

这些是 JSONMap 最吸引人的独创功能，也是与其他 JSON 库最大的差异化优势：

#### 1. 深层路径直达 ⭐⭐⭐⭐⭐
**一行代码穿透任意层级，告别判空地狱**

```java
// ❌ 传统方式：20 行代码
Map<String, Object> data = getData();
String city = null;
if (data != null && data.get("user") != null) {
    Map user = (Map) data.get("user");
    if (user.get("profile") != null) {
        Map profile = (Map) user.get("profile");
        if (profile.get("addresses") != null) {
            List addresses = (List) profile.get("addresses");
            if (!addresses.isEmpty()) {
                Map addr = (Map) addresses.get(0);
                city = (String) addr.get("city");
            }
        }
    }
}

// ✅ JSONMap 方式：1 行代码
String city = new JSONMap(data).getStr("user.profile.addresses[0].city");
```

**支持的路径语法：**
- `user.name` - 点号访问嵌套对象
- `items[0]` - 数组索引访问
- `items[-1]` - 负数索引（倒数第一个）
- `users[0].profile.tags[-1]` - 混合路径

📖 [完整文档](./jsonmap-jsonlist.md#深层路径访问)

---

#### 2. 智能层级构建 ⭐⭐⭐⭐⭐
**意念构建复杂结构，自动创建中间层级**

```java
// ❌ 传统方式：需要手动创建每一层
Map<String, Object> config = new HashMap<>();
Map<String, Object> server = new HashMap<>();
server.put("port", 8080);
server.put("host", "localhost");
config.put("server", server);

Map<String, Object> db = new HashMap<>();
Map<String, Object> master = new HashMap<>();
master.put("url", "jdbc:mysql://...");
db.put("master", master);
config.put("db", db);

// ✅ JSONMap 方式：自动创建所有层级
JSONMap config = new JSONMap()
    .set("server.port", 8080)
    .set("server.host", "localhost")
    .set("db.master.url", "jdbc:mysql://...");
```

**支持的构建模式：**
- `a.b.c` - 自动创建嵌套对象
- `arr[0]` - 自动创建数组
- `arr[5]` - 自动补齐中间元素（填充 null）
- `arr[-1]` - 负数索引修改
- `a[0][1].c` - 多维数组
- `a.b[0].c[1].d` - 复杂混合路径

📖 [完整文档](./jsonmap-jsonlist.md#层级构建)

---

#### 3. 全能类型转换 ⭐⭐⭐⭐⭐
**源数据是什么不重要，你想要什么类型就给你什么**

```java
JSONMap data = new JSONMap();
data.put("score", "99.5");      // 存的是字符串

// 随心所欲地取
data.getInt("score");           // → 99（自动转换并截断）
data.getDouble("score");        // → 99.5
data.getBigDecimal("score");    // → 99.5（精确计算）
data.getStr("score");           // → "99.5"
```

**容错性极强：**
```java
// 前端传的类型乱七八糟？自动纠正
JSONMap form = new JSONMap("{\"age\":\"25\"}");  // 字符串
Integer age = form.getInt("age");                 // → 25（自动转整数）

// Bean 转换也能自动纠正类型
User user = form.as(User.class);  // age 字段自动从 String 转 Integer
```

📖 [完整文档](./jsonmap-jsonlist.md#类型转换)

---

#### 4. 负索引访问 ⭐⭐⭐⭐
**Python 风格的数组访问，优雅获取倒数元素**

```java
JSONMap data = new JSONMap("{\"tags\":[\"a\",\"b\",\"c\",\"d\",\"e\"]}");

data.getStr("tags[0]");    // → "a"（第一个）
data.getStr("tags[-1]");   // → "e"（最后一个）
data.getStr("tags[-2]");   // → "d"（倒数第二个）

// 在 set 中也能用
data.set("tags[-1]", "updated");  // 修改最后一个元素
```

📖 [完整文档](./jsonmap-jsonlist.md#负索引访问)

---

### 💎 实用功能（日常开发必备）

#### 5. 多种构造方式 ⭐⭐⭐⭐
**怎么方便怎么来**

```java
// 标准 JSON
new JSONMap("{\"name\":\"张三\",\"age\":25}");

// 简化 JSON（key 不加引号）
new JSONMap("{name:'张三',age:25}");

// 带注释的 JSON
new JSONMap("{\n" +
    "  name: '张三',  // 用户姓名\n" +
    "  age: 25        // 用户年龄\n" +
    "}");

// 键值对构造（最简洁）
new JSONMap("name", "张三", "age", 25, "city", "北京");

// 从 Bean 转换
new JSONMap(userObject);
```

📖 [完整文档](./jsonmap-jsonlist.md#构造方式)

---

#### 6. 链式操作 ⭐⭐⭐⭐
**流畅的 API 设计**

```java
JSONMap request = new JSONMap()
    .set("header.version", "1.0")
    .set("header.timestamp", System.currentTimeMillis())
    .set("body.user.name", userName)
    .set("body.user.email", email)
    .add("body.user.tags", "vip")
    .add("body.user.tags", "active");
```

📖 [完整文档](./jsonmap-jsonlist.md#实战场景)

---

#### 7. 智能数组追加 ⭐⭐⭐
**自动创建数组并追加元素**

```java
JSONMap config = new JSONMap()
    .add("whitelist", "192.168.1.1")    // 自动创建数组
    .add("whitelist", "192.168.1.2")    // 自动追加
    .add("whitelist", "192.168.1.3");

// 结果：{"whitelist": ["192.168.1.1", "192.168.1.2", "192.168.1.3"]}
```

📖 [完整文档](./jsonmap-jsonlist.md#层级构建)

---

### 🎁 功能彩蛋（老司机专区）

这些是低频但惊艳的高级功能，适合特定场景使用：

#### 8. 多维数组支持 ⭐⭐⭐
**处理复杂的矩阵数据**

```java
JSONMap json = new JSONMap();

// 构建二维数组
json.set("matrix[0][0]", 1);
json.set("matrix[0][1]", 2);
json.set("matrix[1][0]", 3);
json.set("matrix[1][1]", 4);

// 结果：{"matrix":[[1,2],[3,4]]}

// 访问
json.getInt("matrix[0][1]");  // → 2
json.getInt("matrix[1][0]");  // → 3
```

**更复杂的场景：**
```java
// 三维数组带属性
json.set("data[0][0].value", "A1");
json.set("data[0][1].value", "A2");
json.set("data[1][0].value", "B1");

// 访问
json.getStr("data[0][1].value");  // → "A2"
```

📖 [完整文档](./jsonmap-jsonlist.md#层级构建)

---

#### 9. 数组自动补齐 ⭐⭐⭐
**跳过索引自动填充 null**

```java
JSONMap json = new JSONMap();

// 直接设置索引 5，自动补齐前面的元素
json.set("arr[5]", "value5");

// 结果：{"arr":[null,null,null,null,null,"value5"]}

JSONList arr = json.getList("arr");
arr.size();  // → 6
```

📖 [完整文档](./jsonmap-jsonlist.md#层级构建)

---

#### 10. 逗号分隔字符串转数组 ⭐⭐⭐
**自动识别并拆分**

```java
// 存储逗号分隔的字符串
JSONMap data = new JSONMap("{\"tags\":\"java,python,go\"}");

// 自动拆分为数组
List<String> tags = data.getList("tags", String.class);
// → ["java", "python", "go"]

// 也支持数字
JSONMap nums = new JSONMap("{\"ids\":\"1,2,3,4,5\"}");
List<Integer> ids = nums.getList("ids", Integer.class);
// → [1, 2, 3, 4, 5]
```

📖 [完整文档](./jsonmap-jsonlist.md#api-参考)

---

#### 11. Bean 类型自动纠正 ⭐⭐⭐
**前端传的类型不对？自动修正**

```java
// 前端传的数据类型不对
JSONMap form = new JSONMap("{\"age\":\"25\",\"score\":\"99.5\"}");

// 转换为 Bean 时自动纠正类型
User user = form.as(User.class);
// age 字段：String "25" → Integer 25
// score 字段：String "99.5" → Double 99.5
```

**容错性极强：**
```java
JSONMap data = new JSONMap();
data.put("a", "not a number");

// 转换失败时返回 null，不抛异常
User user = data.as(User.class);  // → null
```

📖 [完整文档](./jsonmap-jsonlist.md#类型转换)

---

#### 12. 清除空属性 ⭐⭐
**一键清理 null 和空字符串**

```java
JSONMap data = new JSONMap();
data.put("name", "张三");
data.put("empty1", "");
data.put("empty2", null);
data.put("valid", "有效值");

data.clearEmptyProp();

// 结果：{"name":"张三","valid":"有效值"}
```

📖 [完整文档](./jsonmap-jsonlist.md#api-参考)

---

#### 13. 子对象修改影响原对象 ⭐⭐
**获取的子 Map 是引用，不是副本**

```java
JSONMap data = new JSONMap("{\"user\":{\"name\":\"张三\",\"age\":25}}");

// 获取子对象
JSONMap user = data.getMap("user");

// 修改子对象
user.put("age", 30);

// 原对象也被修改
data.getInt("user.age");  // → 30
```

📖 [完整文档](./jsonmap-jsonlist.md#实战场景)

---

#### 14. 支持 BigDecimal 精确计算 ⭐⭐
**金额计算必备**

```java
JSONMap data = new JSONMap();
data.put("amount", "99.99");

// 获取 BigDecimal 进行精确计算
BigDecimal amount = data.getBigDecimal("amount");
BigDecimal tax = amount.multiply(new BigDecimal("0.13"));
```

📖 [完整文档](./jsonmap-jsonlist.md#api-参考)

---

#### 15. 混合路径的极限挑战 ⭐⭐
**处理最复杂的嵌套结构**

```java
JSONMap json = new JSONMap();

// 构建超复杂结构
json.set("app.servers[0].clusters[0].nodes[0].ip", "192.168.1.1");
json.set("app.servers[0].clusters[0].nodes[0].ports[0]", 8080);
json.set("app.servers[0].clusters[0].nodes[0].ports[1]", 8081);
json.set("app.servers[0].clusters[1].nodes[0].ip", "192.168.1.2");

// 访问
String ip = json.getStr("app.servers[0].clusters[0].nodes[0].ip");
Integer port = json.getInt("app.servers[0].clusters[0].nodes[0].ports[1]");
```

📖 [完整文档](./jsonmap-jsonlist.md#实战场景)

---

#### 16. @SetValue 注解 - 扁平 Bean ↔ 嵌套 JSON 映射 ⭐⭐⭐⭐⭐
**用扁平的代码结构操作嵌套的数据结构**

```java
// 代码里用扁平 Bean（有 IDE 提示，好维护）
@Data
public class User {
    private String name;
    
    @SetValue("info")
    private String phone;
    
    @SetValue("info")
    private String address;
}

// 存储/传输时自动转成嵌套 JSON
User user = new User();
user.setName("张三");
user.setPhone("13800138000");

JSONMap json = new JSONMap();
BeanUtil.copyAsSource(user, json, false);

// 结果：{"name":"张三","info":{"phone":"13800138000","address":"北京"}}
```

**典型应用场景：**
- 数据库 JSON 字段 + Excel 导入导出
- 前后端结构差异适配
- 多表数据合并
- 配置中心/动态配置
- ES/MongoDB 文档映射
- 数据迁移 / ETL
- 低代码/表单设计器

📖 [完整文档](./bean-mapping.md)

---

## 🗺️ 学习路径推荐

### 新手入门（5 分钟）
1. [README 快速开始](../README.md#-30-秒快速开始)
2. [核心概念](./core.md#-3-秒感受降维打击)
3. [基本构造方式](./jsonmap-jsonlist.md#构造方式)

### 进阶使用（15 分钟）
1. [深层路径访问](./jsonmap-jsonlist.md#深层路径访问)
2. [层级构建](./jsonmap-jsonlist.md#层级构建)
3. [类型转换](./jsonmap-jsonlist.md#类型转换)
4. [实战场景](./jsonmap-jsonlist.md#实战场景)

### 高级技巧（30 分钟）
1. [多维数组](#8-多维数组支持-)
2. [数组自动补齐](#9-数组自动补齐-)
3. [混合路径极限挑战](#15-混合路径的极限挑战-)
4. [最佳实践](./jsonmap-jsonlist.md#最佳实践)

---

## 📚 完整文档索引

### 核心文档
- [📘 JSONMap & JSONList 完整指南](./jsonmap-jsonlist.md) - 所有 API 和实战场景
- [📗 核心工具集概览](./core.md) - 快速入门和核心概念
- [🏠 项目主页](../README.md) - 项目介绍和快速开始

### 应用场景
- [🎯 应用场景与痛点分析](./scenarios.md) - 四大核心场景、痛点对比、适用项目类型
- [🔥 @SetValue 注解指南](./bean-mapping.md) - 扁平 Bean ↔ 嵌套 JSON 双向映射

### 工具类文档
- [🔧 JacksonUtil](./jacksonutil.md) - JSON 序列化与路径取值
- [📅 DateUtil](./dateutil.md) - 日期时间处理
- [🔤 StringUtils](./stringutils.md) - 字符串处理
- [🔄 ValUtil](./valutil.md) - 类型转换工具
- [💾 Cache](./cache.md) - 缓存工具

---

## 🎯 功能对比表

| 功能 | 传统方式 | FastJSON | Jackson | **JSONMap** |
|------|---------|----------|---------|-------------|
| 深层取值 | 10+ 行代码 | 链式 get | 链式 get | **一行路径** ⭐ |
| 空值安全 | 需手动判断 | 部分支持 | 部分支持 | **完全安全** ⭐ |
| 类型转换 | 手动强转 | 需指定类型 | 需指定类型 | **自动推断** ⭐ |
| 构建结构 | 多层 put | 多层 put | ObjectNode | **路径 set** ⭐ |
| 负索引 | ❌ | ❌ | ❌ | **✅ 支持** ⭐ |
| 多维数组 | ❌ | ❌ | ❌ | **✅ 支持** ⭐ |
| 数组补齐 | ❌ | ❌ | ❌ | **✅ 自动** ⭐ |
| 带注释 JSON | ❌ | ❌ | 需配置 | **✅ 原生** ⭐ |
| 类型纠正 | ❌ | 部分 | 部分 | **✅ 完全** ⭐ |

---

## 💡 使用建议

### 什么时候用 JSONMap？

✅ **推荐使用：**
- API 响应解析（特别是嵌套深的）
- 动态构建请求体
- 配置文件处理
- 表单数据处理
- 数据转换和清洗
- 临时数据结构构建

❌ **不推荐使用：**
- 性能极致要求的场景（虽然性能已经很好）
- 需要严格类型检查的场景
- 数据结构固定且简单的场景

### 性能说明

- **底层实现**：基于 HashMap，性能与原生 Map 一致
- **路径解析**：首次解析后会缓存，后续访问很快
- **类型转换**：基于 Jackson，性能优秀
- **内存占用**：与普通 Map 相当

---

<div align="center">

**从入门到精通，从基础到彩蛋，JSONMap 让 Java 数据处理更优雅！**

如果觉得有帮助，请点个 ⭐ Star 支持一下！

[返回首页](../README.md) | [完整文档](./jsonmap-jsonlist.md) | [GitHub](https://github.com/dingkui/dlz.comm)

</div>
