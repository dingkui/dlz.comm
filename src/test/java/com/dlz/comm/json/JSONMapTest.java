package com.dlz.comm.json;

import com.dlz.comm.exception.SystemException;
import com.dlz.comm.util.JacksonUtil;
import com.dlz.comm.util.ValUtil;
import com.dlz.test.beans.AA;
import com.dlz.test.beans.TestBean;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JSONMap单元测试类
 * <p>
 * 测试JSONMap的各种功能，包括构造函数、类型转换、层级操作等
 *
 * @author dk
 */
@DisplayName("JSONMap单元测试")
class JSONMapTest {

    @Nested
    @DisplayName("构造函数测试")
    class ConstructorTests {
        @Test
        @DisplayName("无参构造函数测试")
        void testDefaultConstructor() {
            JSONMap jsonMap = new JSONMap();
            assertNotNull(jsonMap);
            assertTrue(jsonMap.isEmpty());

            assertEquals("{}", jsonMap.toString());


            //输出：{}
            jsonMap.put("a", 1);
            assertEquals("{\"a\":1}", jsonMap.toString());
            //输出：{"a":1}

            jsonMap.put("a", "1");
            assertEquals("{\"a\":\"1\"}", jsonMap.toString());
            //输出：{"a":"1"}
        }

        @Test
        @DisplayName("Map对象构造函数测试")
        void testMapConstructor() {
            Map<String, Object> map = new HashMap<>();
            map.put("name", "张三");
            map.put("age", 25);
            map.put("active", true);

            JSONMap jsonMap = new JSONMap(map);
            assertEquals("张三", jsonMap.getStr("name"));
            assertEquals(Integer.valueOf(25), jsonMap.getInt("age"));
            assertEquals(Boolean.TRUE, jsonMap.getBoolean("active"));

            JSONMap params = new JSONMap("{a:\"1\",b:2}");
            // 即使前端传 {"age": "25"}（字符串），也能正确转成 Integer
            TestBean user = params.as(TestBean.class);
            assertEquals(1, user.getA());
            assertEquals("2", user.getB());
        }


        @Test
        @DisplayName("null对象构造函数测试")
        void testNullConstructor() {
            JSONMap jsonMap = new JSONMap((Object) null);
            assertNotNull(jsonMap);
            assertTrue(jsonMap.isEmpty());

            // 测试null字符串构造函数
            jsonMap = new JSONMap((CharSequence) null);
            assertNotNull(jsonMap);
            assertTrue(jsonMap.isEmpty());
        }

        @Test
        @DisplayName("JSON字符串构造函数测试")
        void testCharSequenceConstructor() {
            String jsonString = "{\"name\":\"李四\",\"score\":85.5,\"age\":30}";
            JSONMap jsonMap = new JSONMap(jsonString);

            assertEquals(jsonString, jsonMap.toString());

            assertEquals("李四", jsonMap.getStr("name"));
            assertEquals(Integer.valueOf(30), jsonMap.getInt("age"));
            assertEquals(Double.valueOf(85.5), jsonMap.getDouble("score"));


            String jsonString2 = "{\"a\":{\"b\":1}}";

            jsonMap = new JSONMap(jsonString2);
            assertEquals(jsonString2, jsonMap.toString());
            assertEquals(1, jsonMap.getInt("a.b"));
            //输出：{"a":{"b":1}}

            jsonMap.put("b", "3");
            assertEquals("{\"a\":{\"b\":1},\"b\":\"3\"}", jsonMap.toString());
            assertEquals(1, jsonMap.getInt("a.b"));
            assertEquals(3, jsonMap.getInt("b"));
            //输出：{"a":{"b":2},"b":"2"}

            jsonMap.set("c.c1", "666");
            assertEquals("{\"a\":{\"b\":1},\"b\":\"3\",\"c\":{\"c1\":\"666\"}}", jsonMap.toString());
            assertEquals(1, jsonMap.getInt("a.b"));
            assertEquals(3, jsonMap.getInt("b"));
            assertEquals(666, jsonMap.getInt("c.c1"));
            assertEquals("666", jsonMap.getStr("c.c1"));
            //输出：{"a":{"b":2},"b":"2","c":{"c1":"666"}}


            //参数为JSON字符串(简化版：key不包含双引号)
            jsonMap = new JSONMap("{a:{b:1}}");
            assertEquals(jsonString2, jsonMap.toString());
            //输出：{"a":{"b":1}}


            //参数为JSON字符串 带注释
            jsonMap = new JSONMap("{\n" +
                    "    a: { //测试\n" +
                    "        \"b\": 1 //测试\n" +
                    "    }\n" +
                    "}");
            assertEquals(jsonString2, jsonMap.toString());
            //输出：{"a":{"b":1}}

        }

        @Test
        @DisplayName("无效JSON字符串构造函数测试")
        void testInvalidJsonConstructor() {
            String invalidJson = "{invalid json}";
            assertThrows(SystemException.class, () -> new JSONMap(invalidJson));
        }


        @Test
        @DisplayName("键值对构造函数测试")
        void testKeyValueConstructor() {
            // 测试参数个数只能是偶数
            assertThrows(SystemException.class, () ->
                    new JSONMap("name", "赵六", "age", 25, "city"));

            // 测试参数类型错误:键名必须为String
            assertThrows(SystemException.class, () ->
                    new JSONMap("name", "孙七", 123, "invalid"));

            JSONMap jsonMap = new JSONMap("name", "王五", "age", 28, "city", "北京");

            assertEquals("王五", jsonMap.getStr("name"));
            assertEquals(Integer.valueOf(28), jsonMap.getInt("age"));
            assertEquals("北京", jsonMap.getStr("city"));

            //同样的键值被后面的覆盖
            jsonMap = new JSONMap("name", "王五", "age", 28, "city", "北京", "city", "不知道");
            assertEquals("不知道", jsonMap.getStr("city"));
        }

        /**
         * 参数为对象
         */
        @Test
        @DisplayName("参数为对象构造函数测试")
        public void testObjectConstructor() {
            TestBean arg = new TestBean();
            arg.setA(1);
            arg.setB("2");
            JSONMap jsonMap = new JSONMap(arg);
            System.out.println(jsonMap);
            assertEquals("{\"a\":1,\"b\":\"2\"}", jsonMap.toString());
            //输出：{"a":1,"b":"2"}
            jsonMap.put("c", "1");
            assertEquals("{\"a\":1,\"b\":\"2\",\"c\":\"1\"}", jsonMap.toString());
            //输出：{"a":1,"b":"2","c":"1"}
        }
    }

    @Nested
    @DisplayName("静态工厂方法测试")
    class StaticFactoryTests {

        @Test
        @DisplayName("createJsonMap方法测试")
        void testCreateJsonMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("id", 1);
            map.put("title", "测试标题");

            JSONMap jsonMap = JSONMap.createJsonMap(map);
            assertEquals(Integer.valueOf(1), jsonMap.getInt("id"));
            assertEquals("测试标题", jsonMap.getStr("title"));
        }
    }

    @Nested
    @DisplayName("类型转换方法测试")
    class TypeConversionTests {
        @Test
        @DisplayName("asMap方法测试")
        void testAsMap() {
            JSONMap jsonMap = new JSONMap();
            jsonMap.put("user1", new JSONMap("name", "用户1"));
            jsonMap.put("user2", new JSONMap("name", "用户2"));

            Map<String, JSONMap> resultMap = jsonMap.asMap();
            assertEquals("用户1", resultMap.get("user1").getStr("name"));
            assertEquals("用户2", resultMap.get("user2").getStr("name"));
        }

        @Test
        @DisplayName("asMapList方法测试")
        void testAsMapList() {
            JSONMap jsonMap = new JSONMap();
            jsonMap.put("list1", Arrays.asList(new JSONMap("item", "项目1")));
            jsonMap.put("list2", Arrays.asList(new JSONMap("item", "项目2")));

            Map<String, JSONList> resultMap = jsonMap.asMapList();
            assertEquals(1, resultMap.get("list1").size());
            assertEquals("项目1", resultMap.get("list1").getMap(0).getStr("item"));
        }

        @Test
        @DisplayName("asMap指定类型方法测试")
        void testAsMapWithType() {
            JSONMap jsonMap = new JSONMap();
            jsonMap.put("str1", "字符串1");
            jsonMap.put("str2", "字符串2");

            Map<String, String> resultMap = jsonMap.asMap(String.class);
            assertEquals("字符串1", resultMap.get("str1"));
            assertEquals("字符串2", resultMap.get("str2"));
        }

        @Test
        @DisplayName("as指定类型方法测试")
        void testAsType() {
            JSONMap paras = new JSONMap();
            paras.put("a", "12");
            paras.put("b", 2);
            //类型不匹配可以自动纠正装换
            TestBean as = paras.as(TestBean.class);
            assertEquals(12, as.getA()); //输出：12
            assertEquals("2", as.getB());//输出：2


            paras.put("a", "not a number");
            //有错误类型无法转换的，as结果为null
            as = paras.as(TestBean.class);
            assertNull(as); //转换失败，输出null
        }
    }

    @Nested
    @DisplayName("数据清理方法测试")
    class DataCleaningTests {

        @Test
        @DisplayName("clearEmptyProp方法测试")
        void testClearEmptyProp() {
            JSONMap jsonMap = new JSONMap();
            jsonMap.put("name", "有效值");
            jsonMap.put("empty1", "");
            jsonMap.put("empty2", null);
            jsonMap.put("notEmpty3", "   ");

            assertEquals(4, jsonMap.size());
            jsonMap.clearEmptyProp();
            assertEquals(2, jsonMap.size());
            assertEquals("有效值", jsonMap.getStr("name"));
        }
    }

    @Nested
    @DisplayName("put方法测试：Map的put方法一致")
    class PutTests {
        /**
         * put方法与Map的put方法一致，即key不做解析，直接put到map中
         */
        @Test
        @DisplayName("put方法测试")
        public void testPut() {
            //输出：{"a":1}
            assertEquals("{\"a\":1}", new JSONMap().put("a", 1).toString());

            //输出：{"a.b":1}
            assertEquals("{\"a.b\":1}", new JSONMap().put("a.b", 1).toString());

            //输出：{"a.c[1]":1}
            assertEquals("{\"a.c[1]\":1}", new JSONMap().put("a.c[1]", 1).toString());
        }
    }

    @Nested
    @DisplayName("层级set方法测试：独创功能 ☆☆☆☆☆")
    class HierarchicalOperationSetTests {
        /**
         * 解析key值中的.号，找到对应的对象或直接构造除以对应位置的数据
         */
        @Test
        @DisplayName("set方法测试")
        public void testSet() {
            //输出：{"a":"1"}
            JSONMap json = new JSONMap().set("a", 1);
            assertEquals("{\"a\":1}", json.toString());
            assertEquals("1", json.getStr("a"));

            //输出：{"a":{"b":1}}
            json = new JSONMap().set("a.b", 1);
            assertEquals("{\"a\":{\"b\":1}}", json.toString());
            assertEquals("{\"b\":1}", json.getStr("a"));
            assertEquals("1", json.getStr("a.b"));


            //输出：{"a":{"b":{"c":1}}}
            json = new JSONMap().set("a.b.c", 1);
            assertEquals("{\"a\":{\"b\":{\"c\":1}}}", json.toString());
            assertEquals("{\"b\":{\"c\":1}}", json.getStr("a"));
            assertEquals("{\"c\":1}", json.getStr("a.b"));
            assertEquals("1", json.getStr("a.b.c"));

            //注意：set方法不支持数组下标
            //输出：{"a":{"b[0]":{"c":"1"}}}
            json = new JSONMap().set("a.b[0].c", 1);
            assertEquals("{\"a\":{\"b\":[{\"c\":1}]}}", json.toString());
            assertEquals("{\"b\":[{\"c\":1}]}", json.getStr("a"));
            assertEquals("[{\"c\":1}]", json.getStr("a.b"));
        }

        @Test
        @DisplayName("set方法null值测试")
        void testSetNullValue() {
            JSONMap jsonMap = new JSONMap();
            jsonMap.set("test.key", null);
            // 应该不会添加任何内容
            assertEquals("{}", jsonMap.getStr("test"));
        }

        @Test
        @DisplayName("set方法替换模式测试")
        void testSetReplaceMode() {
            JSONMap jsonMap = new JSONMap();
            jsonMap.set("data", new JSONMap("old", "旧值"));
            JSONMap newData = new JSONMap("new", "新值");

            jsonMap.set("data", newData); // 替换模式

            assertEquals("新值", jsonMap.getStr("data.new"));
            assertNull(jsonMap.get("data.old"));
        }

        @Test
        @DisplayName("set方法合并模式测试")
        void testSetMergeMode() {
            JSONMap jsonMap = new JSONMap();
            jsonMap.set("data", new JSONMap("existing", "已有值"));
            jsonMap.set("data.new", "新值");

            assertEquals("已有值", jsonMap.getMap("data").getStr("existing"));
            assertEquals("新值", jsonMap.getMap("data").getStr("new"));
        }

        @Test
        @DisplayName("set方法类型不一致测试")
        void testSetInconsistentType() {
            JSONMap jsonMap = new JSONMap();
            jsonMap.set("data", "字符串值");

            Map<String, Object> newData = new HashMap<>();
            newData.put("key", "value");

            assertEquals("字符串值", jsonMap.getStr("data"));
            jsonMap.set("data", newData);
            assertEquals("value", jsonMap.getStr("data.key"));
        }

        @Test
        @DisplayName("set方法 - 数组索引基础测试")
        void testSetArrayBasic() {
            JSONMap json = new JSONMap();
            
            // 设置数组元素
            json.set("arr[0]", "value0");
            json.set("arr[1]", "value1");
            json.set("arr[2]", "value2");
            
            assertEquals("value0", json.getStr("arr[0]"));
            assertEquals("value1", json.getStr("arr[1]"));
            assertEquals("value2", json.getStr("arr[2]"));
        }

        @Test
        @DisplayName("set方法 - 数组索引自动补齐")
        void testSetArrayAutoFill() {
            JSONMap json = new JSONMap();
            
            // 跳过索引，自动补null
            json.set("arr[5]", "value5");
            
            JSONList arr = json.getList("arr");
            assertEquals(6, arr.size());
            assertNull(arr.get(0));
            assertNull(arr.get(4));
            assertEquals("value5", arr.get(5));
        }

        @Test
        @DisplayName("set方法 - 负数索引")
        void testSetArrayNegativeIndex() {
            JSONMap json = new JSONMap();
            
            // 先设置一些元素
            json.set("arr[0]", "value0");
            json.set("arr[1]", "value1");
            json.set("arr[2]", "value2");
            
            // 使用负数索引（倒数第一个）
            json.set("arr[-1]", "lastValue");
            
            assertEquals("lastValue", json.getStr("arr[2]"));
            assertEquals("lastValue", json.getStr("arr[-1]"));
        }

        @Test
        @DisplayName("set方法 - 数组带属性路径")
        void testSetArrayWithProperty() {
            JSONMap json = new JSONMap();
            
            // a[0].b.c 形式
            json.set("users[0].name", "张三");
            json.set("users[0].age", 25);
            json.set("users[1].name", "李四");
            json.set("users[1].age", 30);
            
            assertEquals("张三", json.getStr("users[0].name"));
            assertEquals(25, json.getInt("users[0].age"));
            assertEquals("李四", json.getStr("users[1].name"));
            assertEquals(30, json.getInt("users[1].age"));
        }

        @Test
        @DisplayName("set方法 - 多维数组")
        void testSetMultiDimensionalArray() {
            JSONMap json = new JSONMap();
            
            // a[0][1] 形式
            json.set("matrix[0][0]", 1);
            json.set("matrix[0][1]", 2);
            json.set("matrix[1][0]", 3);
            json.set("matrix[1][1]", 4);
            
            assertEquals("{\"matrix\":[[1,2],[3,4]]}", json.toString());
            assertEquals(1, json.getInt("matrix[0][0]"));
            assertEquals(2, json.getInt("matrix[0][1]"));
            assertEquals(3, json.getInt("matrix[1][0]"));
            assertEquals(4, json.getInt("matrix[1][1]"));
        }

        @Test
        @DisplayName("set方法 - 多维数组带属性")
        void testSetMultiDimensionalArrayWithProperty() {
            JSONMap json = new JSONMap();
            
            // a[0][1].c.d 形式
            json.set("data[0][0].value", "A1");
            json.set("data[0][1].value", "A2");
            json.set("data[1][0].value", "B1");

            assertEquals("{\"data\":[[{\"value\":\"A1\"},{\"value\":\"A2\"}],[{\"value\":\"B1\"}]]}", json.toString());
            assertEquals("A1", json.getStr("data[0][0].value"));
            assertEquals("A2", json.getStr("data[0][1].value"));
            assertEquals("B1", json.getStr("data[1][0].value"));
        }

        @Test
        @DisplayName("set方法 - 以括号开头")
        void testSetStartWithBracket() {
            JSONMap json = new JSONMap();
            
            // [0].a 形式 - 这种情况下根对象本身应该是数组
            // 但JSONMap是Map，所以这个测试可能需要特殊处理
            // 暂时跳过或者抛出异常
        }

        @Test
        @DisplayName("set方法 - 混合路径")
        void testSetMixedPath() {
            JSONMap json = new JSONMap();
            
            // a.b[0].c[1].d 形式
            json.set("config.servers[0].name", "server1");
            json.set("config.servers[0].ports[0]", 8080);
            json.set("config.servers[0].ports[1]", 8081);
            json.set("config.servers[1].name", "server2");

            assertEquals("{\"config\":{\"servers\":[{\"name\":\"server1\",\"ports\":[8080,8081]},{\"name\":\"server2\"}]}}", json.toString());
            assertEquals("server1", json.getStr("config.servers[0].name"));
            assertEquals(8080, json.getInt("config.servers[0].ports[0]"));
            assertEquals(8081, json.getInt("config.servers[0].ports[1]"));
            assertEquals("server2", json.getStr("config.servers[1].name"));
        }

        @Test
        @DisplayName("set方法 - 类型不匹配异常")
        void testSetTypeConflict() {
            JSONMap json = new JSONMap();
            
            // 先设置为普通值
            json.put("data", "string value");
            
            // 尝试作为Map设置子属性，应该抛出异常
            assertThrows(SystemException.class, () -> {
                json.set("data.key", "value");
            });
        }

        @Test
        @DisplayName("set方法 - 数组类型不匹配异常")
        void testSetArrayTypeConflict() {
            JSONMap json = new JSONMap();
            
            // 先设置为普通值
            json.put("data", "string value");
            
            // 尝试作为数组设置，应该抛出异常
            assertThrows(SystemException.class, () -> {
                json.set("data[0]", "value");
            });
        }

        @Test
        @DisplayName("set方法 - 空key异常")
        void testSetEmptyKey() {
            JSONMap json = new JSONMap();
            
            assertThrows(SystemException.class, () -> {
                json.set("", "value");
            });
            
            assertThrows(SystemException.class, () -> {
                json.set(null, "value");
            });
        }

        @Test
        @DisplayName("set方法 - 复杂场景综合测试")
        void testSetComplexScenario() {
            JSONMap json = new JSONMap();
            
            // 构建复杂的嵌套结构
            json.set("app.name", "MyApp");
            json.set("app.version", "1.0.0");
            json.set("app.servers[0].host", "192.168.1.1");
            json.set("app.servers[0].port", 8080);
            json.set("app.servers[0].tags[0]", "production");
            json.set("app.servers[0].tags[1]", "primary");
            json.set("app.servers[1].host", "192.168.1.2");
            json.set("app.servers[1].port", 8081);
            json.set("app.config.timeout", 30);
            json.set("app.config.retry", 3);
            
            // 验证结构
            assertEquals("MyApp", json.getStr("app.name"));
            assertEquals("1.0.0", json.getStr("app.version"));
            assertEquals("192.168.1.1", json.getStr("app.servers[0].host"));
            assertEquals(8080, json.getInt("app.servers[0].port"));
            assertEquals("production", json.getStr("app.servers[0].tags[0]"));
            assertEquals("primary", json.getStr("app.servers[0].tags[1]"));
            assertEquals("192.168.1.2", json.getStr("app.servers[1].host"));
            assertEquals(30, json.getInt("app.config.timeout"));
            assertEquals(3, json.getInt("app.config.retry"));
        }
    }


    @Nested
    @DisplayName("层级get方法测试：独创功能 ☆☆☆☆☆")
    class HierarchicalOperationGetTests {

        // ==================== 基础类型转换测试 ====================

        /**
         * 测试基础类型转换功能
         * <p>
         * 验证同一个值可以被转换为不同的类型：String、Integer、Float等
         */
        @Test
        @DisplayName("基础类型转换 - 同一值转换为不同类型")
        void testBasicTypeConversion() {
            TestBean arg = new TestBean();
            arg.setA(999);
            arg.setB("bean测试");
            JSONMap paras = new JSONMap("a", 1, "b", "2", "c", arg);

            // 验证整数可以转换为String
            String strA = paras.getStr("a");
            assertEquals(String.class, strA.getClass());
            assertEquals("1", strA);

            // 验证整数可以转换为Integer
            Integer intA = paras.getInt("a");
            assertEquals(Integer.class, intA.getClass());
            assertEquals(Integer.valueOf(1), intA);

            // 验证整数可以转换为Float
            Float floatA = paras.getFloat("a");
            assertEquals(Float.class, floatA.getClass());
            assertEquals(Float.valueOf(1.0f), floatA);
        }

        /**
         * 测试BigDecimal类型转换
         * <p>
         * 验证各种数值类型（String、Long、Double）都能正确转换为BigDecimal
         */
        @Test
        @DisplayName("BigDecimal类型转换 - 各种数值源转换")
        void testBigDecimalConversion() {
            JSONMap paras = new JSONMap();

            // 超大数字字符串转BigDecimal
            paras.put("bigNum", "1111111111111111123123123213413333333333333333333333333333333333333333333333333333333333333333333333234124312341431324.21352345324534253");
            BigDecimal bigNum = paras.getBigDecimal("bigNum");
            assertNotNull(bigNum);
            assertTrue(bigNum.toString().startsWith("111111111111111112312312321341333"));

            // 整数字符串转BigDecimal
            paras.put("intStr", "123");
            assertEquals(new BigDecimal("123"), paras.getBigDecimal("intStr"));

            // Long类型转BigDecimal
            paras.put("longVal", 123L);
            assertEquals(new BigDecimal("123"), paras.getBigDecimal("longVal"));

            // 小数字符串转BigDecimal
            paras.put("decimalStr", "123.1");
            assertEquals(new BigDecimal("123.1"), paras.getBigDecimal("decimalStr"));

            // Double类型转BigDecimal
            paras.put("doubleVal", 123.1);
            assertNotNull(paras.getBigDecimal("doubleVal"));
        }

        /**
         * 测试数值类型互转
         * <p>
         * 验证同一个小数值可以转换为Float、Long、Integer、String等类型
         */
        @Test
        @DisplayName("数值类型互转 - 小数转换为各种类型")
        void testNumericTypeInterConversion() {
            JSONMap paras = new JSONMap();
            paras.put("value", 123.1);

            // 小数转Float
            assertEquals(Float.valueOf(123.1f), paras.getFloat("value"));

            // 小数转Long（截断小数部分）
            assertEquals(Long.valueOf(123L), paras.getLong("value"));

            // 小数转Integer（截断小数部分）
            assertEquals(Integer.valueOf(123), paras.getInt("value"));

            // 小数转String
            assertEquals("123.1", paras.getStr("value"));
        }

        /**
         * 测试空JSONMap的类型获取
         * <p>
         * 验证从空Map或不存在的key获取值时返回null
         */
        @Test
        @DisplayName("空值处理 - 不存在的key返回null")
        void testNullValueHandling() {
            JSONMap paras = new JSONMap();

            assertNull(paras.getFloat("nonExist"));
            assertNull(paras.getLong("nonExist"));
            assertNull(paras.getInt("nonExist"));
            assertNull(paras.getStr("nonExist"));
        }

        // ==================== 层级路径访问测试 ====================

        /**
         * 测试点号分隔的多级路径访问
         * <p>
         * 验证使用"a.b"形式可以访问嵌套对象的属性
         */
        @Test
        @DisplayName("多级路径访问 - 点号分隔获取嵌套属性")
        void testDotNotationAccess() {
            JSONMap paras = new JSONMap("{\"a\":{\"b\":1}}");

            // 多级路径获取String
            String strB = paras.getStr("a.b");
            assertEquals(String.class, strB.getClass());
            assertEquals("1", strB);

            // 多级路径获取Integer
            Integer intB = paras.getInt("a.b");
            assertEquals(Integer.class, intB.getClass());
            assertEquals(Integer.valueOf(1), intB);
        }

        /**
         * 测试通过对象构造的多级路径访问
         * <p>
         * 验证嵌套的Java对象也可以通过点号路径访问
         */
        @Test
        @DisplayName("多级路径访问 - 嵌套对象属性获取")
        void testNestedObjectAccess() {
            TestBean arg = new TestBean();
            arg.setA(999);
            arg.setB("bean测试");
            JSONMap paras = new JSONMap("a", 1, "b", "2", "c", arg);

            // 验证JSON结构
            assertEquals("{\"a\":1,\"b\":\"2\",\"c\":{\"a\":999,\"b\":\"bean测试\"}}", paras.toString());

            // 访问嵌套对象的属性并转换类型
            assertEquals("999", paras.getStr("c.a"));
            assertEquals(Integer.valueOf(999), paras.getInt("c.a"));
            assertEquals("bean测试", paras.getStr("c.b"));
        }

        /**
         * 测试三层及以上的多级路径访问
         * <p>
         * 验证深层嵌套结构的访问
         */
        @Test
        @DisplayName("多级路径访问 - 深层嵌套结构")
        void testDeepNestedAccess() {
            JSONMap paras = new JSONMap();
            JSONMap paras2 = new JSONMap();
            JSONMap paras3 = new JSONMap();
            paras.put("a", paras2);
            paras2.put("b", paras3);
            paras3.put("c", 1);

            // 三级路径访问
            assertEquals("1", paras.getStr("a.b.c"));
            assertEquals(Float.valueOf(1.0f), paras2.getFloat("b.c"));
        }

        /**
         * 测试复杂嵌套结构的路径访问
         * <p>
         * 验证包含多个分支的嵌套结构访问
         */
        @Test
        @DisplayName("多级路径访问 - 复杂嵌套结构")
        void testComplexNestedStructureAccess() {
            JSONMap paras = new JSONMap("{\"a\":{\"b2\":{\"c22\":\"221\",\"c21\":\"22\"},\"b\":{\"c1\":\"22\",\"c2\":\"221\"}}}");

            // 获取嵌套的Map对象（返回JSON字符串）
            assertEquals("{\"c1\":\"22\",\"c2\":\"221\"}", paras.getStr("a.b"));

            // 访问不同分支
            assertEquals("22", paras.getStr("a.b.c1"));
            assertEquals("221", paras.getStr("a.b2.c22"));
        }

        /**
         * 测试获取子Map后进行修改
         * <p>
         * 验证获取的子Map修改会影响原对象
         */
        @Test
        @DisplayName("多级路径访问 - 获取子Map并修改")
        void testGetSubMapAndModify() {
            JSONMap paras = new JSONMap("{\"a\":{\"b\":{\"c1\":\"22\",\"c2\":\"221\"}}}");

            // 获取子Map
            JSONMap subMap = paras.getMap("a");
            assertNotNull(subMap);

            // 修改子Map
            subMap.set("b.c1", "999");

            // 验证修改影响到原对象
            assertEquals("999", paras.getStr("a.b.c1"));
        }

        // ==================== 数组下标访问测试 ====================

        /**
         * 测试数组下标访问
         * <p>
         * 验证使用[index]形式访问数组元素，支持正向和负向索引
         */
        @Test
        @DisplayName("数组下标访问 - 正向和负向索引")
        void testArrayIndexAccess() {
            JSONMap paras = new JSONMap("{\"d\":[666,111,222,333,444]}");

            // 正向索引：第一个元素
            assertEquals(Integer.valueOf(666), paras.getInt("d[0]"));

            // 正向索引：第二个元素
            assertEquals(Integer.valueOf(111), paras.getInt("d[1]"));

            // 负向索引：倒数第一个元素
            assertEquals(Integer.valueOf(444), paras.getInt("d[-1]"));

            // 负向索引：倒数第二个元素
            assertEquals(Integer.valueOf(333), paras.getInt("d[-2]"));
        }

        /**
         * 测试复杂的多级数组路径访问
         * <p>
         * 验证"a.b[0].c"形式的混合路径访问
         */
        @Test
        @DisplayName("数组下标访问 - 混合路径访问")
        void testMixedPathWithArrayAccess() {
            Integer[] v = new Integer[]{1, 2};
            Map<String, List<TestBean>> map = new HashMap<>();
            map.put("b", Arrays.stream(v).map(n -> {
                TestBean arg = new TestBean();
                arg.setB("测试b-" + n);
                return arg;
            }).collect(Collectors.toList()));
            map.put("c", Arrays.stream(v).map(n -> {
                TestBean arg = new TestBean();
                arg.setB("测试c-" + n);
                return arg;
            }).collect(Collectors.toList()));

            JSONMap paras = new JSONMap("a", map);

            // 获取数组（返回JSON字符串）
            assertTrue(paras.getStr("a.b").startsWith("["));

            // 获取数组第一个元素
            assertTrue(paras.getStr("a.b[0]").contains("测试b-1"));

            // 获取数组元素的属性
            assertEquals("测试b-1", paras.getStr("a.b[0].b"));
            assertEquals("测试c-2", paras.getStr("a.c[1].b"));
        }

        /**
         * 测试复杂的多级数组路径访问
         * <p>
         * 验证"a.b[0].c"形式的混合路径访问
         */
        @Test
        @DisplayName("数组下标访问 - 混合路径访问")
        void testAdd() {
            JSONMap config = new JSONMap();
            // 链式调用，同时设置不同深度的值
            config.set("server.port", 8080)
                    .set("server.name", "AppServer")
                    .set("db.master.ip", "192.168.1.1")
                    .add("whitelist", "127.0.0.1") // 自动创建数组并添加
                    .add("whitelist", "127.0.0.2"); // 自动创建数组并添加
            // 输出:
            // 获取数组元素的属性
            assertEquals("[\"127.0.0.1\",\"127.0.0.2\"]", config.getStr("whitelist"));
        }

//        /**
//         * 测试多维数组访问
//         * <p>
//         * 验证[0][1]形式的多维数组访问
//         */
//        @Test
//        @DisplayName("数组下标访问 - 多维数组")
//        void testMultiDimensionalArrayAccess() {
//
//
//            JSONMap b = new JSONMap();
//            JSONMap a = new JSONMap();
//            b.add("info", a);
//
//            // 构建二维数组结构
//            a.add("l1", new JSONList().adds(new JSONMap().add("l1_1", 1)).adds(new JSONMap().add("l1_1", 2)));
//            a.add("l1", new JSONList()
//                    .adds(new JSONMap().add("l1_2", 3))
//                    .adds(new JSONMap().add("l1_2", 4))
//                    .adds(new JSONMap().add("l1_2", 5)), 3);
//
//            System.out.println( a.toString());
//            // 访问二维数组
//            assertNotNull(b.getList("info.l1[1]"));
//            assertEquals("4", a.getStr("l1[1][1].l1_2"));
//            assertEquals("5", b.getStr("info.l1[1][-1].l1_2"));
//        }

        // ==================== 集合类型转换测试 ====================

        /**
         * 测试获取List和Array
         * <p>
         * 验证getList和getArray方法的使用
         */
        @Test
        @DisplayName("集合类型转换 - getList和getArray")
        void testGetListAndArray() {
            JSONMap paras = new JSONMap("{\"a\":[1,2,3],\"b\":{\"b\":1,\"a\":2}}");

            // 获取数组为List
            JSONList listA = paras.getList("a");
            assertEquals(3, listA.size());
            assertEquals(Integer.valueOf(1), listA.getInt(0));
            assertEquals(Integer.valueOf(3), listA.getInt(2));

            // 获取数组为Array
            Object[] arrayA = paras.getArray("a");
            assertEquals(3, arrayA.length);

            // 获取指定类型的Array
            Integer[] intArray = paras.getArray("a", Integer.class);
            assertEquals(3, intArray.length);
            assertEquals(Integer.valueOf(1), intArray[0]);

            // Map转List返回空列表
            assertEquals(1,paras.getList("b").size());

            paras.set("c[3]",1);
            assertEquals("{\"a\":[1,2,3],\"b\":{\"a\":2,\"b\":1},\"c\":[null,null,null,1]}",paras.toString());
        }

        /**
         * 测试逗号分隔字符串转List
         * <p>
         * 验证字符串"1,2,3"可以转换为List
         */
        @Test
        @DisplayName("集合类型转换 - 逗号分隔字符串转List")
        void testCommaSeparatedStringToList() {
            JSONMap paras = new JSONMap("{\"a\":\"1,2,3,4,5\"}");

            List<String> list = paras.getList("a", String.class);
            assertEquals(5, list.size());
            assertEquals("1", list.get(0));
            assertEquals("5", list.get(4));
        }

        /**
         * 测试ValUtil工具类的数组转换
         * <p>
         * 验证ValUtil.toArray和toList方法
         */
        @Test
        @DisplayName("集合类型转换 - ValUtil工具类转换")
        void testValUtilConversion() {
            String jsonArray = "[\"a\",1]";

            // 转换为String数组
            String[] strArray = ValUtil.toArray(jsonArray, String.class);
            assertEquals(2, strArray.length);
            assertEquals("a", strArray[0]);
            assertEquals("1", strArray[1]);

            // 转换为String List
            List<String> strList = ValUtil.toList(jsonArray, String.class);
            assertNotNull(strList);
            assertEquals(2, strList.size());
            assertEquals("a", strList.get(0));

            // 逗号分隔字符串转Integer List
            String commaSeparated = "1,2,3,4,5";
            List<Integer> intList = ValUtil.toList(commaSeparated, Integer.class);
            assertNotNull(intList);
            assertEquals(5, intList.size());
            assertEquals(Integer.valueOf(1), intList.get(0));
            assertEquals(Integer.valueOf(5), intList.get(4));
        }

        // ==================== 对象类型转换测试 ====================

        /**
         * 测试JSONList中的嵌套JSON字符串解析
         * <p>
         * 验证数组中的JSON字符串元素可以被解析为Map
         */
        @Test
        @DisplayName("对象类型转换 - JSONList嵌套JSON字符串解析")
        void testJSONListNestedStringParsing() {
            // 数组第一个元素是JSON字符串，后两个是JSON对象
            JSONList list = new JSONList("[\"{\\\"b\\\":1,\\\"a\\\":2}\",{\"b\":1,\"a\":2},{\"b1\":1,\"a1\":2}]");

            // 验证JSON字符串可以被解析为Map并获取属性
            assertEquals("1", list.getMap(0).getStr("b"));
            assertEquals("2", list.getMap(0).getStr("a"));

        }

        /**
         * 测试指定类型的JSONList构造
         * <p>
         * 验证JSONList可以指定元素类型
         */
        @Test
        @DisplayName("对象类型转换 - 指定类型的JSONList")
        void testTypedJSONList() {
            String json = "[{\"xxx\":null,\"c\":[{\"d\":1}]}]";

            JSONList jsonList = new JSONList(json, AA.class);

            // 验证元素是指定的类型
            assertFalse(jsonList.get(0) instanceof JSONMap);
            assertInstanceOf(AA.class, jsonList.get(0));
        }

        // ==================== JSON序列化与反序列化测试 ====================

        /**
         * 测试JSON构造与还原
         * <p>
         * 验证对象序列化为JSON字符串后可以正确还原
         */
        @Test
        @DisplayName("JSON序列化 - 构造与还原")
        void testJsonSerializationAndDeserialization() {
            // 构造嵌套结构
            JSONMap paras = new JSONMap();
            JSONMap paras2 = new JSONMap();
            JSONMap paras3 = new JSONMap();
            paras.put("a", paras2);
            paras2.put("b", paras3);
            paras3.put("c", 1);

            // 序列化
            String json = paras.toString();
            assertEquals("{\"a\":{\"b\":{\"c\":1}}}", json);

            // 反序列化并验证
            JSONMap restored = JacksonUtil.readValue(json);
            assertNotNull(restored);
            assertEquals(Integer.valueOf(1), restored.getMap("a.b").getInt("c"));
            assertEquals("1", restored.getStr("a.b.c"));
        }

        /**
         * 测试set方法后的数据访问
         * <p>
         * 验证使用set方法设置深层数据后可以正确访问
         */
        @Test
        @DisplayName("set后get访问 - 深层路径设置与访问")
        void testSetThenGetAccess() {
            JSONMap paras = new JSONMap("{\"a\":{\"b\":{\"c1\":\"22\",\"c2\":\"221\"}}}");

            // 使用set设置深层数据
            Map<String, Object> newData = new HashMap<>();
            newData.put("d", 1);
            paras.set("a.b.c.d", newData);

            // 验证设置后的结构
            assertEquals(Integer.valueOf(1), paras.getInt("a.b.c.d.d"));

            // 原有数据不受影响
            assertEquals("22", paras.getStr("a.b.c1"));
            assertEquals("221", paras.getStr("a.b.c2"));
        }
    }

    @Nested
    @DisplayName("添加操作方法测试")
    class AddOperationTests {
        @Test
        @DisplayName("add2List方法测试")
        void testAdd2List() {
            JSONMap jsonMap = new JSONMap();
            jsonMap.add("scores", 85);
//            assertEquals(85, jsonMap.getInt("scores[0]"));
//            assertEquals("{\"scores\":[85]}", jsonMap.toString());
            jsonMap.add("scores", 90);
            jsonMap.add("scores", "95,98");
            assertEquals("[85,90,\"95\",\"98\"]", jsonMap.getList("scores").toString());
            assertEquals("[85, 90, 95, 98]", jsonMap.getList("scores",Integer.class).toString());

            JSONList scores = jsonMap.getList("scores");
            assertEquals(4, scores.size());
            assertEquals(85, scores.getInt(0));
            assertEquals(98, scores.getInt(3));
        }
    }

    @Nested
    @DisplayName("IUniversalVals接口方法测试")
    class UniversalValsTests {

        @Test
        @DisplayName("各种类型获取方法测试")
        void testGetTypeMethods() {
            JSONMap jsonMap = new JSONMap();
            jsonMap.put("intValue", 100);
            jsonMap.put("doubleValue", 99.99);
            jsonMap.put("stringValue", "测试字符串");
            jsonMap.put("booleanValue", true);
            jsonMap.put("bigDecimalValue", new BigDecimal("123.45"));
            jsonMap.put("listValue", Arrays.asList("元素1", "元素2"));

            // 整数类型测试
            assertEquals(Integer.valueOf(100), jsonMap.getInt("intValue"));
            assertEquals(Long.valueOf(100), jsonMap.getLong("intValue"));

            // 浮点类型测试
            assertEquals(Double.valueOf(99.99), jsonMap.getDouble("doubleValue"));
            assertEquals(Float.valueOf(99.99f), jsonMap.getFloat("doubleValue"));

            // 字符串类型测试
            assertEquals("测试字符串", jsonMap.getStr("stringValue"));

            // 布尔类型测试
            assertEquals(Boolean.TRUE, jsonMap.getBoolean("booleanValue"));

            // BigDecimal类型测试
            assertEquals(new BigDecimal("123.45"), jsonMap.getBigDecimal("bigDecimalValue"));

            // 列表类型测试
            JSONList list = jsonMap.getList("listValue");
            assertEquals(2, list.size());
            assertEquals("元素1", list.getStr(0));
        }

        @Test
        @DisplayName("默认值测试")
        void testDefaultValue() {
            JSONMap jsonMap = new JSONMap();

            // 测试各种类型的默认值
            assertEquals(Integer.valueOf(-1), jsonMap.getInt("nonExist", -1));
            assertEquals(Long.valueOf(-1L), jsonMap.getLong("nonExist", -1L));
            assertEquals(Double.valueOf(-1.0), jsonMap.getDouble("nonExist", -1.0));
            assertEquals("默认值", jsonMap.getStr("nonExist", "默认值"));
            assertEquals(Boolean.TRUE, jsonMap.getBoolean("nonExist", true));
        }

        @Test
        @DisplayName("对象转换测试")
        void testObjectConversion() {
            JSONMap jsonMap = new JSONMap();
            jsonMap.put("userInfo", new JSONMap("name", "用户", "age", 25));

            JSONMap userInfo = jsonMap.getMap("userInfo");
            assertEquals("用户", userInfo.getStr("name"));
            assertEquals(Integer.valueOf(25), userInfo.getInt("age"));
        }
    }

    @Nested
    @DisplayName("toString方法测试")
    class ToStringTests {

        @Test
        @DisplayName("toString方法测试")
        void testToString() {
            JSONMap jsonMap = new JSONMap();
            jsonMap.put("name", "测试");
            jsonMap.put("age", 30);

            String jsonString = jsonMap.toString();
            assertNotNull(jsonString);
            assertTrue(jsonString.contains("\"name\":\"测试\""));
            assertTrue(jsonString.contains("\"age\":30"));
        }
    }

    @Nested
    @DisplayName("链式调用测试")
    class ChainCallTests {

        @Test
        @DisplayName("链式调用测试")
        void testChainCalls() {
            JSONMap jsonMap = new JSONMap()
                    .put("name", "链式测试")
                    .set("user.profile.email", "test@example.com")
                    .add("tags", "标签1")
                    .add("tags", "标签2");

            JSONMap map = new JSONMap("{\"name\":\"链式测试\",\"user\":{\"profile\":{\"email\":\"test@example.com\"}},\"tags\":[\"标签1\",\"标签2\"]}");

            assertEquals(map.toString(), jsonMap.toString());
            assertEquals("链式测试", jsonMap.getStr("name"));
            assertEquals("test@example.com", jsonMap.getMap("user").getMap("profile").getStr("email"));
            assertEquals("test@example.com", jsonMap.getStr("user.profile.email"));
            assertEquals(2, jsonMap.getList("tags").size());
        }
    }
}