package com.dlz.comm.util.system;

import com.dlz.comm.cache.CacheMap;
import com.dlz.comm.exception.SystemException;
import com.dlz.comm.fn.DlzFn;
import com.dlz.comm.util.ExceptionUtils;
import com.dlz.comm.util.VAL;
import com.dlz.comm.util.ValUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class FieldReflections {
    private static CacheMap<Class<?>, Map<String, Field>> classFieldCache =new CacheMap<>();
    private static CacheMap<DlzFn,VAL<Class<?>,Field>> fnFieldCache = new CacheMap<>();
    /**
     * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
     */
    public static <T> T getValue(final Object obj, final Field field) {
        if (field == null) {
            throw new IllegalArgumentException("field is null");
        }
        makeAccessible(field);
        Object result = null;
        try {
            result = field.get(obj);
        } catch (IllegalAccessException e) {
            log.error("不可能抛出的异常{}", e.getMessage());
        }
        return (T) result;
    }
    /**
     * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
     */
    public static <T> T getValue(final Object obj, final String fieldName, final boolean ignore) {
        if (obj == null) {
            if(ignore){
                return null;
            }
            throw new IllegalArgumentException("Could not getValue [" + fieldName + "] on target [null]");
        }
        final Field field = getField(obj, fieldName, ignore);
        if(field==null && ignore){
            return null;
        }
        return getValue(obj, field);
    }

    /**
     * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
          */
    public static boolean setValue(final Object obj, final String fieldName, final Object value, final boolean ignore) {
        if (obj == null) {
            if(ignore){
                return false;
            }
            throw new IllegalArgumentException("Could not setValue [" + fieldName + "] on target [null]");
        }
        return setValue(obj, getField(obj, fieldName,ignore), value);
    }

    /**
     * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
     */
    public static boolean setValue(final Object obj, final String fieldName, final Object value) {
        return setValue(obj, fieldName, value, false);
    }

    /**
     * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
     */
    public static boolean setValue(final Object obj, final Field field, final Object value) {
        if (field == null) {
            throw new IllegalArgumentException("field is null");
        }
        try {
            Type genericType = field.getGenericType();
            if(genericType instanceof TypeVariable){
                genericType = Reflections.getActualType(obj.getClass(), (TypeVariable) genericType);
            }
            field.set(obj, ValUtil.toObj(value, genericType));
        } catch (IllegalAccessException e) {
            log.error("不可能抛出的异常:{}", e.getMessage());
            return false;
        }
        return true;
    }


    /**
     * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
     * <p>
     * 如向上转型到Object仍无法找到, 返回null.
     */
    public static Field getField(final Object obj, final String fieldName, final boolean ignore) {
        return getField(obj.getClass(),fieldName,ignore);
    }
    /**
     * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
     * <p>
     * 如向上转型到Object仍无法找到, 返回null.
     */
    public static Field getField(final Class<?> clazz, final String fieldName, final boolean ignore) {
        Field field = getFieldsMap(clazz).get(fieldName);
        if (field == null) {
            if(ignore){
                log.info("NoSuchField and ignore:" + clazz + "." + fieldName);
                return null;
            }
            throw new SystemException("NoSuchField:" + clazz + "." + fieldName);
        }
        return field;
    }

    public static Map<String, Field> getFieldsMap(Class<?> beanClass) {
        return classFieldCache.getAndSet(beanClass, () -> {
            Map<String, Field> caheMap = new LinkedHashMap<>();
            Class<?> searchType = beanClass;
            Field[] declaredFields;
            while (searchType != null && searchType != Object.class) {
                declaredFields = searchType.getDeclaredFields();
                Arrays.stream(declaredFields).forEach(field -> {
                    String fieldName = field.getName();
                    if(!Modifier.isStatic(field.getModifiers()) && !caheMap.containsKey(fieldName)){
                        caheMap.put(fieldName, field);
                        makeAccessible(field);
                    }
                });
                searchType = searchType.getSuperclass();
            }
            return caheMap;
        });
    }

    public static List<Field> getFields(Class<?> beanClass) {
        return getFieldsMap(beanClass).values().stream().collect(Collectors.toList());
    }

    /**
     * uncapitalize a String, changing the first letter to lowercase
     *
     * <p>For a blank string, returns an empty string.
     *
     * <pre>
     * uncapitalize(null)  = null
     * uncapitalize("")    = ""
     * uncapitalize("Cat") = "cat"
     * uncapitalize("cat") = "cat"
     * </pre>
     *
     * @param name the String to uncapitalize, may be null
     */
    public static String decapitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.equals(name.toUpperCase(Locale.ROOT))){
            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    public static <T> VAL<Class<?>,Field> getFn(DlzFn<T, ?> function) {
        return fnFieldCache.getAndSet(function, () -> {
            String fieldName = null;
            try {
                // 第1步 获取SerializedLambda
                Method method = function.getClass().getDeclaredMethod("writeReplace");
                method.setAccessible(Boolean.TRUE);
                SerializedLambda serializedLambda = (SerializedLambda) method.invoke(function);
                // 第2步 implMethodName 即为Field对应的Getter方法名
                String implMethodName = serializedLambda.getImplMethodName();
                if (implMethodName.startsWith("get") && implMethodName.length() > 3) {
                    fieldName = decapitalize(implMethodName.substring(3));
                } else if (implMethodName.startsWith("is") && implMethodName.length() > 2) {
                    fieldName = decapitalize(implMethodName.substring(2));
                } else if (implMethodName.startsWith("lambda$")) {
                    throw new IllegalArgumentException("SerializableFunction不能传递lambda表达式,只能使用方法引用");
                } else {
                    throw new IllegalArgumentException(implMethodName + "不是Getter方法引用");
                }
                // 第3步 获取的Class是字符串，并且包名是“/”分割，需要替换成“.”，才能获取到对应的Class对象
//            String declaredClass = serializedLambda.getImplClass().replace("/", ".");
//                Class<?> aClass = Class.forName(declaredClass, false, ClassUtils.getDefaultClassLoader());
                String declaredClass = serializedLambda.getInstantiatedMethodType().replaceAll("\\(L(.*);\\).*", "$1").replace("/", ".");
                Class<?> aClass = Class.forName(declaredClass);

                // 第4步 Spring 中的反射工具类获取Class中定义的Field
                return VAL.of(aClass, getField(aClass, fieldName, false));
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
            throw new NoSuchFieldError(fieldName);
        });
    }
    /**
     * 改变private/protected的成员变量为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
     */
    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier
                .isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }
}