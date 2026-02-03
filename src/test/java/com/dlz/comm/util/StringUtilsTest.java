package com.dlz.comm.util;

import com.dlz.comm.json.JSONMap;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.junit.Assert.*;

/**
 * StringUtils 工具类单元测试
 * 
 * 测试字符串处理的各种功能
 * 
 * @author test
 */
public class StringUtilsTest {
    
    private static final Logger log = LoggerFactory.getLogger(StringUtilsTest.class);
    
    @Test
    public void testNVL() {
        // 测试基本NVL功能
        assertEquals("空字符串测试", "", StringUtils.NVL(null));
        assertEquals("正常字符串测试", "test", StringUtils.NVL("test"));
        assertEquals("空字符串保持", "", StringUtils.NVL(""));
        
        // 测试带默认值的NVL
        assertEquals("null默认值测试", "default", StringUtils.NVL(null, "default"));
        assertEquals("正常值测试", "test", StringUtils.NVL("test", "default"));
        assertEquals("空字符串保持", "", StringUtils.NVL("", "default"));
    }
    
    @Test
    public void testGetBeanId() {
        // 测试类名转Bean ID
        assertEquals("简单类名测试", "stringUtils", StringUtils.getBeanId("com.dlz.comm.util.StringUtils"));
        assertEquals("单级包名测试", "test", StringUtils.getBeanId("Test"));
        assertEquals("多级包名测试", "className", StringUtils.getBeanId("com.example.package.ClassName"));
        
        // 测试Class对象转Bean ID
        assertEquals("Class对象测试", "stringUtils", StringUtils.getBeanId(StringUtils.class));
        assertEquals("String类测试", "string", StringUtils.getBeanId(String.class));
    }
    
    @Test
    public void testFormatMsg_Object_ObjectArray() {
        // 基本格式化测试
        assertEquals("基本占位符测试", "this is a for b", 
                    StringUtils.formatMsg("this is {} for {}", "a", "b"));
        
        // 指定下标测试
        assertEquals("指定下标测试", "this is b for a", 
                    StringUtils.formatMsg("this is {1} for {0}", "a", "b"));
        
        // 混合下标测试
        assertEquals("混合下标测试", "this is b for b and a", 
                    StringUtils.formatMsg("this is {1} for {} and {0}", "a", "b"));
        
        // 说明+下标测试
        assertEquals("说明加下标测试", "this is b for a", 
                    StringUtils.formatMsg("this is {param1} for {0}", "a", "b"));
        
        // 下标越界测试
        assertEquals("下标越界测试", "this is {9} for b", 
                    StringUtils.formatMsg("this is {9} for {}", "a", "b"));
        
        // 参数不足测试
        assertEquals("参数不足测试", "this is a for b and {}", 
                    StringUtils.formatMsg("this is {} for {} and {}", "a", "b"));
        
        // 空参数测试
        assertEquals("空参数测试", "this is  for ", 
                    StringUtils.formatMsg("this is {} for {}", "", ""));
        
        // null参数测试
        assertEquals("null参数测试", "this is null for null", 
                    StringUtils.formatMsg("this is {} for {}", null, null));
    }
    
    @Test
    public void testFormatMsg_Object_JSONMap() {
        // 准备测试数据
        JSONMap map = new JSONMap();
        map.put("name", "张三");
        map.put("age", "25");
        map.put("city.name", "北京");
        
        // 基本替换测试
        assertEquals("基本替换测试", "姓名：张三，年龄：25", 
                    StringUtils.formatMsg("姓名：${name}，年龄：${age}", map));
        
        // 嵌套属性测试
        assertEquals("嵌套属性测试", "来自北京", 
                    StringUtils.formatMsg("来自${city.name}", map));
        
        // 不存在的属性测试
        assertEquals("不存在属性测试", "姓名：张三，地址：{address}",
                    StringUtils.formatMsg("姓名：${name}，地址：${address}", map));
        
        // 空字符串测试
        assertEquals("空字符串测试", "", 
                    StringUtils.formatMsg("", map));
        
        // null输入测试
        assertEquals("null输入测试", "", 
                    StringUtils.formatMsg(null, map));
    }
    
    @Test
    public void testAddZeroBefor() {
        // 基本测试
        assertEquals("基本补零测试", "00123", StringUtils.addZeroBefor(123, 5));
        assertEquals("刚好长度测试", "12345", StringUtils.addZeroBefor(12345, 5));
        assertEquals("超长测试", "123456", StringUtils.addZeroBefor(123456, 5));
        assertEquals("负数测试", "-0123", StringUtils.addZeroBefor(-123, 5));
        assertEquals("零测试", "00000", StringUtils.addZeroBefor(0, 5));
    }
    
    @Test
    public void testIsEmpty() {
        // null测试
        assertTrue("null应该为空", StringUtils.isEmpty(null));
        
        // CharSequence测试
        assertTrue("空字符串应该为空", StringUtils.isEmpty(""));
        assertFalse("非空字符串不应该为空", StringUtils.isEmpty("test"));
        assertFalse("空格字符串不应该为空", StringUtils.isEmpty(" "));
        
        // Collection测试
        assertTrue("空List应该为空", StringUtils.isEmpty(new ArrayList<>()));
        assertFalse("非空List不应该为空", StringUtils.isEmpty(Arrays.asList("test")));
        
        // Map测试
        assertTrue("空Map应该为空", StringUtils.isEmpty(new HashMap<>()));
        Map<String, String> map = new HashMap<>();
        map.put("key", "value");
        assertFalse("非空Map不应该为空", StringUtils.isEmpty(map));
        
        // Array测试
        assertTrue("空数组应该为空", StringUtils.isEmpty(new String[0]));
        assertFalse("非空数组不应该为空", StringUtils.isEmpty(new String[]{"test"}));
        
        // Optional测试
        assertTrue("空Optional应该为空", StringUtils.isEmpty(Optional.empty()));
        assertFalse("非空Optional不应该为空", StringUtils.isEmpty(Optional.of("test")));
        
        // 普通对象测试
        assertFalse("普通对象不应该为空", StringUtils.isEmpty(new Object()));
    }
    
    @Test
    public void testIsAnyEmpty() {
        // 全部非空测试
        assertFalse("全部非空应该返回false", StringUtils.isAnyEmpty("a", "b", "c"));
        
        // 包含null测试
        assertTrue("包含null应该返回true", StringUtils.isAnyEmpty("a", null, "c"));
        
        // 包含空字符串测试
        assertTrue("包含空字符串应该返回true", StringUtils.isAnyEmpty("a", "", "c"));
        
        // 全部空测试
        assertTrue("全部空应该返回true", StringUtils.isAnyEmpty(null, "", new ArrayList<>()));
        
        // 单个参数测试
        assertTrue("单个null参数应该返回true", StringUtils.isAnyEmpty((Object) null));
        assertFalse("单个非空参数应该返回false", StringUtils.isAnyEmpty("test"));
    }
    
    @Test
    public void testIsBlank() {
        // 基本测试
        assertTrue("null应该为空白", StringUtils.isBlank(null));
        assertTrue("空字符串应该为空白", StringUtils.isBlank(""));
        assertTrue("只有空格应该为空白", StringUtils.isBlank(" "));
        assertTrue("多个空格应该为空白", StringUtils.isBlank("   "));
        assertTrue("制表符应该为空白", StringUtils.isBlank("\t"));
        assertTrue("换行符应该为空白", StringUtils.isBlank("\n"));
        assertTrue("混合空白字符应该为空白", StringUtils.isBlank(" \t\n "));
        
        // 非空白测试
        assertFalse("包含字符不应该为空白", StringUtils.isBlank("a"));
        assertFalse("前后有空格但中间有字符不应该为空白", StringUtils.isBlank(" a "));
        assertFalse("数字字符不应该为空白", StringUtils.isBlank("123"));
    }
    
    @Test
    public void testIsAnyBlank() {
        // 全部非空白测试
        assertFalse("全部非空白应该返回false", StringUtils.isAnyBlank("a", "b", "c"));
        
        // 包含空白测试
        assertTrue("包含空白应该返回true", StringUtils.isAnyBlank("a", " ", "c"));
        assertTrue("包含null应该返回true", StringUtils.isAnyBlank("a", null, "c"));
        assertTrue("包含空字符串应该返回true", StringUtils.isAnyBlank("a", "", "c"));
        
        // 全部空白测试
        assertTrue("全部空白应该返回true", StringUtils.isAnyBlank(null, "", "   "));
        
        // 单个参数测试
        assertTrue("单个空白参数应该返回true", StringUtils.isAnyBlank(" "));
        assertFalse("单个非空白参数应该返回false", StringUtils.isAnyBlank("test"));
    }
    
    @Test
    public void testIsAllBlank() {
        // 全部空白测试
        assertTrue("全部null应该返回true", StringUtils.isAllBlank(null, null));
        assertTrue("全部空字符串应该返回true", StringUtils.isAllBlank("", ""));
        assertTrue("全部空白字符应该返回true", StringUtils.isAllBlank(" ", "   ", "\t"));
        assertTrue("混合空白应该返回true", StringUtils.isAllBlank(null, "", " "));
        
        // 包含非空白测试
        assertFalse("包含非空白应该返回false", StringUtils.isAllBlank("a", " ", "c"));
        assertFalse("部分空白应该返回false", StringUtils.isAllBlank("", "test"));
        
        // 单个参数测试
        assertTrue("单个空白参数应该返回true", StringUtils.isAllBlank(" "));
        assertFalse("单个非空白参数应该返回false", StringUtils.isAllBlank("test"));
    }
    
    @Test
    public void testStartsWithAny() {
        // 基本测试
        assertTrue("应该以任一字符串开头", 
                  StringUtils.startsWithAny("abcdef", "abc", "xyz"));
        assertFalse("不应该以任何字符串开头", 
                   StringUtils.startsWithAny("abcdef", "xyz", "123"));
        
        // null测试
        assertFalse("null序列应该返回false", 
                   StringUtils.startsWithAny(null, "abc"));
        assertFalse("空搜索数组应该返回false", 
                   StringUtils.startsWithAny("abcdef"));
        
        // 空序列测试
        assertFalse("空序列应该返回false", 
                   StringUtils.startsWithAny("", "abc"));
        
        // 边界情况测试
        assertTrue("相同字符串应该返回true", 
                  StringUtils.startsWithAny("abc", "abc"));
        assertFalse("较短序列应该返回false", 
                   StringUtils.startsWithAny("ab", "abc"));
    }
    
    @Test
    public void testStartsWith() {
        // 基本测试
        assertTrue("应该以指定字符串开头", 
                  StringUtils.startsWith("abcdef", "abc"));
        assertFalse("不应该以指定字符串开头", 
                   StringUtils.startsWith("abcdef", "xyz"));
        
        // 边界测试
        assertTrue("相同字符串应该返回true", 
                  StringUtils.startsWith("abc", "abc"));
        assertFalse("较长搜索串应该返回false", 
                   StringUtils.startsWith("ab", "abc"));
        assertFalse("null序列应该返回false", 
                   StringUtils.startsWith(null, "abc"));
        assertFalse("空序列应该返回false", 
                   StringUtils.startsWith("", "abc"));
        assertTrue("空搜索串应该返回true", 
                  StringUtils.startsWith("abc", ""));
    }
    
    @Test
    public void testIsNumber() {
        // 数字测试
        assertTrue("整数应该返回true", StringUtils.isNumber("123"));
        assertTrue("负数应该返回true", StringUtils.isNumber("-123"));
        assertTrue("正数应该返回true", StringUtils.isNumber("+123"));
        assertTrue("小数应该返回true", StringUtils.isNumber("123.45"));
        assertTrue("负小数应该返回true", StringUtils.isNumber("-123.45"));
        
        // 非数字测试
        assertFalse("包含字母不应该返回true", StringUtils.isNumber("123a"));
        assertFalse("空字符串不应该返回true", StringUtils.isNumber(""));
        assertFalse("null不应该返回true", StringUtils.isNumber(null));
        assertFalse("只有符号不应该返回true", StringUtils.isNumber("-"));
        assertFalse("多个小数点不应该返回true", StringUtils.isNumber("12.34.56"));
    }
    
    @Test
    public void testIsLongOrInt() {
        // 整数测试
        assertTrue("整数应该返回true", StringUtils.isLongOrInt("123"));
        assertTrue("负整数应该返回true", StringUtils.isLongOrInt("-123"));
        assertTrue("正整数应该返回true", StringUtils.isLongOrInt("+123"));
        
        // 非整数测试
        assertFalse("小数不应该返回true", StringUtils.isLongOrInt("123.45"));
        assertFalse("包含字母不应该返回true", StringUtils.isLongOrInt("123a"));
        assertFalse("空字符串不应该返回true", StringUtils.isLongOrInt(""));
        assertFalse("null不应该返回true", StringUtils.isLongOrInt(null));
    }
    
    @Test
    public void testIsNotEmpty() {
        // 非空测试
        assertTrue("非空字符串应该返回true", StringUtils.isNotEmpty("test"));
        assertTrue("空格字符串应该返回true", StringUtils.isNotEmpty(" "));
        assertTrue("非空集合应该返回true", StringUtils.isNotEmpty(Arrays.asList("test")));
        
        // 空测试
        assertFalse("null应该返回false", StringUtils.isNotEmpty(null));
        assertFalse("空字符串应该返回false", StringUtils.isNotEmpty(""));
        assertFalse("空集合应该返回false", StringUtils.isNotEmpty(new ArrayList<>()));
    }
    
    @Test
    public void testCapitalize() {
        // 基本测试
        assertEquals("首字母应该大写", "Test", StringUtils.capitalize("test"));
        assertEquals("已经大写应该保持", "Test", StringUtils.capitalize("Test"));
        assertEquals("单字符应该大写", "A", StringUtils.capitalize("a"));
        
        // 边界测试
        assertNull("null应该返回null", StringUtils.capitalize(null));
        assertEquals("空字符串应该保持", "", StringUtils.capitalize(""));
        assertEquals("特殊字符应该保持", "!test", StringUtils.capitalize("!test"));
    }
    
    @Test
    public void testLeftPad() {
        // 基本测试
        assertEquals("应该左填充", "zzzbat", StringUtils.leftPad("bat", 6, 'z'));
        assertEquals("刚好长度应该保持", "bat", StringUtils.leftPad("bat", 3, 'z'));
        assertEquals("长度不足应该保持", "bat", StringUtils.leftPad("bat", 1, 'z'));
        assertEquals("负长度应该保持", "bat", StringUtils.leftPad("bat", -1, 'z'));
        
        // 边界测试
        assertNull("null应该返回null", StringUtils.leftPad(null, 5, 'z'));
        assertEquals("空字符串应该填充", "zzz", StringUtils.leftPad("", 3, 'z'));
    }
    
    @Test
    public void testRepeat() {
        // 基本测试
        assertEquals("应该重复字符", "eee", StringUtils.repeat('e', 3));
        assertEquals("零次重复应该返回空", "", StringUtils.repeat('e', 0));
        assertEquals("负数重复应该返回空", "", StringUtils.repeat('e', -2));
        assertEquals("单次重复应该返回单字符", "a", StringUtils.repeat('a', 1));
    }
    
    @Test
    public void testJoin_Array() {
        String[] array = {"a", "b", "c"};
        
        // 基本测试
        assertEquals("应该正确连接", "a;b;c", StringUtils.join(array, ";"));
        assertEquals("null分隔符应该无分隔符连接", "abc", StringUtils.join(array, null));
        assertEquals("空字符串分隔符", "a|b|c", StringUtils.join(array, "|"));
        
        // 边界测试
        assertNull("null数组应该返回null", StringUtils.join((String[]) null, ";"));
        assertEquals("空数组应该返回空字符串", "", StringUtils.join(new String[0], ";"));
        
        // 包含null元素测试
        String[] arrayWithNull = {"a", null, "c"};
        assertEquals("null元素应该变为空字符串", "a;;c", StringUtils.join(arrayWithNull, ";"));
    }
    
    @Test
    public void testJoin_Collection() {
        List<String> list = Arrays.asList("a", "b", "c");
        
        // 基本测试
        assertEquals("应该正确连接", "a;b;c", StringUtils.join(list, ";"));
        assertEquals("字符分隔符测试", "a|b|c", StringUtils.join(list, '|'));
        
        // 边界测试
        assertEquals("空集合应该返回空字符串", "", StringUtils.join(new ArrayList<>(), ";"));
        assertEquals("null元素测试", "a;;c", StringUtils.join(Arrays.asList("a", null, "c"), ";"));
    }
    
    @Test
    public void testJoin_Iterable() {
        List<String> list = Arrays.asList("a", "b", "c");
        
        // 基本测试
        assertEquals("Iterable连接测试", "a;b;c", StringUtils.join(";", list));
        
        // 空Iterable测试
        assertEquals("空Iterable测试", "", StringUtils.join(";", new ArrayList<>()));
    }
    
    @Test
    public void testArrayToList() {
        String[] array = {"a", "b", "c"};
        List<String> list = StringUtils.arrayToList(array);
        
        assertNotNull("返回列表不应该为null", list);
        assertEquals("列表大小应该正确", 3, list.size());
        assertEquals("第一个元素应该正确", "a", list.get(0));
        assertEquals("最后一个元素应该正确", "c", list.get(2));
        
        // 空数组测试
        List<String> emptyList = StringUtils.arrayToList(new String[0]);
        assertNotNull("空数组转换不应该为null", emptyList);
        assertTrue("空数组转换应该为空列表", emptyList.isEmpty());
    }
    
    @Test
    public void testListToArray() {
        List<String> list = Arrays.asList("a", "b", "c");
        Object[] array = StringUtils.listToArray(list);
        
        assertNotNull("返回数组不应该为null", array);
        assertEquals("数组长度应该正确", 3, array.length);
        assertEquals("第一个元素应该正确", "a", array[0]);
        assertEquals("最后一个元素应该正确", "c", array[2]);
        
        // 空列表测试
        Object[] emptyArray = StringUtils.listToArray(new ArrayList<>());
        assertNotNull("空列表转换不应该为null", emptyArray);
        assertEquals("空列表转换应该为空数组", 0, emptyArray.length);
    }
    
    @Test
    public void testSplit() {
        // 基本测试
        String[] result = StringUtils.split("a,b,c", ",");
        assertArrayEquals("应该正确分割", new String[]{"a", "b", "c"}, result);
        
        // 正则表达式测试
        String[] regexResult = StringUtils.split("a1b2c", "\\d");
        assertArrayEquals("正则分割测试", new String[]{"a", "b", "c"}, regexResult);
        
        // 边界测试
        assertNull("null输入应该返回null", StringUtils.split(null, ","));
        String[] emptyResult = StringUtils.split("", ",");
        assertArrayEquals("空字符串应该返回空数组", new String[]{""}, emptyResult);
        
        // 无匹配分割符测试
        String[] noMatchResult = StringUtils.split("abc", ",");
        assertArrayEquals("无匹配应该返回原字符串", new String[]{"abc"}, noMatchResult);
    }
    
    @Test
    public void testGetReplaceStr() {
        // 准备测试数据
        Map<String, String> testData = new HashMap<>();
        testData.put("name", "张三");
        testData.put("age", "25");
        testData.put("info", "${name}今年${age}岁");
        
        // 测试Function包装
        java.util.function.Function<String, Object> getter = testData::get;
        
        // 基本测试
        Object result = StringUtils.getReplaceStr("name", getter, 0);
        assertEquals("基本获取测试", "张三", result);
        
        // 递归替换测试
        Object recursiveResult = StringUtils.getReplaceStr("info", getter, 0);
        assertEquals("递归替换测试", "张三今年25岁", recursiveResult);
        
        // null值处理测试
        Object nullResult = StringUtils.getReplaceStr("notExist", getter, 0);
        assertNull("nullType=0应该返回null", nullResult);
        
        Object nullResult1 = StringUtils.getReplaceStr("notExist", getter, 1);
        assertEquals("nullType=1应该返回键名", "notExist", nullResult1);
        
        Object nullResult2 = StringUtils.getReplaceStr("notExist", getter, 2);
        assertEquals("nullType=2应该返回{键名}", "{notExist}", nullResult2);
    }
}