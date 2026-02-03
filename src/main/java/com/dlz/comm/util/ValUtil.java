package com.dlz.comm.util;

import com.dlz.comm.consts.Charsets;
import com.dlz.comm.exception.SystemException;
import com.dlz.comm.json.JSONList;
import com.dlz.comm.json.JSONMap;
import com.dlz.comm.util.system.FieldReflections;
import com.dlz.comm.util.system.annotation.SetValue;
import com.fasterxml.jackson.databind.JavaType;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

/**
 * 对象转换工具类
 *
 * @author dk 2017-11-03
 */
@Slf4j
public class ValUtil {
    public final static Integer ZERO_INT = 0;
    public final static Long ZERO_LONG = 0l;
    public final static Float ZERO_FLOAT = 0f;
    public final static Double ZERO_DOUBLE = 0.0;
    public final static String STR_BLANK = "";
    public final static String STR_NULL = "null";

    private static Number toNumber(Object input, Number defaultV) {
        if (input == null || "".equals(input)) {
            return defaultV;
        }
        if (input instanceof Number) {
            return (Number) input;
        }
        return new BigDecimal(input.toString());
    }

    public static BigDecimal toBigDecimal(Object input) {
        return toBigDecimal(input, null);
    }

    public static BigDecimal toBigDecimal(Object input, BigDecimal defaultV) {
        Number o = toNumber(input, null);
        if (o == null) {
            return defaultV;
        }
        if (o instanceof BigDecimal) {
            return (BigDecimal) o;
        } else if (o instanceof Float) {
            return new BigDecimal(o.toString());
        } else if (o instanceof Double) {
            return new BigDecimal(o.toString());
        } else if (o instanceof Integer) {
            return new BigDecimal(o.intValue());
        } else if (o instanceof Long) {
            return new BigDecimal(o.longValue());
        }
        return new BigDecimal(o.toString());
    }

    public static BigDecimal toBigDecimalZero(Object input) {
        return toBigDecimal(input, BigDecimal.ZERO);
    }


    public static Double toDouble(Object input) {
        return toDouble(input, null);
    }

    public static Double toDouble(Object input, Double defaultV) {
        Number o = toNumber(input, null);
        if (o == null) {
            return defaultV;
        }
        return o.doubleValue();
    }

    public static Double toDoubleZero(Object input) {
        return toDouble(input, ZERO_DOUBLE);
    }

    public static Float toFloat(Object input) {
        return toFloat(input, null);
    }

    public static Float toFloat(Object input, Float defaultV) {
        Number o = toNumber(input, null);
        if (o == null) {
            return defaultV;
        }
        return o.floatValue();
    }

    public static Float toFloatZero(Object input) {
        return toFloat(input, ZERO_FLOAT);
    }

    public static Integer toInt(Object input) {
        return toInt(input, null);
    }

    public static Integer toInt(Object input, Integer defaultV) {
        Number o = toNumber(input, null);
        if (o == null) {
            return defaultV;
        }
        return o.intValue();
    }

    public static Integer toIntZero(Object input) {
        return toInt(input, ZERO_INT);
    }

    public static Long toLong(Object input) {
        return toLong(input, null);
    }

    public static Long toLong(Object input, Long defaultV) {
        Number o = toNumber(input, null);
        if (o == null) {
            return defaultV;
        }
        return o.longValue();
    }

    public static Long toLongZero(Object input) {
        return toLong(input, ZERO_LONG);
    }


    public static Boolean toBoolean(Object input) {
        return toBoolean(input, false);
    }

    public static Boolean toBoolean(Object input, Boolean defaultV) {
        if (input == null) {
            return defaultV;
        }
        if (input instanceof Boolean) {
            return (Boolean) input;
        }
        if (input instanceof Number) {
            return ((Number) input).intValue()!=0;
        }
        String r = input.toString();

        return !"false".equalsIgnoreCase(r) && !"0".equals(r) && !"".equals(r);
    }

    public static String toStrBlank(Object input) {
        return toStr(input, STR_BLANK);
    }

    public static String toStrWithEmpty(Object input, String defaultValue) {
        if (null == input || input.equals(STR_NULL) || input.equals(STR_BLANK)) {
            return defaultValue;
        }
        return toStr(input, defaultValue);
    }

    public static String toStr(Object input) {
        return toStr(input, null);
    }

    public static String toStr(Object input, String defaultV) {
        if (input == null) {
            return defaultV;
        }
        if (input instanceof CharSequence || input instanceof Number) {
            return input.toString();
        }
        return JacksonUtil.getJson(input);
    }
    public static String toStr(byte[] input, Charset charset) {
        return new String(input,charset);
    }
    public static String toStr(byte[] input) {
        if(input==null){
            return null;
        }
        return new String(input, Charsets.UTF_8);
    }

    public static JSONList toList(Object input) {
        return toList(input, (List) null);
    }

    public static JSONList toList(Object input, Collection defaultV) {
        if (input == null) {
            return new JSONList(defaultV);
        }
        if (input instanceof JSONList) {
            return (JSONList) input;
        }
        try {
            return new JSONList(input);
        } catch (RuntimeException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return new JSONList(defaultV);
    }

    public static <T> List<T> toList(Object input, Class<T> clazz) {
        T[] array = toArray(input, clazz);
        if (array == null) {
            return null;
        }
        return Arrays.asList(array);
    }

    public static JSONList toListEmputy(Object input) {
        return toList(input, new ArrayList());
    }


    public static Object[] toArray(Object input) {
        return toArray(input, (Object[]) null);
    }

    public static Object[] toArray(Object input, Object[] defaultV) {
        if (input == null) {
            return defaultV;
        }
        if (input instanceof Object[]) {
            return (Object[]) input;
        } else if (input instanceof Collection) {
            return ((Collection) input).toArray();
        }
        try {
            return new JSONList(input).toArray();
        } catch (RuntimeException e) {
            log.warn(e.getMessage());
        }
        return defaultV;
    }

    public static <T> T[] toArray(Collection input, Class<T> clazz) {
        if (input == null) {
            return null;
        }
        T[] re = (T[]) Array.newInstance(clazz, input.size());
        final Iterator it = input.iterator();
        int i = 0;
        while (it.hasNext()) {
            re[i++] = toObj(it.next(), clazz);
        }
        return re;
    }

    public static <T> T[] toArray(Object[] input, Class<T> clazz) {
        if (input == null) {
            return null;
        }
        T[] re = (T[]) Array.newInstance(clazz, input.length);
        for (int i = 0; i < input.length; i++) {
            re[i] = toObj(input[i], clazz);
        }
        return re;
    }

    public static <T> T[] toArray(Object input, Class<T> clazz) {
        if (input == null) {
            return null;
        }
        if (input instanceof Collection) {
            return toArray((Collection) input, clazz);
        }
        if (input instanceof Object[]) {
            return toArray((Object[]) input, clazz);
        }
        if (input instanceof CharSequence) {
            String string = input.toString();
            if(JacksonUtil.isJsonArray(string)){
                return toArray(JacksonUtil.readListValue(string, clazz), clazz);
            }
            return toArray(string.split(","), clazz);
        }
        try {
            String string = toStr(input);
            if (JacksonUtil.isJsonArray(string)) {
                return toArray(JacksonUtil.readListValue(string, clazz), clazz);
            } else {
                throw new RuntimeException("参数不能转换成List:" + string);
            }
        } catch (RuntimeException e) {
            log.warn(e.getMessage());
        }
        return null;
    }

    public static LocalDateTime toLocalDateTime(Object input, String format, LocalDateTime defaultV) {
        if (input == null) {
            return defaultV;
        }
        if (LocalDateTime.class.isAssignableFrom(input.getClass())) {
            return (LocalDateTime) input;
        }
        if (Date.class.isAssignableFrom(input.getClass())) {
            return DateUtil.getLocalDateTime((Date) input);
        }
        if (Number.class.isAssignableFrom(input.getClass())) {
            return DateUtil.getLocalDateTime(new Date(((Number) input).longValue()));
        }
        return DateUtil.getLocalDateTime(toStr(input), format);
    }
    public static LocalDateTime toLocalDateTime(Object input) {
        return toLocalDateTime(input, null, null);
    }

    public static LocalDateTime toLocalDateTime(Object input, String format) {
        return toLocalDateTime(input, format, null);
    }

    public static Date toDate(Object input) {
        return toDate(input, null, null);
    }

    public static Date toDate(Object input, String format) {
        return toDate(input, format, null);
    }

    public static Date toDate(Object input, String format, Date defaultV) {
        if (input == null) {
            return defaultV;
        }
        if (Date.class.isAssignableFrom(input.getClass())) {
            return (Date) input;
        }
        if (Number.class.isAssignableFrom(input.getClass())) {
            return new Date(((Number) input).longValue());
        }
        if (LocalDateTime.class.isAssignableFrom(input.getClass())) {
            return DateUtil.getDate((LocalDateTime) input);
        }
        if (LocalDate.class.isAssignableFrom(input.getClass())) {
            return DateUtil.getDate((LocalDate) input);
        }
        return DateUtil.getDate(toStr(input), format);
    }

    public static Date toDateNow(Object input) {
        return toDate(input, null, new Date());
    }

    public static String toDateStr(Object input) {
        return toDateStr(input, null, null);
    }

    public static String toDateStr(Object input, String format) {
        return toDateStr(input, format, null);
    }

    public static String toDateStr(Object input, String format, Date defaultV) {
        Date date = toDate(input, null, defaultV);
        if (date == null) {
            return "";
        }
        if (format == null) {
            return DateUtil.DATETIME.format(date);
        }
        return DateUtil.format(date, format);
    }

    public static String toDateStrNow(Object input) {
        return toDateStr(input, null, new Date());
    }

    private static Map<Class<?>, Function<?, ?>> natveConverts = new HashMap<>();

    static {
        natveConverts.put(String.class, ValUtil::toStr);
        natveConverts.put(Integer.class, ValUtil::toInt);
        natveConverts.put(Long.class, ValUtil::toLong);
        natveConverts.put(Date.class, ValUtil::toDate);
        natveConverts.put(BigDecimal.class, ValUtil::toBigDecimal);
        natveConverts.put(Float.class, ValUtil::toFloat);
        natveConverts.put(Double.class, ValUtil::toDouble);
        natveConverts.put(Boolean.class, ValUtil::toBoolean);
        natveConverts.put(LocalDateTime.class, ValUtil::toLocalDateTime);
        natveConverts.put(null, input -> input);
    }

    public static <T> T toNativeObj(Object input, Class<T> clazz) {
        if (input == null) {
            return null;
        }
        if (clazz.isAssignableFrom(input.getClass())) {
            return (T) input;
        }
        final Function function = natveConverts.get(clazz);
        if (function != null) {
            return (T) function.apply(input);
        }
        return null;
    }

    public static <T> T toObj(Object input, Class<T> classs) {
        if (input == null || classs == null) {
            return (T) input;
        }
        T re = toNativeObj(input, classs);
        return re != null ? re : JacksonUtil.coverObj(input, classs);
    }

    public static <T> T toObj(Object input, Type type) {
        if (input == null || type == null) {
            return (T) input;
        }
        if (type instanceof Class) {
            return toObj(input, (Class<? extends T>) type);
        } else if (type instanceof ParameterizedType) {
            return JacksonUtil.coverObj(input, JacksonUtil.mkJavaType(type));
        } else if (type instanceof JavaType) {
            return JacksonUtil.coverObj(input, (JavaType) type);
        }
        throw new SystemException(type + "未识别泛型参数");
    }

    public static <T> T toObj(Object input, JavaType javaType) {
        if (input == null || javaType == null) {
            return (T) input;
        }
        T re = toNativeObj(input, (Class<T>) javaType.getRawClass());
        return re != null ? re : JacksonUtil.coverObj(input, javaType);
    }

    public static boolean isEmpty(Object cs) {
        if (cs == null) {
            return true;
        }
        if (cs instanceof Collection) {
            return ((Collection) cs).isEmpty();
        } else if (cs instanceof Map) {
            return ((Map) cs).isEmpty();
        } else if (cs.getClass().isArray()) {
            return ((Object[]) cs).length == 0;
        } else if (cs instanceof CharSequence) {
            return ((CharSequence) cs).length() == 0;
        } else {
            return false;
        }
    }


//    public static void main(String[] args) {
//        System.out.println(toDate("2018-21-24 19:23:39.583"));
//        System.out.println(toDate("2018-1-24 19:23"));
//        System.out.println(toDate("19:23"));
//        System.out.println(toDate("2018-1-24"));
//    }
}