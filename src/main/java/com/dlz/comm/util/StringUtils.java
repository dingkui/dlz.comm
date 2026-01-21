package com.dlz.comm.util;

import com.dlz.comm.json.JSONMap;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * 
 * 提供便捷的字符串处理功能，包括字符串格式化、判空、补零、首字母大写等操作
 * 
 * @author dingkui
 * @since 2023
 */
@Slf4j
public class StringUtils {
    private static Pattern paraPattern = Pattern.compile("\\$\\{([\\w\\.]+)\\}");

    /**
     * 根据属性名称获得对应值，并递归替换字符串中的占位符
     *
     * @param name 属性名称
     * @param c 获取属性值的函数
     * @param nullType 数据为null时的处理方式：0返回null,1返回name,其他返回 {name}
     * @return 属性对应的值，如果是字符串则递归替换其中的占位符
     */
    public static Object getReplaceStr(String name, Function<String,Object> c, int nullType) {
        Object ret = c.apply(name);
        if(ret == null){
            return nullType == 0 ? null : nullType == 1 ? name : "{" + name + "}";
        }
        if(ret instanceof CharSequence){
            String retStr = ret.toString().trim();
            Matcher mat = paraPattern.matcher(retStr);
            StringBuilder sb = null;
            int end = 0;
            while(mat.find()){
                String group = mat.group(1);
                if(sb == null){
                    sb = new StringBuilder();
                }
                sb.append(retStr, 0, mat.start());
                sb.append(getReplaceStr(group, c, 2));
                end = mat.end();
            }
            if(end == 0){
                return retStr;
            }
            sb.append(retStr.substring(end));
            return sb.toString();
        }
        return ret;
    }

    /**
     * 使用指定分隔符合并字符串集合
     *
     * @param separator 分隔符
     * @param strings 字符串集合
     * @return 合并后的字符串
     */
    public static String join(CharSequence separator, Iterable<?> strings) {
        Iterator<?> i = strings.iterator();
        if (!i.hasNext()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(i.next().toString());
        while (i.hasNext()) {
            sb.append(separator);
            sb.append(i.next().toString());
        }
        return sb.toString();
    }

    /**
     * 如果字符串为null则返回空字符串
     *
     * @param cs 待处理的字符串
     * @return 如果cs为null返回空字符串，否则返回原字符串
     */
    public static String NVL(String cs) {
        return NVL(cs, "");
    }

    /**
     * 如果字符串为null则返回默认值
     *
     * @param cs 待处理的字符串
     * @param defaultStr 默认值
     * @return 如果cs为null返回默认值，否则返回原字符串
     */
    public static String NVL(String cs, String defaultStr) {
        return cs == null ? defaultStr : cs;
    }

    /**
     * 根据类名生成Bean ID
     * 
     * 规则：将类名首字母小写
     *
     * @param className 完整类名
     * @return Bean ID
     */
    public static String getBeanId(String className) {
        int lastIndexOf = className.lastIndexOf(".");
        return className.substring(lastIndexOf + 1, lastIndexOf + 2).toLowerCase() + className.substring(lastIndexOf + 2);
    }

    /**
     * 根据类生成Bean ID
     * 
     * 规则：将类名首字母小写
     *
     * @param clazz 类对象
     * @return Bean ID
     */
    public static String getBeanId(Class<?> clazz) {
        return getBeanId(clazz.getName());
    }

    private static Pattern myMsgPattern = Pattern.compile("\\{([\\w]*)\\}");
    private static Pattern myMsgPatternSub = Pattern.compile(".*([\\d]+)$");
    
    /**
     * 格式化文本，{} 表示占位符
     * 
     * 支持位置占位符，如 {xxx0},{0}表示用参数下标，支持用字符说明，结尾的数字是下标
     * 无下标时，默认采用当前所在的序号
     * 下标无效时，此处不做替换
     * 
     * 示例：
     * 通常使用：formatMsg("this is {} for {}", "a", "b") -> this is a for b
     * 指定下标： formatMsg("this is {1} for {}", "a", "b") -> this is b for b
     * 说明+明确下标： formatMsg("this is {b1} for {}", "a", "b") -> this is b for b
     * 说明+明确下标： formatMsg("this is {xx1x_1} for {}", "a", "b") -> this is b for b
     * 下标无效： formatMsg("this is {9} for {}", "a", "b") -> this is {9} for b
     * 下标无效： formatMsg("this is {} for {} and {}", "a", "b") -> this is a for b and {}
     *
     * @param message 待格式化的消息模板
     * @param paras 格式化参数
     * @return 格式化后的文本
     */
    public static String formatMsg(Object message, Object... paras) {
        String msg = ValUtil.toStr(message, "");
        Matcher mat = myMsgPattern.matcher(msg);
        StringBuffer sb = new StringBuffer();
        int end = 0;
        int i = 0;
        while (mat.find()) {
            int index = i;
            String indexStr = mat.group(1);
            if(indexStr.length() > 0){
                indexStr = myMsgPatternSub.matcher(indexStr).replaceAll("$1");
                if (indexStr.length() > 0) {
                     index = Integer.parseInt(indexStr);
                }
            }
            sb.append(msg, end, mat.start());
            if (paras.length > index) {
                sb.append(ValUtil.toStr(paras[index]));
            } else {
                sb.append(mat.group(0));
            }
            end = mat.end();
            i++;
        }
        return sb.append(msg, end, msg.length()).toString();
    }

    /**
     * 替换内容匹配符：如 ${bb}
     */
    private static Pattern PATTERN_REPLACE = Pattern.compile("\\$\\{(\\w[\\.\\w]*)\\}");
    
    /**
     * 对消息语句中 ${aa} 的内容进行文本替换
     * 
     * @param input 待处理的输入字符串
     * @param m JSONMap对象，提供替换值
     * @return 替换后的字符串
     */
    public static String formatMsg(Object input, JSONMap m) {
        String msg = ValUtil.toStr(input, "");
        int length = msg.length();
        Matcher mat = PATTERN_REPLACE.matcher(msg);
        int start = 0;
        StringBuffer sb = new StringBuffer();
        while (mat.find()) {
            String key = mat.group(1);
            String o = m.getStr(key);
            sb.append(msg, start, mat.start());
            if(o != null){
                sb.append(o);
            }
            start = mat.end();
        }
        if (start == 0) {
            return msg;
        }
        sb.append(msg, start, length);
        return formatMsg(sb.toString(), m);
    }

    /**
     * 在数字前面补0至指定长度
     *
     * @param i 待处理的数字
     * @param length 目标长度
     * @return 补0后的字符串
     */
    public static String addZeroBefor(long i, int length) {
        return leftPad(String.valueOf(i), length, '0');
    }

    /**
     * 检查对象是否为空
     * 
     * 支持的类型包括：Collection、Map、Array、CharSequence
     * 
     * @param obj 待检查的对象
     * @return 如果对象为空返回true，否则返回false
     */
    @SuppressWarnings({"rawtypes"})
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof Optional) {
            return !((Optional<?>) obj).isPresent();
        }
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        }
        return false;
    }

    /**
     * 检查对象数组中是否存在任意一个空对象
     * 
     * @param cs 待检查的对象数组
     * @return 如果存在任意一个空对象返回true，否则返回false
     */
    public static boolean isAnyEmpty(Object... cs) {
        for (final Object c : cs) {
            if (isEmpty(c)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 判断输入的字符序列是否为空或纯空格
     * 
     * 示例：
     * StringUtils.isBlank(null) = true
     * StringUtils.isBlank("") = true
     * StringUtils.isBlank(" ") = true
     * StringUtils.isBlank("12345") = false
     * StringUtils.isBlank(" 12345 ") = false
     * 
     * @param cs 待检查的字符序列
     * @return 如果字符序列为空或纯空格返回true，否则返回false
     */
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查字符序列数组中是否存在任意一个空白字符序列
     * 
     * @param cs 待检查的字符序列数组
     * @return 如果存在任意一个空白字符序列返回true，否则返回false
     */
    public static boolean isAnyBlank(CharSequence... cs) {
        for (final CharSequence c : cs) {
            if (isBlank(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查字符序列数组中是否全部为空白字符序列
     * 
     * @param cs 待检查的字符序列数组
     * @return 如果全部为空白字符序列返回true，否则返回false
     */
    public static boolean isAllBlank(CharSequence... cs) {
        for (final CharSequence c : cs) {
            if (!isBlank(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查字符序列是否以指定的任一字符串开头
     * 
     * @param sequence 待检查的字符序列
     * @param searchStrings 查找的字符串数组
     * @return 如果以任一字符串开头返回true，否则返回false
     */
    public static boolean startsWithAny(final CharSequence sequence, final CharSequence... searchStrings) {
        if (isEmpty(sequence) || searchStrings.length == 0) {
            return false;
        }
        for (final CharSequence searchString : searchStrings) {
            if (startsWith(sequence, searchString)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查字符序列是否以指定字符串开头
     * 
     * @param sequence 待检查的字符序列
     * @param searchString 指定的字符串
     * @return 如果以指定字符串开头返回true，否则返回false
     */
    public static boolean startsWith(final CharSequence sequence, CharSequence searchString) {
        int length = searchString.length();
        if(isEmpty(sequence) || sequence.length() < length){
            return false;
        }
        for (int i = 0; i < length; i++) {
            if(sequence.charAt(i) != searchString.charAt(i)){
                return false;
            }
        }
        return true;
    }

    /**
     * 检查字符序列是否为数值
     * 
     * @param o 待检查的字符序列
     * @return 如果是数值返回true，否则返回false
     */
    public static boolean isNumber(CharSequence o) {
        return o.length() > 0 && o.toString().replaceAll("[\\d\\.+-]", "").length() == 0;
    }

    /**
     * 检查字符序列是否为长整型或整型
     * 
     * @param o 待检查的字符序列
     * @return 如果是长整型或整型返回true，否则返回false
     */
    public static boolean isLongOrInt(CharSequence o) {
        return o.length() > 0 && o.toString().replaceAll("[\\d+-]", "").length() == 0;
    }

    /**
     * 检查对象是否非空
     * 
     * @param cs 待检查的对象
     * @return 如果对象非空返回true，否则返回false
     */
    public static boolean isNotEmpty(Object cs) {
        return !isEmpty(cs);
    }

    /**
     * 将字符串首字母大写
     * 
     * 示例：
     * StringUtils.capitalize(null)  = null
     * StringUtils.capitalize("")    = ""
     * StringUtils.capitalize("cat") = "Cat"
     * StringUtils.capitalize("cAt") = "CAt"
     * 
     * @param str 待处理的字符串
     * @return 首字母大写后的字符串
     */
    public static String capitalize(final String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        char firstChar = str.charAt(0);
        if (Character.isTitleCase(firstChar)) {
            // already capitalized
            return str;
        }
        return new StringBuilder(strLen).append(Character.toTitleCase(firstChar)).append(str.substring(1)).toString();
    }

    /**
     * 在字符串左边添加指定字符达到指定的长度
     * 
     * 示例：
     * StringUtils.leftPad(null, *, *)     = null
     * StringUtils.leftPad("", 3, 'z')     = "zzz"
     * StringUtils.leftPad("bat", 3, 'z')  = "bat"
     * StringUtils.leftPad("bat", 5, 'z')  = "zzbat"
     * StringUtils.leftPad("bat", 1, 'z')  = "bat"
     * StringUtils.leftPad("bat", -1, 'z') = "bat"
     * 
     * @param str 待处理的字符串
     * @param size 目标长度
     * @param padChar 填充字符
     * @return 左填充后的字符串
     */
    public static String leftPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        return repeat(padChar, pads).concat(str);
    }

    /**
     * 构造指定个数的字符的字符串
     * 
     * 示例：
     * StringUtils.repeat('e', 0)  = ""
     * StringUtils.repeat('e', 3)  = "eee"
     * StringUtils.repeat('e', -2) = ""
     * 
     * @param ch 要重复的字符
     * @param repeat 重复次数
     * @return 重复字符构成的字符串
     */
    public static String repeat(final char ch, int repeat) {
        final char[] buf = new char[repeat];
        while (repeat-- > 0) {
            buf[repeat] = ch;
        }
        return new String(buf);
    }

    /**
     * 使用指定分隔符合并数组元素
     * 
     * 示例：
     * StringUtils.join(null, *)         = null
     * StringUtils.join([], *)           = ""
     * StringUtils.join([null], *)       = ""
     * StringUtils.join([1, 2, 3], ";")  = "1;2;3"
     * StringUtils.join([1, 2, 3], null) = "123"
     * 
     * @param array 待合并的数组
     * @param separator 分隔符
     * @return 合并后的字符串
     */
    public static <T> String join(final T[] array, String separator) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return "";
        }
        final StringJoiner buf = new StringJoiner(NVL(separator));
        for (T element : array){
            buf.add(ValUtil.toStr(element, ""));
        }
        return buf.toString();
    }

    /**
     * 将数组转换为列表
     * 
     * @param array 待转换的数组
     * @return 转换后的列表
     */
    public static <T> List<T> arrayToList(final T[] array) {
        return Arrays.asList(array);
    }

    /**
     * 将列表转换为数组
     * 
     * @param list 待转换的列表
     * @return 转换后的数组
     */
    public static <T> Object[] listToArray(final Collection<T> list) {
        return list.toArray();
    }

    /**
     * 使用指定分隔符合并集合元素
     * 
     * @param array 待合并的集合
     * @param separator 分隔符
     * @return 合并后的字符串
     */
    public static <T> String join(final Collection<T> array, String separator) {
        return join(listToArray(array), separator);
    }

    /**
     * 使用指定字符分隔符合并集合元素
     * 
     * @param array 待合并的集合
     * @param separator 分隔符
     * @return 合并后的字符串
     */
    public static <T> String join(final Collection<T> array, char separator) {
        return join(listToArray(array), String.valueOf(separator));
    }

    /**
     * 按正则表达式分割字符串
     * 
     * @param value 待分割的字符串
     * @param regex 分割正则表达式
     * @return 分割后的字符串数组
     */
    public static String[] split(String value, String regex) {
        return value == null ? null : value.split(regex);
    }

}