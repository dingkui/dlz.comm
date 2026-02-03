package com.dlz.comm.json;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JSONList单元测试类
 * 
 * 测试JSONList的各种功能，包括构造函数、类型转换、索引访问等
 * 
 * @author dk
 */
@DisplayName("JSONList单元测试")
class JSONListTest {

    @Nested
    @DisplayName("构造函数测试")
    class ConstructorTests {
        @Test
        @DisplayName("构造参数为复杂多级结构测试")
        public void test0(){
            List<LinkedHashMap<String, Object>> index = Arrays
                    .stream(new String[]{"1", "2", "3"})
                    .map(item -> {
                LinkedHashMap<String, Object> p = new LinkedHashMap<>();
                p.put("index", item);
                return p;
            }).collect(Collectors.toList());

            JSONList jsonList = new JSONList(index);
            assertEquals("[{\"index\":\"1\"},{\"index\":\"2\"},{\"index\":\"3\"}]", jsonList.toString());
            assertEquals(1, jsonList.getMap(0).getInt("index"));
            assertEquals(2, jsonList.getMap(1).getInt("index"));
            assertEquals(3, jsonList.getMap(2).getInt("index"));
            assertEquals(3, jsonList.getMap(-1).getInt("index"));
            assertEquals(3, jsonList.getInt("[-1]index"));
        }
        @Test
        @DisplayName("无参构造函数测试")
        void testDefaultConstructor() {
            JSONList jsonList = new JSONList();
            assertNotNull(jsonList);
            assertTrue(jsonList.isEmpty());
            assertEquals("[]", jsonList.toString());
            assertEquals(0, jsonList.size());
        }

        @Test
        @DisplayName("指定容量构造函数测试")
        void testInitialCapacityConstructor() {
            JSONList jsonList = new JSONList(10);
            assertNotNull(jsonList);
            assertTrue(jsonList.isEmpty());
            assertEquals("[]", jsonList.toString());
            assertEquals(0, jsonList.size());
        }

        @Test
        @DisplayName("null对象构造函数测试")
        void testNullObjectConstructor() {
            JSONList jsonList = new JSONList((Object) null);
            assertNotNull(jsonList);
            assertEquals("[]", jsonList.toString());
            assertTrue(jsonList.isEmpty());

            jsonList = new JSONList((Collection<?>) null);
            assertNotNull(jsonList);
            assertEquals("[]", jsonList.toString());
            assertTrue(jsonList.isEmpty());

            jsonList = new JSONList((Object[]) null);
            assertNotNull(jsonList);
            assertEquals("[]", jsonList.toString());
            assertTrue(jsonList.isEmpty());
        }

        @Test
        @DisplayName("数组或集合类型构造函数测试")
        void testCollectionConstructor() {
            String[] array = {"数组元素1", "数组元素2", "数组元素3"};
            JSONList jsonList = new JSONList(array);

            assertEquals("[\"数组元素1\",\"数组元素2\",\"数组元素3\"]", jsonList.toString());
            assertEquals(3, jsonList.size());
            assertEquals("数组元素1", jsonList.getStr(0));
            assertEquals("数组元素2", jsonList.getStr(1));
            assertEquals("数组元素3", jsonList.getStr(2));

            //带类型转换的数组构造函数测试
            String[] array2 = {"1.1", "2.2", "3.3"};
            jsonList = new JSONList(array2, Double.class);

            assertEquals(3, jsonList.size());
            assertEquals(Double.valueOf(1.1), jsonList.getDouble(0));
            assertEquals(Double.valueOf(2.2), jsonList.getDouble(1));
            assertEquals(Double.valueOf(3.3), jsonList.getDouble(2));


            //带类型转换的数组构造函数测试
            List<String> list = Arrays.asList("元素1", "元素2", "元素3");
            jsonList = new JSONList(list);
            assertEquals("[\"元素1\",\"元素2\",\"元素3\"]", jsonList.toString());
            assertEquals(3, jsonList.size());
            assertEquals("元素1", jsonList.getStr(0));
            assertEquals("元素2", jsonList.getStr(1));
            assertEquals("元素3", jsonList.getStr(2));

            //带类型转换的数组构造函数测试
            list = Arrays.asList("1", "2", "3");
            jsonList = new JSONList(list, Integer.class);

            assertEquals(3, jsonList.size());
            assertEquals(Integer.valueOf(1), jsonList.getInt(0));
            assertEquals(Integer.valueOf(2), jsonList.getInt(1));
            assertEquals(Integer.valueOf(3), jsonList.getInt(2));
        }


        @Test
        @DisplayName("JSON字符串构造函数测试")
        void testJsonStringConstructor() {
            String jsonString = "[\"字符串1\", 123, true, 45.67]";
            JSONList jsonList = new JSONList(jsonString);
            
            assertEquals(4, jsonList.size());
            assertEquals("字符串1", jsonList.getStr(0));
            assertEquals(Integer.valueOf(123), jsonList.getInt(1));
            assertEquals(Boolean.TRUE, jsonList.getBoolean(2));
            assertEquals(Double.valueOf(45.67), jsonList.getDouble(3));
        }

        @Test
        @DisplayName("逗号分隔字符串构造函数测试")
        void testCommaSeparatedStringConstructor() {
            String commaString = "元素1,元素2,元素3";
            JSONList jsonList = new JSONList(commaString);
            
            assertEquals(3, jsonList.size());
            assertEquals("元素1", jsonList.getStr(0));
            assertEquals("元素2", jsonList.getStr(1));
            assertEquals("元素3", jsonList.getStr(2));
        }

        @Test
        @DisplayName("带类型转换的逗号分隔字符串构造函数测试")
        void testTypedCommaSeparatedStringConstructor() {
            String commaString = "1,2,3";
            JSONList jsonList = new JSONList(commaString, Integer.class);
            
            assertEquals(3, jsonList.size());
            assertEquals(Integer.valueOf(1), jsonList.getInt(0));
            assertEquals(Integer.valueOf(2), jsonList.getInt(1));
            assertEquals(Integer.valueOf(3), jsonList.getInt(2));
        }

        @Test
        @DisplayName("无效JSON字符串构造函数测试")
        void testInvalidJsonConstructor() {
            String invalidJson = "{invalid json}";
            assertThrows(RuntimeException.class, () -> new JSONList(invalidJson));
        }
    }

    @Nested
    @DisplayName("类型转换方法测试")
    class TypeConversionTests {

        @Test
        @DisplayName("asList指定类型方法测试")
        void testAsListWithType() {
            JSONList jsonList = new JSONList();
            jsonList.add("1");
            jsonList.add("2");
            jsonList.add("3");

            List<Integer> intList = jsonList.asList(Integer.class);
            assertEquals(3, intList.size());
            assertEquals(Integer.valueOf(1), intList.get(0));
            assertEquals(Integer.valueOf(2), intList.get(1));
            assertEquals(Integer.valueOf(3), intList.get(2));
        }

        @Test
        @DisplayName("asList转换为JSONMap方法测试")
        void testAsListToJSONMap() {
            JSONList jsonList = new JSONList();
            jsonList.add(new JSONMap("name", "用户1", "age", 25));
            jsonList.add(new JSONMap("name", "用户2", "age", 30));

            List<JSONMap> mapList = jsonList.asList();
            assertEquals(2, mapList.size());
            assertEquals("用户1", mapList.get(0).getStr("name"));
            assertEquals(Integer.valueOf(25), mapList.get(0).getInt("age"));
            assertEquals("用户2", mapList.get(1).getStr("name"));
            assertEquals(Integer.valueOf(30), mapList.get(1).getInt("age"));
        }
    }

    @Nested
    @DisplayName("索引访问方法测试")
    class IndexAccessTests {

        @Test
        @DisplayName("getMap方法测试")
        void testGetMap() {
            JSONList jsonList = new JSONList();
            jsonList.add(new JSONMap("name", "用户1"));
            jsonList.add("{\"name\":\"用户2\"}");
            jsonList.add("简单字符串"); // 这个会抛异常

            // 测试正常的JSONMap对象
            JSONMap map1 = jsonList.getMap(0);
            assertEquals("用户1", map1.getStr("name"));

            // 测试JSON字符串转换
            JSONMap map2 = jsonList.getMap(1);
            assertEquals("用户2", map2.getStr("name"));

            // 测试简单类型抛异常
            assertThrows(RuntimeException.class, () -> jsonList.getMap(2));
        }

        @Test
        @DisplayName("getObj方法测试")
        void testGetObj() {
            JSONList jsonList = new JSONList();
            jsonList.add(new JSONMap("name", "用户1"));
            jsonList.add("{\"name\":\"用户2\"}");

            JSONMap map1 = jsonList.getObj(0);
            assertEquals("用户1", map1.getStr("name"));

            JSONMap map2 = jsonList.getObj(1);
            assertEquals("用户2", map2.getStr("name"));
        }
    }

    @Nested
    @DisplayName("IUniversalVals4List接口方法测试")
    class UniversalVals4ListTests {

        @Test
        @DisplayName("各种类型获取方法测试")
        void testGetTypeMethods() {
            JSONList jsonList = new JSONList();
            jsonList.add(100);
            jsonList.add(99.99);
            jsonList.add("测试字符串");
            jsonList.add(true);
            jsonList.add(new BigDecimal("123.45"));

            // 整数类型测试
            assertEquals(Integer.valueOf(100), jsonList.getInt(0));
            assertEquals(Long.valueOf(100), jsonList.getLong(0));
            
            // 浮点类型测试
            assertEquals(Double.valueOf(99.99), jsonList.getDouble(1));
            assertEquals(Float.valueOf(99.99f), jsonList.getFloat(1));
            
            // 字符串类型测试
            assertEquals("测试字符串", jsonList.getStr(2));
            
            // 布尔类型测试
            assertEquals(Boolean.TRUE, jsonList.getBoolean(3));
            
            // BigDecimal类型测试
            assertEquals(new BigDecimal("123.45"), jsonList.getBigDecimal(4));
        }

        @Test
        @DisplayName("默认值测试")
        void testDefaultValue() {
            JSONList jsonList = new JSONList();

            // 测试各种类型的默认值（超出范围的索引）
            assertEquals(Integer.valueOf(-1), jsonList.getInt(999, -1));
            assertEquals(Long.valueOf(-1L), jsonList.getLong(999, -1L));
            assertEquals(Double.valueOf(-1.0), jsonList.getDouble(999, -1.0));
            assertEquals("默认值", jsonList.getStr(999, "默认值"));
            assertEquals(Boolean.TRUE, jsonList.getBoolean(999, true));
        }

        @Test
        @DisplayName("数组类型获取测试")
        void testArrayAccess() {
            JSONList jsonList = new JSONList();
            jsonList.add(Arrays.asList("元素1", "元素2", "元素3"));

            Object[] array = jsonList.getArray(0);
            assertEquals(3, array.length);
            assertEquals("元素1", array[0]);
            assertEquals("元素2", array[1]);
            assertEquals("元素3", array[2]);
        }

        @Test
        @DisplayName("列表类型获取测试")
        void testListAccess() {
            JSONList jsonList = new JSONList();
            jsonList.add(Arrays.asList("元素1", "元素2", "元素3"));

            List list = jsonList.getList(0);
            assertEquals(3, list.size());
            assertEquals("元素1", list.get(0));
            assertEquals("元素2", list.get(1));
            assertEquals("元素3", list.get(2));
        }

        @Test
        @DisplayName("日期类型获取测试")
        void testDateAccess() {
            JSONList jsonList = new JSONList();
            jsonList.add("2023-01-01");
            jsonList.add("2023-01-01 12:30:45");

            Date date1 = jsonList.getDate(0);
            assertNotNull(date1);
            
            Date date2 = jsonList.getDate(1);
            assertNotNull(date2);
        }

        @Test
        @DisplayName("日期字符串获取测试")
        void testDateStringAccess() {
            JSONList jsonList = new JSONList();
            Date now = new Date();
            jsonList.add(now);

            String dateStr = jsonList.getDateStr(0);
            assertNotNull(dateStr);
        }
    }

    @Nested
    @DisplayName("添加操作方法测试")
    class AddOperationTests {

        @Test
        @DisplayName("adds方法链式调用测试")
        void testAddsMethod() {
            JSONList jsonList = new JSONList()
                .adds("元素1")
                .adds("元素2")
                .adds(123);

            assertEquals(3, jsonList.size());
            assertEquals("元素1", jsonList.getStr(0));
            assertEquals("元素2", jsonList.getStr(1));
            assertEquals(Integer.valueOf(123), jsonList.getInt(2));
        }
    }

    @Nested
    @DisplayName("toString方法测试")
    class ToStringTests {

        @Test
        @DisplayName("toString方法测试")
        void testToString() {
            JSONList jsonList = new JSONList();
            jsonList.add("测试");
            jsonList.add(123);
            jsonList.add(true);

            String jsonString = jsonList.toString();
            assertNotNull(jsonString);
            assertTrue(jsonString.startsWith("["));
            assertTrue(jsonString.endsWith("]"));
            assertTrue(jsonString.contains("\"测试\""));
            assertTrue(jsonString.contains("123"));
            assertTrue(jsonString.contains("true"));
        }
    }

    @Nested
    @DisplayName("继承ArrayList功能测试")
    class ArrayListFunctionalityTests {

        @Test
        @DisplayName("基本ArrayList操作测试")
        void testBasicArrayListOperations() {
            JSONList jsonList = new JSONList();
            
            // 添加元素
            jsonList.add("元素1");
            jsonList.add("元素2");
            assertEquals(2, jsonList.size());
            
            // 访问元素
            assertEquals("元素1", jsonList.get(0));
            assertEquals("元素2", jsonList.get(1));
            
            // 移除元素
            jsonList.remove(0);
            assertEquals(1, jsonList.size());
            assertEquals("元素2", jsonList.get(0));
            
            // 清空列表
            jsonList.clear();
            assertTrue(jsonList.isEmpty());
        }

        @Test
        @DisplayName("迭代器测试")
        void testIterator() {
            JSONList jsonList = new JSONList(Arrays.asList("元素1", "元素2", "元素3"));
            
            List<String> result = new ArrayList<>();
            for (Object item : jsonList) {
                result.add(item.toString());
            }
            
            assertEquals(3, result.size());
            assertEquals("元素1", result.get(0));
            assertEquals("元素2", result.get(1));
            assertEquals("元素3", result.get(2));
        }
    }

    @Nested
    @DisplayName("边界情况测试")
    class EdgeCaseTests {

        @Test
        @DisplayName("空列表测试")
        void testEmptyList() {
            JSONList jsonList = new JSONList();
            
            // 测试各种获取方法在空列表上的表现
            assertThrows(IndexOutOfBoundsException.class, () -> jsonList.getStr(0));
            assertEquals("默认值", jsonList.getStr(0, "默认值"));
            assertThrows(IndexOutOfBoundsException.class, () -> jsonList.getMap(0));
        }

        @Test
        @DisplayName("null值处理测试")
        void testNullValueHandling() {
            JSONList jsonList = new JSONList();
            jsonList.add(null);
            
            assertNull(jsonList.get(0));
            assertEquals("默认", jsonList.getStr(0, "默认"));
            assertNull(jsonList.getMap(0));
        }

        @Test
        @DisplayName("类型不匹配测试")
        void testTypeMismatch() {
            JSONList jsonList = new JSONList();
            jsonList.add("不是数字的字符串");
            
            // 字符串转数字应该返回null或默认值
            assertThrows(NumberFormatException.class, () -> jsonList.getInt(0));
            assertThrows(NumberFormatException.class, () -> jsonList.getInt(0, -1));
        }
    }
}