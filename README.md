# 🗡️ JSONMap —— Java 数据操作的瑞士军刀
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![JDK](https://img.shields.io/badge/JDK-8%20%7C%2011%20%7C%2017%20%7C%2021-green.svg)](https://www.oracle.com/java/)
[![Size](https://img.shields.io/badge/Size-~100KB-brightgreen.svg)]()
[![Dependency](https://img.shields.io/badge/Dependency-jackson--databind%20only-orange.svg)]()
> **一行代码穿透复杂 JSON，告别 Java 数据处理的"体力活"**
```java
// 以前要写 10 行的判空+强转，现在 1 行搞定
String city = json.getStr("user.profile.addresses[0].city");
```
---
## ⚡ 为什么选择 JSONMap？
|      特性      | 说明                                  |
|:------------:|:------------------------------------|
| 🪶 **极致轻量**  | 仅 **~100KB**，不给项目增加负担               |
| 🔗 **唯一依赖**  | 只依赖 `jackson-databind`（你的项目大概率已经有了） |
| 🛡️ **0 侵入** | 不改任何现有代码，想用就用，不想用就删                 |
| ☕ **全版本兼容**  | JDK 8 / 11 / 17 / 21 全部支持           |
| 🚀 **即插即用**  | 加一行依赖，2 分钟上手                        |
| 😄 **熟悉的味道** | Ta就是你熟悉的HashMap，套了个马甲就身价百倍了         |
---
## 🚀 30 秒快速开始
### 1. 添加依赖
```xml
<dependency>
    <groupId>com.dlz</groupId>
    <artifactId>dlz-comm</artifactId>
    <version>6.5.1</version>
</dependency>
```
### 2. 开始使用
```java
// 就这样，没有任何配置，没有任何初始化
//yourJsonString: {"user":{"name":"张三","profile":{"addresses":[{"city":"上海"}]}}}";
JSONMap data = new JSONMap(yourJsonString);
String name = data.getStr("user.name");// → "张三"
String city = data.getStr("user.profile.addresses[0].city");// → "上海"
```
**没错，就这么简单。**
---
## 💥 3 秒感受降维打击
### ❌ 传统 Java 方式
```java
// 从 API 响应中获取用户的城市信息
Map<String, Object> response = getApiResponse();
String city = null;
if (response != null) {
    Object data = response.get("data");
    if (data instanceof Map) {
        Object user = ((Map) data).get("user");
        if (user instanceof Map) {
            Object profile = ((Map) user).get("profile");
            if (profile instanceof Map) {
                Object addresses = ((Map) profile).get("addresses");
                if (addresses instanceof List && !((List) addresses).isEmpty()) {
                    Object firstAddr = ((List) addresses).get(0);
                    if (firstAddr instanceof Map) {
                        city = (String) ((Map) firstAddr).get("city");
                    }
                }
            }
        }
    }
}
```
### ✅ JSONMap 方式
```java
String city = new JSONMap(response).getStr("data.user.profile.addresses[0].city");
```
> **12 行 → 1 行，代码量减少 90%**
---
## 🛠️ 独创三大“神兵利器”
JSONMap 将复杂操作封装为三个核心能力，直击 Java 开发痛点。
### 1. 🎯 穿云箭：深层路径直达 (Deep Path Access)
**独创亮点**：无需任何中间对象，直接通过**点号**和**下标**穿透任意层级。
*   **智能判空**：路径中任意一环为 null，直接返回 null，绝无 NPE。
*   **支持负索引**：`tags[-1]` 直接获取数组倒数第一个元素（Python 风格）。
*   **混合解析**：完美处理 `Map` 中嵌套 `List`，`List` 中嵌套 `Map` 的复杂场景。
```java
// 获取倒数第二个历史记录的时间戳
Long time = map.getLong("history.logs[-2].timestamp"); 
```
### 2. 🏗️ 这里的“独创”：意念构建 (Hierarchical Construction)
**独创亮点**：想构造 `{"a":{"b":{"c":1}}}`？别再 new 三个 HashMap 了！
*   **自动铺路**：只要你给路径，中间缺 Map 补 Map，缺 List 补 List。
*   **智能合并**：路径已存在？自动识别是“覆盖”还是“合并”。
```java
// ✨ 魔法般的构建过程
JSONMap config = new JSONMap()
    .set("server.port", 8080)             // 自动创建 server 对象
    .set("db.master.ip", "192.168.1.1")   // 自动创建 db 和 master 对象
    .add("users", "admin") ;      // 自动创建 users 数组
// 结果：{"server":{"port":8080},"db":{"master":{"ip":"192.168.1.1"}},"users":["admin"]}
```
### 3. 🧬 变形金刚：全能类型转换 (Universal Type Casting)
**独创亮点**：源数据是什么类型不重要，重要的是**你想要什么类型**。
*   **容错性极强**：数据库存的是 String "100"，代码想要 int 100？自动转。
*   **对象映射**：想把某个子节点直接转成 Java Bean？一键搞定。
```java
// 源数据：{"score": "99.5", "info": {...}}
BigDecimal score = map.getBigDecimal("score"); // 自动解析字符串为数字
User user = map.getObj("info", User.class);     // 子节点直接转对象
```
---
## 🔄 Bean 无缝互转
**不是替代 Bean，而是增强 Bean！**
```java
// Bean → JSONMap（需要临时加字段？没问题）
User user = getUser();
JSONMap data = new JSONMap(user)
    .set("extra.loginTime", System.currentTimeMillis())
    .set("extra.source", "API");
// JSONMap → Bean（前端传的类型乱七八糟？自动纠正）
JSONMap params = new JSONMap(request.getBody());
// 即使前端传 {"age": "25"}（字符串），也能正确转成 Integer
User user = params.as(User.class);
```
---
## 📦 工具三剑客
| 工具 | 用途 | 一句话介绍 |
|:---:|:---:|:---|
| **JSONMap** | Map 增强 | 深层取值 + 链式构建 + 类型转换 |
| **JSONList** | List 增强 | 支持负索引 + 类型安全访问 |
| **ValUtil** | 类型转换 | 万能转换器，空值安全 |
---
## 🆚 和主流库的定位差异
| 库 | 定位 | 擅长 | JSONMap 的差异 |
|:---:|:---:|:---|:---|
| Jackson | 序列化 | JSON ↔ 字符串 | JSONMap 解决的是**拿到数据后怎么操作** |
| FastJSON | 序列化 | 高性能 | 深层取值仍需链式 `.getJSONObject().get()` |
| JSONPath | 查询 | 复杂查询语法 | JSONMap 能读也能写，还能转 Bean |
| Hutool JSONUtil | 工具集 | 功能全 | JSONMap 更聚焦，体积更小 |
> **Jackson 负责"搬运"，JSONMap 负责"加工"，完美互补。**
---
## 📖 详细文档
- [📘 三剑客 完整指南](docs/core.md) - 所有 API 和使用技巧
- [🎁 隐藏宝藏](./docs/JSONMap.md#-隐藏宝藏老司机专区) - 低频但惊艳的高级功能
---
## 💬 常见问题
<details>
<summary><b>Q: 我的项目没有 Jackson 怎么办？</b></summary>
添加 dlz.comm自动引入Jackson，大多数 Spring 项目默认就有：
```xml
 <dependency>
            <groupId>top.dlzio</groupId>
            <artifactId>dlz.comm</artifactId>
            <version>6.5.1</version>
 </dependency>
```
</details>
<details>
<summary><b>Q: 性能怎么样？</b></summary>
底层就是 `LinkedHashMap`，性能与原生 Map 一致。路径解析有缓存优化。
</details>
<details>
<summary><b>Q: 会和现有代码冲突吗？</b></summary>
不会。JSONMap 继承自 `LinkedHashMap`，可以当普通 Map 使用，完全兼容。
</details>
<details>
<summary><b>Q: Spring Boot 2.x / 3.x 都支持吗？</b></summary>
都支持。JSONMap 不依赖 Spring，只依赖 Jackson。
</details>
---
## 🌟 Star History
如果觉得有帮助，请点个 ⭐ 支持一下！
---
<div align="center">
**用 1 行代码做完 10 行的事，这就是 JSONMap。**
Made with ❤️ by DLZ
</div>