# 快速失败原则 (Fail-Fast Principle)

## 什么是快速失败？

快速失败是一种软件设计理念：**当检测到错误时，立即抛出异常，而不是静默处理或返回默认值**。

## 用故事理解快速失败
想象你在找一个"变形师傅"帮你做类型转换：

1. **有效值** → 找到你了，该你表演了！
   - "变个猴子" → "行，我干！" ✅ 
   - "变成狗" → "也行！" ✅ 
   - ...对的你，干对的事，真香 ！🍗
   - **结果**：正常转换


2. **无效值** → 找的就是你，但你干不了这活！
   - "变成梁朝伟" → "什么？这个真不行，颜值不够！！" ❌
   - "变xxx" → "变你大爷，我又不是魔术师！！" ❌
   - **结果**：报警！报警！出大事了，要么人找错了，要么事错了！→ 抛出异常
   
3. **null（你还没出生）** → 好不容易找到你家，发现这个家根本就没有你
   - **结果**：没事，有备胎 → 返回 `defaultV`

4. **null（魂魄）** → 好不容易找到你家，发现你在家，但你只是一缕魂魄
   - **结果**：没事，有备胎 → 返回 `defaultV`

5. **null（路径不存在）** → 找到你的国家，找到你的省，但你说的城市不存在
   - **结果**：找不到你了，没事，有备胎 → 返回 `defaultV`

**核心思想**：
- **找不到（null/空）** → 预期内的缺失，有备胎用备胎，没备胎？我是上帝，最好别惹我！
- **找错了（类型不匹配）** → 预期外的错误，拉警报，备胎也不管用，重要事情说3遍：搞错了就得上报！！！

## 为什么要快速失败？

### 1. 及早发现问题

```java
// ❌ 静默失败 - 问题被隐藏
Integer age = ValUtil.toInt("abc", 0);  // 返回 0，但 "abc" 根本不是数字
saveUser(age);  // 保存了错误数据，问题延后到数据库或业务逻辑

// ✅ 快速失败 - 问题立即暴露
Integer age = ValUtil.toInt("abc");  // 立即抛出 NumberFormatException
// 开发者马上知道数据有问题，可以立即修复
```

### 2. 明确的契约

```java
// 方法契约：toInt() 将对象转换为 Integer
// - 如果对象可以转换 → 返回 Integer
// - 如果对象不能转换 → 抛出异常
// - 如果对象是 null 或空字符串 → 返回 defaultV（这是明确的预期行为）

Integer result = ValUtil.toInt("123");     // ✅ 返回 123
Integer result = ValUtil.toInt(null, 0);   // ✅ 返回 0（null 是预期的）
Integer result = ValUtil.toInt("", 0);     // ✅ 返回 0（空字符串是预期的）
Integer result = ValUtil.toInt("abc");     // ❌ 抛出异常（"abc" 不能转换为 int）
```

### 3. 避免错误传播

```java
// ❌ 静默失败导致错误传播
String userInput = "abc";  // 用户输入错误
Integer quantity = ValUtil.toInt(userInput, 0);  // 返回 0
int total = quantity * price;  // 计算错误的总价
order.setTotal(total);  // 保存错误数据
// 问题：错误数据进入系统，可能影响报表、统计、业务决策

// ✅ 快速失败阻止错误传播
String userInput = "abc";
try {
    Integer quantity = ValUtil.toInt(userInput);  // 立即抛出异常
    int total = quantity * price;
    order.setTotal(total);
} catch (NumberFormatException e) {
    // 在入口处处理错误，返回友好提示
    return "请输入有效的数量";
}
```

### 4. 更容易调试

```java
// ❌ 静默失败 - 难以定位问题
Integer value = ValUtil.toInt(data.get("amount"), 0);  // 返回 0
// ... 100 行代码后
if (value == 0) {
    // 这里出错了，但不知道是：
    // 1. amount 本来就是 0？
    // 2. amount 是 null？
    // 3. amount 是无效字符串？
    // 4. data 里没有 amount 字段？
}

// ✅ 快速失败 - 精确定位问题
Integer value = ValUtil.toInt(data.get("amount"));  
// 如果出错，异常堆栈直接指向这一行
// 开发者立即知道：data.get("amount") 返回了无效值
```

## ValUtil 的快速失败实现

### 规则

1. **有效值** → 正常转换
2. **无效值** → 抛出异常（NumberFormatException、DateTimeParseException 等）
3. **null** → 容器中的值是空，或者不存在这个值 → 返回 `defaultV`（这是明确的预期行为）
4. **null** → 容器不存在 → 返回 `defaultV`（这是明确的预期行为）

### 示例

```java
// null 和空字符串 - 返回默认值
ValUtil.toInt(null);           // 返回 null
ValUtil.toInt(null, 0);        // 返回 0
ValUtil.toInt("");             // 返回 null
ValUtil.toInt("", 0);          // 返回 0

// 有效值 - 正常转换
ValUtil.toInt("123");          // 返回 123
ValUtil.toInt(123);            // 返回 123
ValUtil.toInt(123.45);         // 返回 123（Number 类型）

// 无效值 - 抛出异常
ValUtil.toInt("abc");          // ❌ NumberFormatException
ValUtil.toInt("12.34");        // ❌ NumberFormatException
ValUtil.toDouble("xyz");       // ❌ NumberFormatException
ValUtil.toDate("invalid");     // 返回 null（日期解析失败）
```

## 何时使用默认值？

默认值应该用于**明确的预期场景**，而不是用来掩盖错误：

```java
// ✅ 正确使用默认值
Integer pageSize = ValUtil.toInt(request.getParameter("pageSize"), 10);
// 如果用户没有传 pageSize，使用默认值 10 是合理的

// ❌ 错误使用默认值
Integer userId = ValUtil.toInt(request.getParameter("userId"), 0);
// 如果 userId 是必需的，应该让它抛出异常，而不是使用默认值 0
// 正确做法：
Integer userId = ValUtil.toInt(request.getParameter("userId"));
if (userId == null) {
    throw new IllegalArgumentException("userId is required");
}
```

## 对比其他库

### Apache Commons Lang

```java
// NumberUtils.toInt() - 静默失败
int value = NumberUtils.toInt("abc", 0);  // 返回 0，错误被隐藏
```

### ValUtil

```java
// ValUtil.toInt() - 快速失败
Integer value = ValUtil.toInt("abc");  // 抛出异常，问题立即暴露
```

## 总结

快速失败原则的核心思想：

1. **错误应该尽早暴露**，而不是被隐藏
2. **方法契约应该明确**：能转换就转换，不能转换就报错
3. **默认值只用于预期场景**（如 null、空字符串），不用于掩盖错误
4. **让调用者决定如何处理错误**，而不是库替调用者做决定

这种设计让代码更健壮、更容易调试、更不容易出现隐藏的 bug。

---
## 🌟 Star History
如果觉得有帮助，请点个 ⭐ 支持一下！
>  写代码其实是快乐的，是在混乱中建立秩序，在空白中赋予生命 —— 当思想和逻辑完美契合，代码如流水般自然流淌，这种感觉很妙！
<div align="center">
Made with ❤️ by DLZ
</div>
