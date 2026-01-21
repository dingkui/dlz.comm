package com.dlz.comm.util;

import com.dlz.comm.exception.SystemException;
import com.dlz.comm.json.JSONList;
import com.dlz.comm.json.JSONMap;
import com.dlz.comm.util.jackson.DlzJavaTimeModule;
import com.dlz.comm.util.jackson.JacksonObjectDeserializer;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext.Impl;
import com.fasterxml.jackson.databind.deser.Deserializers;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Jackson JSON处理工具类
 * 
 * 提供便捷的JSON序列化、反序列化、类型转换等功能
 * 
 * 主要特性包括：
 * 1. 支持自定义对象与JSON字符串互转
 * 2. 支持复杂类型的反序列化
 * 3. 支持JSON路径取值
 * 4. 提供统一的日期时间格式处理
 * 
 * @author dingkui
 * @since 2013-09-13
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
public class JacksonUtil {
    private static ObjectMapper objectMapper;
    private final static Class<?> CLASS_OBJECT = Object.class;

    static {
        //添加自定义解析器，将默认的linkedHashMap 和List对应修改为 JSONMap和JSONList
        Deserializers deserializers = new Deserializers.Base() {
            @Override
            public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) {
                Class<?> rawType = type.getRawClass();
                if (rawType == CLASS_OBJECT) {
                    //添加自定义解析器，将默认的linkedHashMap 和List对应修改为 JSONMap和JSONList
                    return new JacksonObjectDeserializer();
                }
                return null;
            }
        };
        final DeserializerFactoryConfig config = new DeserializerFactoryConfig().withAdditionalDeserializers(deserializers);
        final DefaultDeserializationContext dc = new Impl(new BeanDeserializerFactory(config));
        objectMapper = new ObjectMapper(null, null, dc);
        // https://github.com/FasterXML/jackson-databind
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 单引号
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setSerializationInclusion(Include.NON_NULL);

        /**
         * 配置默认的日期转换格式 ，参考http://wiki.fasterxml.com/JacksonFAQDateHandling
         * 序列化时，日期的统一格式
         */
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        //设置地点为中国
        objectMapper.setLocale(Locale.CHINA);
        //设置为中国上海时区
        objectMapper.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));


//        //去掉默认的时间戳格式
//        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//        // 允许JSON字符串包含非引号控制字符（值小于32的ASCII字符，包含制表符和换行符）
//        objectMapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
//        objectMapper.configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true);
//        objectMapper.findAndRegisterModules();
//        //失败处理
//        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//        //单引号处理
//        objectMapper.configure(JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature(), true);
//        //反序列化时，属性不存在的兼容处理s
//        objectMapper.getDeserializationConfig().withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//        //日期格式化
          objectMapper.registerModule(DlzJavaTimeModule.INSTANCE);
    }

    /**
     * 获取ObjectMapper实例
     * 
     * @return ObjectMapper实例
     */
    public static ObjectMapper getInstance() {
        return objectMapper;
    }

    /**
     * 将对象转换为JSON字符串
     * 
     * @param o 待转换的对象
     * @return JSON字符串，如果转换失败则抛出异常
     */
    public static String getJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON转换异常" + e.getMessage(), e);
        }
    }

    /**
     * 将对象序列化成 json byte 数组
     *
     * @param object javaBean对象
     * @return JSON字符串对应的字节数组
     */
    public static byte[] toJsonAsBytes(Object object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw SystemException.build(e);
        }
    }

    /**
     * 将字符串反序列化为JSONMap对象
     * 
     * @param content JSON字符串内容
     * @return JSONMap对象
     */
    public static JSONMap readValue(String content) {
        return readValue(content, mkJavaType(JSONMap.class));
    }

    /**
     * 将对象反序列化为JSONMap对象
     * 
     * @param content 待转换的内容
     * @return JSONMap对象
     */
    public static JSONMap readValue(Object content) {
        return readValue(content, mkJavaType(JSONMap.class));
    }

    /**
     * 将字符串反序列化为指定类型对象
     * 
     * @param content JSON字符串内容
     * @param valueType 目标类型
     * @param <T> 目标类型泛型
     * @return 指定类型的对象
     */
    public static <T> T readValue(String content, Class<T> valueType) {
        return readValue(content, mkJavaType(valueType));
    }

    /**
     * 将对象反序列化为指定类型对象
     * 
     * @param content 待转换的内容
     * @param valueType 目标类型
     * @param <T> 目标类型泛型
     * @return 指定类型的对象
     */
    public static <T> T readValue(Object content, Class<T> valueType) {
        return readValue(content, mkJavaType(valueType));
    }

    /**
     * 将对象反序列化为指定类型列表
     * 
     * @param content 待转换的内容
     * @param elementClass 列表元素类型
     * @param <T> 列表元素类型泛型
     * @return 指定类型的列表
     */
    public static <T> List<T> readList(Object content, Class<T> elementClass) {
        return readValue(content, mkJavaType(ArrayList.class, elementClass));
    }

    /**
     * 将字符串反序列化为JSONList对象
     * 
     * @param content JSON字符串内容
     * @return JSONList对象
     */
    public static JSONList readList(String content) {
        return readValue(content, mkJavaType(JSONList.class));
    }

    /**
     * 将对象反序列化为JSONList对象
     * 
     * @param content 待转换的内容
     * @return JSONList对象
     */
    public static JSONList readList(Object content) {
        return readValue(content, mkJavaType(JSONList.class));
    }

    /**
     * 将字符串反序列化为指定类型对象
     * 
     * @param content JSON字符串内容
     * @param valueType 目标类型
     * @param <T> 目标类型泛型
     * @return 指定类型的对象，如果转换失败则返回null
     */
    public static <T> T readValue(String content, JavaType valueType) {
        try {
            return objectMapper.readValue(content, valueType);
        } catch (Exception e) {
            log.error("JacksonUtil.readValue error:valueType={} content={}", valueType, content);
            log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }


    /**
     * 将TreeNode转换为指定类型的对象
     *
     * @param treeNode JSON树节点
     * @param valueType 目标类型
     * @param <T> 目标类型泛型
     * @return 转换结果
     */
    public static <T> T treeToValue(TreeNode treeNode, Class<T> valueType) {
        try {
            return objectMapper.treeToValue(treeNode, valueType);
        } catch (JsonProcessingException e) {
            throw SystemException.build(e);
        }
    }

    /**
     * 将对象转换为JsonNode
     *
     * @param value 待转换的对象
     * @return JsonNode对象
     */
    public static JsonNode valueToTree(Object value) {
        return objectMapper.valueToTree(value);
    }

    /**
     * 将内容转换为JsonNode
     *
     * @param content 待转换的内容，支持多种类型如String, byte[], InputStream, File, URL, Reader, JsonParser等
     * @return JsonNode对象
     */
    public static JsonNode readTree(Object content) {
        try {
            if (content instanceof CharSequence) {
                return objectMapper.readTree(content.toString());
            } else if (content instanceof byte[]) {
                return objectMapper.readTree((byte[]) content);
            } else if (content instanceof InputStream) {
                return objectMapper.readTree((InputStream) content);
            } else if (content instanceof File) {
                return objectMapper.readTree((File) content);
            } else if (content instanceof URL) {
                return objectMapper.readTree((URL) content);
            } else if (content instanceof Reader) {
                return objectMapper.readTree((Reader) content);
            } else if (content instanceof JsonParser) {
                return objectMapper.readTree((JsonParser) content);
            }
            return objectMapper.readTree(getJson(content));
        } catch (IOException e) {
            throw SystemException.build(e);
        }
    }

    /**
     * 将对象反序列化为指定类型对象
     * 
     * @param content 待转换的内容，支持多种类型
     * @param valueType 目标类型
     * @param <T> 目标类型泛型
     * @return 指定类型的对象，如果转换失败则返回null
     */
    public static <T> T readValue(Object content, JavaType valueType) {
        try {
            if (content instanceof CharSequence) {
                return objectMapper.readValue(content.toString(), valueType);
            } else if (content instanceof byte[]) {
                return objectMapper.readValue((byte[]) content, valueType);
            } else if (content instanceof InputStream) {
                return objectMapper.readValue((InputStream) content, valueType);
            } else if (content instanceof File) {
                return objectMapper.readValue((File) content, valueType);
            } else if (content instanceof URL) {
                return objectMapper.readValue((URL) content, valueType);
            } else if (content instanceof Reader) {
                return objectMapper.readValue((Reader) content, valueType);
            } else if (content instanceof DataInput) {
                return objectMapper.readValue((DataInput) content, valueType);
            } else if (content instanceof JsonParser) {
                return objectMapper.readValue((JsonParser) content, valueType);
            }
            return objectMapper.readValue(getJson(content), valueType);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace("JacksonUtil.readValue error,content:" + content + " valueType:" + valueType, e));
            return null;
        }
    }

    /**
     * 将对象反序列化为指定类型对象
     * 
     * @param content 待转换的内容，支持多种类型
     * @param valueType 目标类型引用
     * @param <T> 目标类型泛型
     * @return 指定类型的对象，如果转换失败则返回null
     */
    public static <T> T readValue(Object content, TypeReference<T> valueType) {
        try {
            if (content instanceof CharSequence) {
                return objectMapper.readValue(content.toString(), valueType);
            } else if (content instanceof byte[]) {
                return objectMapper.readValue((byte[]) content, valueType);
            } else if (content instanceof InputStream) {
                return objectMapper.readValue((InputStream) content, valueType);
            } else if (content instanceof File) {
                return objectMapper.readValue((File) content, valueType);
            } else if (content instanceof URL) {
                return objectMapper.readValue((URL) content, valueType);
            } else if (content instanceof Reader) {
                return objectMapper.readValue((Reader) content, valueType);
            } else if (content instanceof JsonParser) {
                return objectMapper.readValue((JsonParser) content, valueType);
            }
            return objectMapper.readValue(getJson(content), valueType);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace("JacksonUtil.readValue error,content:" + content + " valueType:" + valueType, e));
            return null;
        }
    }

    /**
     * 将字符串反序列化为指定类型对象
     * 
     * @param content JSON字符串内容
     * @param valueType 目标类型引用
     * @param <T> 目标类型泛型
     * @return 指定类型的对象，如果转换失败则返回null
     */
    public static <T> T readValue(String content, TypeReference<T> valueType) {
        try {
            return objectMapper.readValue(content, valueType);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace("JacksonUtil.readValue error,content:" + content + " valueType:" + valueType, e));
            return null;
        }
    }

    /**
     * 将字符串反序列化为指定类型列表
     * 
     * @param content JSON字符串内容
     * @param valueType 列表元素类型
     * @param <T> 列表元素类型泛型
     * @return 指定类型的列表
     */
    public static <T> List<T> readListValue(String content, Class<T> valueType) {
        return readValue(content, mkJavaType(List.class, valueType));
    }

    /**
     * 使用Jackson进行类型转换
     *
     * @param fromValue 源对象
     * @param toValueType 目标类型
     * @param <T> 目标类型泛型
     * @return 转换结果
     */
    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        return objectMapper.convertValue(fromValue, toValueType);
    }

    /**
     * 使用Jackson进行类型转换
     *
     * @param fromValue 源对象
     * @param toValueType 目标类型
     * @param <T> 目标类型泛型
     * @return 转换结果
     */
    public static <T> T convertValue(Object fromValue, JavaType toValueType) {
        return objectMapper.convertValue(fromValue, toValueType);
    }

    /**
     * 使用Jackson进行类型转换
     *
     * @param fromValue 源对象
     * @param toValueTypeRef 目标类型引用
     * @param <T> 目标类型泛型
     * @return 转换结果
     */
    public static <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) {
        return objectMapper.convertValue(fromValue, toValueTypeRef);
    }

    /**
     * 判断对象是否可以被序列化
     *
     * @param value 待判断的对象
     * @return 是否可以序列化
     */
    public static boolean canSerialize(Object value) {
        if (value == null) {
            return true;
        }
        return objectMapper.canSerialize(value.getClass());
    }

    /**
     * 类型转换
     *
     * @param o 待转换的对象
     * @param valueType 目标类型
     * @param <T> 目标类型泛型
     * @return 转换后的对象
     */
    public static <T> T coverObj(Object o, Class<T> valueType) {
        return coverObj(o, mkJavaType(valueType));
    }


    /**
     * 类型转换
     *
     * @param o 待转换的对象
     * @param javaType 目标类型
     * @param <T> 目标类型泛型
     * @return 转换后的对象
     */
    public static <T> T coverObj(Object o, JavaType javaType) {
        try {
            if (o == null) {
                return null;
            }
            Class valueType = javaType.getRawClass();
            if (javaType.getBindings().size() == 0) {
                if (valueType.isAssignableFrom(o.getClass())) {
                    return (T) o;
                }
                if (valueType.isAssignableFrom(JSONList.class)) {
                    return (T) new JSONList(o);
                }
                if (valueType.isAssignableFrom(JSONMap.class)) {
                    return (T) new JSONMap(o);
                }
            }

            String str;
            if (o instanceof CharSequence) {
                str = o.toString().trim();
            } else {
                str = getJson(o);
            }
            return readValue(str, javaType);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

//    public List<String> t1(){
//    return null;
//    }
//
//    private class T<x>{
//
//    }
//
//    public static void main(String[] args) throws NoSuchMethodException {
//        Method t1 = JacksonUtil.class.getMethod("t1");
//
//        JavaType javaType1 = getJavaType(t1.getGenericReturnType());
////        JavaType javaType=constructType(javaType1);
//        Class valueType = javaType1.getRawClass();
//        javaType1.getRawClass().isAssignableFrom(JSONList.class)
//
//        System.out.println(javaType1.getBindings().size());
//    }

    /**
     * 从对象中按路径提取指定值
     *
     * @param data 数据源，可以是JSON字符串、数组、集合或对象
     * @param key 路径表达式
     * @param javaType 目标类型
     * @param <T> 目标类型泛型
     * @return 提取的值
     */
    public static <T> T at(Object data, String key, JavaType javaType) {
        Object o = at(data, key);
        if (o == null) {
            return null;
        }
        return coverObj(o, javaType);
    }

    /**
     * 从对象中按路径提取指定值
     *
     * @param data 数据源，可以是JSON字符串、数组、集合或对象
     * @param key 路径表达式
     * @param valueType 目标类型
     * @param <T> 目标类型泛型
     * @return 提取的值
     */
    public static <T> T at(Object data, String key, Class<T> valueType) {
        Object o = at(data, key);
        if (o == null) {
            return null;
        }
        return coverObj(o, valueType);
    }

    /**
     * 从对象中使用路径取出需要的值
     *
     * @param data 数据源，可以是JSON字符串、数组、集合或对象
     * @param key 对象路径，支持属性和index
     *            .符号表示属性操作
     *            [i]表示index,i设置为负数表示反方向读取，比如 -1表示倒数第一个
     *            使用例子：{"info":{"a":[[{"b":1},{"c":2}],[{"d":3},{"e":4},{"f":5}]]}}
     *            要取出 c所在对象的属性：info.a[0][1].c
     *            取出f所在对象 :info.a[1][2]
     *            取出f所在对象 :info.a[1][-1]
     * @return 提取的值
     */
    public static Object at(Object data, String key) {
        if (data == null || "".equals(key)) {
            return data;
        }
        if (data instanceof Object[] || data instanceof Collection) {
            if (key.startsWith("[")) {
                return getObjFromList(ValUtil.toList(data), key);
            }
            return null;
        }

        if (key.startsWith(".")) {
            key = key.substring(1);
        }
        return getObjFromMap(ValUtil.toObj(data, JSONMap.class), key);
    }

    /**
     * 从列表中按路径获取对象
     * 
     * @param list 列表
     * @param key 路径表达式
     * @return 获取的对象
     */
    private static Object getObjFromList(List list, String key) {
        int size = list.size();
        int end = key.indexOf(']');
        int index = Integer.parseInt(key.substring(1, end));
        if (index < 0) {
            index += size;
        }
        if (index < 0 || index > size) {
            return null;
        }
        return at(list.get(index), key.substring(end + 1));
    }

    /**
     * 从Map中按路径获取对象
     * 
     * @param para Map对象
     * @param key 路径表达式
     * @return 获取的对象
     */
    private static Object getObjFromMap(Map para, String key) {
        String pName = key;
        if (para.containsKey(pName)) {
            return para.get(pName);
        }
        int index = key.indexOf('.');
        if (index > -1) {
            pName = key.substring(0, index);
            if (para.containsKey(pName)) {
                return at(para.get(pName), key.substring(index));
            }
        }
        index = pName.indexOf('[');
        if (index > -1) {
            pName = key.substring(0, index);
            if (para.containsKey(pName)) {
                return at(para.get(pName), key.substring(index));
            }
        }
        return null;
    }

    /**
     * 构建JavaType对象
     *
     * @param valueType 基础类型
     * @param parameterTypes 参数类型数组
     * @return JavaType对象
     */
    public static JavaType mkJavaTypeByJavaTypes(Class<?> valueType, JavaType... parameterTypes) {
        int len = parameterTypes.length;
        if (len == 0) {
            return objectMapper.getTypeFactory().constructType(valueType);
        }
        return objectMapper.getTypeFactory().constructParametricType(valueType, parameterTypes);
    }

    /**
     * 构建JavaType对象
     *
     * @param valueType 基础类型
     * @param parameterClasses 参数类型数组
     * @return JavaType对象
     */
    public static JavaType mkJavaType(Class<?> valueType, Class<?>... parameterClasses) {
        int len = parameterClasses.length;
        if (len == 0) {
            return objectMapper.getTypeFactory().constructType(valueType);
        }
        return objectMapper.getTypeFactory().constructParametricType(valueType, parameterClasses);
    }

    /**
     * 根据类型构建JavaType对象
     *
     * @param type 类型
     * @return JavaType对象
     */
    public static JavaType mkJavaType(Type type) {
        if (type == null) {
            return null;
        }
        if (type instanceof ParameterizedType) { // 判断获取的类型是否是参数类型
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] typesto = parameterizedType.getActualTypeArguments();// 强制转型为带参数的泛型类型，
            JavaType[] subclass = new JavaType[typesto.length];
            for (int j = 0; j < typesto.length; j++) {
                subclass[j] = mkJavaType(typesto[j]);
            }
            return objectMapper.getTypeFactory().constructParametricType((Class) parameterizedType.getRawType(), subclass);
//        } else if(type instanceof GenericArrayType){
//        } else if(type instanceof TypeVariable){
//        } else if(type instanceof WildcardType) {
        } else {
//            return TypeFactory.defaultInstance().constructParametricType((Class) type, new JavaType[0]);
            return objectMapper.getTypeFactory().constructType(type);
        }
    }

    private static Pattern JsonObjPattern = Pattern.compile("^\\{(([\"\\w]+:.+)||)\\}$");
    private static Pattern JsonArrayPattern = Pattern.compile("^\\[[^\\[^\\]]*\\]$");

    /**
     * 判断字符串是否为JSON对象格式
     * 
     * @param str 待检测的字符串
     * @return 如果是JSON对象格式返回true，否则返回false
     */
    public static boolean isJsonObj(String str) {
        return JsonObjPattern.matcher(str.replaceAll("\\s", "")).matches();
    }

    /**
     * 判断字符串是否为JSON数组格式
     * 
     * @param str 待检测的字符串
     * @return 如果是JSON数组格式返回true，否则返回false
     */
    public static boolean isJsonArray(String str) {
        return JsonArrayPattern.matcher(str.replaceAll("\\s", "")).find();
    }
//    public static void main(String[] args) {
//        System.out.println(isJsonObj(" { } "));
////        System.out.println(isJsonObj("{ \"xx\" : 123 } "));
////        System.out.println(isJsonObj("{ \"xx\"}"));
////        System.out.println(isJsonObj(" { \"xx\"}"));
////        System.out.println(isJsonArray("[]"));
////        System.out.println(isJsonArray(" [ ]"));
////        System.out.println(isJsonArray(" [ xxx ]"));
////        System.out.println(isJsonArray(" [ xxx ] "));
////        System.out.println(isJsonArray(" [ xxx "));
////        System.out.println(new JSONList("[]"));
//    }
}