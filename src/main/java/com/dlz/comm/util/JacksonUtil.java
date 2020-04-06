package com.dlz.comm.util;

import com.dlz.comm.json.JSONList;
import com.dlz.comm.json.JSONMap;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext.Impl;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.*;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 2013 2013-9-13 下午4:54:15
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
public class JacksonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private final static Class<?> CLASS_OBJECT = Object.class;

    static {
        //添加自定义解析器，将默认的linckedHashMap 和List对应修改为 JSONMap和JSONList
        Deserializers deserializers = new Deserializers() {
            @Override
            public JsonDeserializer<?> findTreeNodeDeserializer(Class<? extends JsonNode> nodeType, DeserializationConfig config, BeanDescription beanDesc)
                    throws JsonMappingException {
                return null;
            }

            @Override
            public JsonDeserializer<?> findReferenceDeserializer(ReferenceType refType, DeserializationConfig config, BeanDescription beanDesc,
                                                                 TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer) throws JsonMappingException {
                return null;
            }

            @Override
            public JsonDeserializer<?> findMapLikeDeserializer(MapLikeType type, DeserializationConfig config, BeanDescription beanDesc,
                                                               KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
                return null;
            }

            @Override
            public JsonDeserializer<?> findMapDeserializer(MapType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer,
                                                           TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
                return null;
            }

            @Override
            public JsonDeserializer<?> findEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
                return null;
            }

            @Override
            public JsonDeserializer<?> findCollectionLikeDeserializer(CollectionLikeType type, DeserializationConfig config, BeanDescription beanDesc,
                                                                      TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
                return null;
            }

            @Override
            public JsonDeserializer<?> findCollectionDeserializer(CollectionType type, DeserializationConfig config, BeanDescription beanDesc,
                                                                  TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
                return null;
            }

            @Override
            public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
                Class<?> rawType = type.getRawClass();
                if (rawType == CLASS_OBJECT) {
                    //添加自定义解析器，将默认的linckedHashMap 和List对应修改为 JSONMap和JSONList
                    return new JacksonObjectDeserializer();
                }
                return null;
            }

            @Override
            public JsonDeserializer<?> findArrayDeserializer(ArrayType type, DeserializationConfig config, BeanDescription beanDesc,
                                                             TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
                return null;
            }
        };
        final DeserializerFactoryConfig config = new DeserializerFactoryConfig().withAdditionalDeserializers(deserializers);
        final DefaultDeserializationContext dc = new Impl(new BeanDeserializerFactory(config));
        objectMapper = new ObjectMapper(null, null, dc);
        // https://github.com/FasterXML/jackson-databind
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setSerializationInclusion(Include.NON_NULL);

        /**
         * 配置默认的日期转换格式 ，参考http://wiki.fasterxml.com/JacksonFAQDateHandling
         */
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    public static String getJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON转换异常" + e.getMessage(), e);
        }
    }

    public static <T> T readValue(String content, Class<T> valueType) {
        return readValue(content, constructType(valueType));
    }

    public static <T> T readValue(String content, JavaType valueType) {
        try {
            return objectMapper.readValue(content, valueType);
        } catch (Exception e) {
            log.error("JacksonUtil.readValue error:valueType={} content={}", valueType, content);
            log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public static <T> List<T> readListValue(String content, Class<T> valueType) {
        return readValue(content, constructType(List.class, valueType));
    }

    /**
     * 类型转换
     *
     * @param o
     * @param valueType
     * @return
     */
    public static <T> T coverObj(Object o, Class<T> valueType) {
        return coverObj(o, constructType(valueType));
    }
	/**
	 * 类型转换
	 *
	 * @param valueType
	 * @param parameterClasses
	 * @return
	 */
	public static JavaType constructType(Class<?> valueType,Class<?>... parameterClasses) {
		int len = parameterClasses.length;
		if(len==0){
			return objectMapper.getTypeFactory().constructType(valueType);
		}
		return objectMapper.getTypeFactory().constructParametricType(valueType, parameterClasses);
	}
    /**
     * 类型转换
     *
     * @param o
     * @param javaType
     * @return
     */
    public static <T> T coverObj(Object o, JavaType javaType) {
        try {
            if (o == null) {
                return null;
            }
            Class valueType = javaType.getRawClass();
            if (valueType.isAssignableFrom(o.getClass())) {
                return (T) o;
            }
            if (valueType.isAssignableFrom(JSONList.class)) {
                return (T) new JSONList(o);
            }
            if (valueType.isAssignableFrom(JSONMap.class)) {
                return (T) new JSONMap(o);
            }

            String str = null;
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

    /**
     * 对象取值
     *
     * @param data
     * @param key
     * @param javaType
     * @return
     */
    public static <T> T at(Object data, String key, JavaType javaType) {
        Object o = at(data, key);
        if (o == null) {
            return null;
        }
        return coverObj(o, javaType);
    }

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
     * @param data 可以是json字符串，数组，集合或者对象
     * @param key  对象路径，支持属性和index
     *             .符号表示属性操作
     *             [i]表示index,i设置为负数表示反方向读取，比如 -1表示倒数第一个
     *             使用例子：{"info":{"a":[[{"b":1},{"c":2}],[{"d":3},{"e":4},{"f":5}]]}}
     *             要取出  c所在对象的属性：info.a[0][1].b
     *             取出f所在对象 :info.a[1][2]
     *             取出f所在对象 :info.a[1][-1]
     * @return
     */
    public static Object at(Object data, String key) {
        if (data == null || "".equals(key)) {
            return data;
        }
        if (data instanceof Object[] || data instanceof Collection) {
            if (key.startsWith("[")) {
                return getObjFromList(ValUtil.getList(data), key);
            }
            return null;
        }

        if (key.startsWith(".")) {
            key = key.substring(1);
        }
        return getObjFromMap(ValUtil.getObj(data, Map.class), key);
    }

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

}
