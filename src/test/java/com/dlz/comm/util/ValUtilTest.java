package com.dlz.comm.util;

import com.dlz.comm.consts.Charsets;
import com.dlz.comm.json.JSONList;
import com.dlz.comm.json.JSONMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ValUtil工具类单元测试
 * 
 * 测试ValUtil的各种类型转换和对象操作功能
 * 
 * @author dk
 */
@DisplayName("ValUtil工具类测试")
class ValUtilTest {

    @Nested
    @DisplayName("数值类型转换测试")
    class NumberConversionTests {

        @Test
        @DisplayName("BigDecimal转换测试")
        void testToBigDecimal() {
            // 正常转换
            assertEquals(new BigDecimal("123.45"), ValUtil.toBigDecimal("123.45"));
            assertEquals(new BigDecimal("123"), ValUtil.toBigDecimal(123));
            assertEquals(new BigDecimal("123.45"), ValUtil.toBigDecimal(123.45));
            
            // null和空值处理
            assertNull(ValUtil.toBigDecimal(null));
            assertNull(ValUtil.toBigDecimal(""));
            
            // 默认值测试
            assertEquals(BigDecimal.ZERO, ValUtil.toBigDecimal(null, BigDecimal.ZERO));
            assertThrows(NumberFormatException.class, ()->ValUtil.toBigDecimal("invalid", new BigDecimal("999")));

            // Zero方法测试
            assertEquals(BigDecimal.ZERO, ValUtil.toBigDecimalZero(null));
        }

        @Test
        @DisplayName("Double转换测试")
        void testToDouble() {
            // 正常转换
            assertEquals(Double.valueOf(123.45), ValUtil.toDouble("123.45"));
            assertEquals(Double.valueOf(123.0), ValUtil.toDouble(123));
            assertEquals(Double.valueOf(123.45), ValUtil.toDouble(123.45));
            
            // null和空值处理
            assertNull(ValUtil.toDouble(null));
            assertNull(ValUtil.toDouble(""));
            
            // 默认值测试
            assertEquals(Double.valueOf(0.0), ValUtil.toDouble(null, 0.0));
            assertThrows(NumberFormatException.class,  ()->ValUtil.toDouble("invalid", 999.0));
            
            // Zero方法测试
            assertEquals(Double.valueOf(0.0), ValUtil.toDoubleZero(null));
        }

        @Test
        @DisplayName("Float转换测试")
        void testToFloat() {
            // 正常转换
            assertEquals(Float.valueOf(123.45f), ValUtil.toFloat("123.45"));
            assertEquals(Float.valueOf(123.0f), ValUtil.toFloat(123));
            assertEquals(Float.valueOf(123.45f), ValUtil.toFloat(123.45));
            
            // null和空值处理
            assertNull(ValUtil.toFloat(null));
            assertNull(ValUtil.toFloat(""));
            
            // 默认值测试
            assertEquals(Float.valueOf(0.0f), ValUtil.toFloat(null, 0.0f));
            assertThrows(NumberFormatException.class,()-> ValUtil.toFloat("invalid", 999.0f));
            
            // Zero方法测试
            assertEquals(Float.valueOf(0.0f), ValUtil.toFloatZero(null));
        }

        @Test
        @DisplayName("Integer转换测试")
        void testToInt() {
            // 正常转换
            assertEquals(Integer.valueOf(123), ValUtil.toInt("123"));
            assertEquals(Integer.valueOf(123), ValUtil.toInt(123));
            assertEquals(Integer.valueOf(123), ValUtil.toInt(123.45));
            
            // null和空值处理
            assertNull(ValUtil.toInt(null));
            assertNull(ValUtil.toInt(""));
            
            // 默认值测试
            assertEquals(Integer.valueOf(0), ValUtil.toInt(null, 0));
            assertThrows(NumberFormatException.class, ()-> ValUtil.toInt("invalid", 999));
            
            // Zero方法测试
            assertEquals(Integer.valueOf(0), ValUtil.toIntZero(null));
        }

        @Test
        @DisplayName("Long转换测试")
        void testToLong() {
            // 正常转换
            assertEquals(Long.valueOf(123), ValUtil.toLong("123"));
            assertEquals(Long.valueOf(123), ValUtil.toLong(123));
            assertEquals(Long.valueOf(123), ValUtil.toLong(123.45));
            
            // null和空值处理
            assertNull(ValUtil.toLong(null));
            assertNull(ValUtil.toLong(""));
            
            // 默认值测试
            assertEquals(Long.valueOf(0), ValUtil.toLong(null, 0L));
            assertThrows(NumberFormatException.class,  ()->ValUtil.toLong("invalid", 999L));
            
            // Zero方法测试
            assertEquals(Long.valueOf(0), ValUtil.toLongZero(null));
        }
    }

    @Nested
    @DisplayName("布尔类型转换测试")
    class BooleanConversionTests {

        @Test
        @DisplayName("Boolean转换测试")
        void testToBoolean() {
            //false: null、空值、0，false(忽略大小写)
            assertEquals(Boolean.FALSE, ValUtil.toBoolean(null));
            assertEquals(Boolean.FALSE, ValUtil.toBoolean(false));
            assertEquals(Boolean.FALSE, ValUtil.toBoolean(""));
            assertEquals(Boolean.FALSE, ValUtil.toBoolean("false"));
            assertEquals(Boolean.FALSE, ValUtil.toBoolean("FALSE"));
            assertEquals(Boolean.FALSE, ValUtil.toBoolean("0"));
            assertEquals(Boolean.FALSE, ValUtil.toBoolean(0));
            assertEquals(Boolean.FALSE, ValUtil.toBoolean(0.0));

            // 其他都是true
            assertEquals(Boolean.TRUE, ValUtil.toBoolean("true"));
            assertEquals(Boolean.TRUE, ValUtil.toBoolean("TRUE"));
            assertEquals(Boolean.TRUE, ValUtil.toBoolean("1"));
            assertEquals(Boolean.TRUE, ValUtil.toBoolean(1));
            assertEquals(Boolean.TRUE, ValUtil.toBoolean("invalid"));

            // 默认值测试
            assertEquals(Boolean.TRUE, ValUtil.toBoolean(null, true));
        }
    }

    @Nested
    @DisplayName("字符串类型转换测试")
    class StringConversionTests {

        @Test
        @DisplayName("字符串转换测试")
        void testToStr() {
            // 正常转换
            assertEquals("test", ValUtil.toStr("test"));
            assertEquals("123", ValUtil.toStr(123));
            assertEquals("123.45", ValUtil.toStr(123.45));
            assertEquals("true", ValUtil.toStr(true));
            
            // null处理
            assertNull(ValUtil.toStr(null));
            assertNotNull(ValUtil.toStr(1));

            // 默认值测试
            assertEquals("default", ValUtil.toStr(null, "default"));
            assertEquals("", ValUtil.toStrBlank(null));
            
            // 特殊值处理
            assertEquals("default", ValUtil.toStrWithEmpty(null, "default"));
            assertEquals("default", ValUtil.toStrWithEmpty("null", "default"));
            assertEquals("default", ValUtil.toStrWithEmpty("", "default"));
        }

        @Test
        @DisplayName("字节数组转换测试")
        void testByteArrayToStr() {
            byte[] bytes = "测试中文".getBytes(Charsets.UTF_8);
            assertEquals("测试中文", ValUtil.toStr(bytes));
            assertEquals("测试中文", ValUtil.toStr(bytes, Charsets.UTF_8));
        }
    }

    @Nested
    @DisplayName("集合类型转换测试")
    class CollectionConversionTests {

        @Test
        @DisplayName("List转换测试")
        void testToList() {
            // JSON字符串转换
            String jsonArray = "[\"元素1\", \"元素2\", \"元素3\"]";
            JSONList list = ValUtil.toList(jsonArray);
            assertEquals(3, list.size());
            assertEquals("元素1", list.getStr(0));
            assertEquals("元素2", list.getStr(1));
            assertEquals("元素3", list.getStr(2));
            
            // Collection转换
            List<String> sourceList = Arrays.asList("a", "b", "c");
            JSONList resultList = ValUtil.toList(sourceList);
            assertEquals(3, resultList.size());
            assertEquals("a", resultList.getStr(0));
            
            // null处理
            JSONList nullList = ValUtil.toList(null);
            assertNotNull(nullList);
            assertTrue(nullList.isEmpty());
            
            // 默认值测试
            List<String> defaultList = Arrays.asList("默认1", "默认2");
            JSONList defaultResult = ValUtil.toList(null, defaultList);
            assertEquals(2, defaultResult.size());
            assertEquals("默认1", defaultResult.getStr(0));
            
            // 空列表测试
            JSONList emptyList = ValUtil.toListEmputy(null);
            assertNotNull(emptyList);
            assertTrue(emptyList.isEmpty());
        }

        @Test
        @DisplayName("指定类型List转换测试")
        void testTypedList() {
            String jsonArray = "[\"1\", \"2\", \"3\"]";
            JSONList list = ValUtil.toList(jsonArray);
            assertEquals(3, list.size());
            assertEquals(Integer.valueOf(1), list.getInt(0));
            assertEquals(Integer.valueOf(2), list.getInt(1));
            assertEquals(Integer.valueOf(3), list.getInt(2));

            List<Integer> intList = ValUtil.toList(jsonArray, Integer.class);
            assertEquals(3, intList.size());
            assertEquals(Integer.valueOf(1), intList.get(0));
            assertEquals(Integer.valueOf(2), intList.get(1));
            assertEquals(Integer.valueOf(3), intList.get(2));
        }

        @Test
        @DisplayName("数组转换测试")
        void testToArray() {
            // Collection转数组
            List<String> list = Arrays.asList("元素1", "元素2");
            Object[] array = ValUtil.toArray(list);
            assertEquals(2, array.length);
            assertEquals("元素1", array[0]);
            assertEquals("元素2", array[1]);
            
            // 数组转数组
            String[] stringArray = {"a", "b", "c"};
            Object[] resultArray = ValUtil.toArray(stringArray);
            assertEquals(3, resultArray.length);
            
            // null处理
            Object[] nullArray = ValUtil.toArray(null);
            assertNull(nullArray);
            
            // 默认值测试
            Object[] defaultArray = {"默认"};
            Object[] resultWithDefault = ValUtil.toArray(null, defaultArray);
            assertEquals(1, resultWithDefault.length);
            assertEquals("默认", resultWithDefault[0]);
        }

        @Test
        @DisplayName("指定类型数组转换测试")
        void testTypedArray() {
            // Collection转指定类型数组
            List<String> list = Arrays.asList("1", "2", "3");
            Integer[] intArray = ValUtil.toArray(list, Integer.class);
            assertEquals(3, intArray.length);
            assertEquals(Integer.valueOf(1), intArray[0]);
            assertEquals(Integer.valueOf(2), intArray[1]);
            assertEquals(Integer.valueOf(3), intArray[2]);
            
            // 数组转指定类型数组
            String[] stringArray = {"1.1", "2.2", "3.3"};
            Double[] doubleArray = ValUtil.toArray(stringArray, Double.class);
            assertEquals(3, doubleArray.length);
            assertEquals(Double.valueOf(1.1), doubleArray[0]);
            assertEquals(Double.valueOf(2.2), doubleArray[1]);
            assertEquals(Double.valueOf(3.3), doubleArray[2]);
            
            // 逗号分隔字符串转数组
            String commaString = "1,2,3";
            Integer[] resultArray = ValUtil.toArray(commaString, Integer.class);
            assertEquals(3, resultArray.length);
            assertEquals(Integer.valueOf(1), resultArray[0]);
            assertEquals(Integer.valueOf(2), resultArray[1]);
            assertEquals(Integer.valueOf(3), resultArray[2]);
        }
    }

    @Nested
    @DisplayName("日期时间转换测试")
    class DateTimeConversionTests {

        @Test
        @DisplayName("LocalDateTime转换测试")
        void testToLocalDateTime() {
            // 字符串转换
            LocalDateTime dateTime = ValUtil.toLocalDateTime("2023-01-01 12:30:45");
            assertNotNull(dateTime);
            assertEquals(2023, dateTime.getYear());
            assertEquals(1, dateTime.getMonthValue());
            assertEquals(1, dateTime.getDayOfMonth());
            
            // Date转换
            Date date = new Date();
            LocalDateTime fromDate = ValUtil.toLocalDateTime(date);
            assertNotNull(fromDate);
            
            // Number转换
            long timestamp = System.currentTimeMillis();
            LocalDateTime fromTimestamp = ValUtil.toLocalDateTime(timestamp);
            assertNotNull(fromTimestamp);
            
            // null处理
            assertNull(ValUtil.toLocalDateTime(null));
        }

        @Test
        @DisplayName("Date转换测试")
        void testToDate() {
            // 字符串转换
            Date date = ValUtil.toDate("2023-01-01 12:30:45");
            assertNotNull(date);
            
            // LocalDateTime转换
            LocalDateTime localDateTime = LocalDateTime.now();
            Date fromLocalDateTime = ValUtil.toDate(localDateTime);
            assertNotNull(fromLocalDateTime);
            
            // Number转换
            long timestamp = System.currentTimeMillis();
            Date fromTimestamp = ValUtil.toDate(timestamp);
            assertNotNull(fromTimestamp);
            
            // null处理
            assertNull(ValUtil.toDate(null));
            
            // 默认值测试
            Date now = new Date();
            Date result = ValUtil.toDateNow(null);
            assertNotNull(result);
        }

        @Test
        @DisplayName("日期字符串转换测试")
        void testToDateStr() {
            // Date转字符串
            Date date = new Date();
            String dateStr = ValUtil.toDateStr(date);
            assertNotNull(dateStr);
            assertFalse(dateStr.isEmpty());
            
            // LocalDateTime转字符串
            LocalDateTime localDateTime = LocalDateTime.now();
            String localDateTimeStr = ValUtil.toDateStr(localDateTime);
            assertNotNull(localDateTimeStr);
            assertFalse(localDateTimeStr.isEmpty());
            
            // 自定义格式
            String formatted = ValUtil.toDateStr(date, "yyyy-MM-dd");
            assertNotNull(formatted);
            assertTrue(formatted.matches("\\d{4}-\\d{2}-\\d{2}"));
            
            // null处理
            assertEquals("", ValUtil.toDateStr(null));
        }
    }

    @Nested
    @DisplayName("对象转换测试")
    class ObjectConversionTests {

        @Test
        @DisplayName("通用对象转换测试")
        void testToObj() {
            // 基本类型转换
            Integer intResult = ValUtil.toObj("123", Integer.class);
            assertEquals(Integer.valueOf(123), intResult);
            
            Double doubleResult = ValUtil.toObj("123.45", Double.class);
            assertEquals(Double.valueOf(123.45), doubleResult);
            
            // JSON对象转换
            String json = "{\"name\":\"测试\",\"age\":25}";
            JSONMap mapResult = ValUtil.toObj(json, JSONMap.class);
            assertEquals("测试", mapResult.getStr("name"));
            assertEquals(Integer.valueOf(25), mapResult.getInt("age"));
            
            // null处理
            assertNull(ValUtil.toObj(null, String.class));
            assertNotNull(ValUtil.toObj("test", (Class<?>) null));
        }

        @Test
        @DisplayName("原生对象转换测试")
        void testToNativeObj() {
            // 字符串转换
            String strResult = ValUtil.toNativeObj(123, String.class);
            assertEquals("123", strResult);
            
            // 数字转换
            Integer intResult = ValUtil.toNativeObj("123", Integer.class);
            assertEquals(Integer.valueOf(123), intResult);
            
            // 日期转换
            Date date = new Date();
            Date dateResult = ValUtil.toNativeObj(date.getTime(), Date.class);
            assertNotNull(dateResult);
            
            // 不支持的类型返回null
            Object unsupported = ValUtil.toNativeObj("test", Object.class);
            assertNotNull(unsupported);
        }
    }

    @Nested
    @DisplayName("空值判断测试")
    class EmptyCheckTests {

        @Test
        @DisplayName("isEmpty方法测试")
        void testIsEmpty() {
            // null检查
            assertTrue(ValUtil.isEmpty(null));
            
            // Collection检查
            assertTrue(ValUtil.isEmpty(new ArrayList<>()));
            assertFalse(ValUtil.isEmpty(Arrays.asList("元素")));
            
            // Map检查
            assertTrue(ValUtil.isEmpty(new HashMap<>()));
            Map<String, String> map = new HashMap<>();
            map.put("key", "value");
            assertFalse(ValUtil.isEmpty(map));
            
            // 数组检查
            assertTrue(ValUtil.isEmpty(new String[0]));
            assertFalse(ValUtil.isEmpty(new String[]{"元素"}));
            
            // 字符串检查
            assertTrue(ValUtil.isEmpty(""));
            assertFalse(ValUtil.isEmpty("非空字符串"));
            
            // 其他对象
            assertFalse(ValUtil.isEmpty(new Object()));
        }
    }

    @Nested
    @DisplayName("边界情况测试")
    class EdgeCaseTests {
        @Test
        @DisplayName("无效输入处理测试")
        void testInvalidInputHandling() {
            // 无效数字字符串
            assertThrows(NumberFormatException.class, () ->ValUtil.toInt("invalid"));
            assertThrows(NumberFormatException.class, () ->ValUtil.toBigDecimal("invalid"));

            // 无效日期字符串
            assertNull(ValUtil.toDate("invalid-date"));
            assertNull(ValUtil.toDate("invalid-date"));
            assertNull(ValUtil.toLocalDateTime("invalid-datetime"));
            
            // 无效JSON
            JSONList list = ValUtil.toList("{invalid json}", new ArrayList<>());
            assertEquals(1,list.size());
            assertEquals(1,ValUtil.toList("{invalid json}").size());
        }

        @Test
        @DisplayName("类型不匹配测试")
        void testTypeMismatch() {
            // 尝试将复杂对象转换为简单类型
            Object complexObject = new HashMap<String, Object>() {{
                put("key", "value");
            }};
            
            String result = ValUtil.toStr(complexObject);
            assertNotNull(result);
            assertTrue(result.contains("\"key\""));
            assertTrue(result.contains("\"value\""));
        }
    }
}