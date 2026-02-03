package com.dlz.comm.util.system;

import com.dlz.comm.cache.CacheMap;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Function;

@Slf4j
public class MethodReflections {
    private static final String SETTER_PREFIX = "set";
    private static final String GETTER_PREFIX = "get";
    private static final String IS_PREFIX = "is";

    private static CacheMap<Class<?>, CacheMap<String, Field>> fieldCache = new CacheMap<>();
    private static CacheMap<Function, Field> fnCache = new CacheMap<>();

    /**
     * 从method名中取得属性名称,属性名称首字母小写
     * getX1 -> x1
     * setX1 -> x1
     * isX1 -> x1
     * X1 -> x1
     * x1 -> x1
     *
     * @param methodName
          */
    private static String getFieldName(String methodName) {
        int start = methodName.startsWith(GETTER_PREFIX) ? 3 : methodName.startsWith(IS_PREFIX) ? 2 : methodName.startsWith(SETTER_PREFIX) ? 3 : 0;
        return methodName.substring(start, 1).toLowerCase() + methodName.substring(start + 1);
    }

    /**
     * 构建方法名
     *
     * @param propertyName
     * @param methodPrefix 方法前缀 get set is
          */
    public static String mkMethodName(String propertyName, String methodPrefix) {
        return (methodPrefix + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1)).intern();
    }

    /**
     * 调用Getter方法.
     */
    public static Object invokeGetter(Object obj, String propertyName) {
        return invokeMethod(obj, mkMethodName(propertyName, GETTER_PREFIX));
    }

    /**
     * 调用Setter方法, 仅匹配方法名。
     */
    public static void invokeSetter(Object obj, String propertyName, Object value) {
        invokeMethod(obj, mkMethodName(propertyName, SETTER_PREFIX), value);
    }

    /**
     * 调用is方法, 仅匹配方法名。
     */
    public static Object invokeIs(Object obj, String propertyName, Object value) {
        return invokeMethod(obj, mkMethodName(propertyName, IS_PREFIX), value);
    }

    /**
     * 直接调用对象方法, 无视private/protected修饰符，
     * 用于一次性调用的情况，否则应使用getAccessibleMethodByName()函数获得Method后反复调用.
     * 只匹配函数名，如果有多个同名函数调用第一个。
     */
    public static Object invokeMethod(final Object obj, final String methodName, final Object... args) {
        Method method = getAccessibleMethod(obj.getClass(), methodName, com.dlz.comm.util.system.Reflections.mkParameterTypes(args));
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        }
        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw com.dlz.comm.util.system.Reflections.convertReflectionExceptionToUnchecked(e);
        }
    }


    /**
     * 直接调用对象方法, 无视private/protected修饰符，
     * 用于一次性调用的情况，否则应使用getAccessibleMethodByName()函数获得Method后反复调用.
     * 只匹配函数名，如果有多个同名函数调用第一个。
     */
    public static Object invokeMethod(final Object obj, final Method method, final Object... args) {
        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw com.dlz.comm.util.system.Reflections.convertReflectionExceptionToUnchecked(e);
        }
    }
    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     * 只匹配函数名。
     * <p>
     * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
     */
    public static Method getAccessibleMethod(final Class obj, final String methodName, Class<?>... parameterTypes) {
        Class<?> targetClass = Reflections.getTargetClass(obj);
        for (Class<?> searchType = targetClass; searchType != Object.class; searchType = searchType.getSuperclass()) {
            Method[] methods = searchType.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName) && arrayContentsEq(parameterTypes, method.getParameterTypes())) {
                    makeAccessible(method);
                    return method;
                }
            }
        }
        log.error("NoSuchMethod:" + targetClass + "." + methodName+"("+parameterTypes+")");
        return null;
    }

    private static boolean arrayContentsEq(Class[] input, Class[] fn) {
        if (input.length == 0) {
            return fn.length == 0;
        }
        if (fn.length == 0) {
            return input.length == 0;
        }
        if (input.length != fn.length) {
            return false;
        }
        for (int i = 0; i < input.length; i++) {
            //如果输入参数为null，则不比较
            //判断类型是否相同
            //判断类型是否兼容
            if (input[i]!=null && input[i] != fn[i] && !fn[i].isAssignableFrom(input[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 改变private/protected的方法为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
     */
    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
                && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }
}