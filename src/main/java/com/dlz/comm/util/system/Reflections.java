package com.dlz.comm.util.system;

import com.dlz.comm.exception.SystemException;
import com.dlz.comm.util.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.*;

/**
 * 反射工具类
 * 
 * 提供调用getter/setter方法、访问私有变量、调用私有方法、获取泛型类型Class、被AOP过的真实类等工具函数
 *
 * @author calvin
 * @since 2023
 */
@Slf4j
public class Reflections {
    /**
     * CGLIB类分隔符
     */
    private static final String CGLIB_CLASS_SEPARATOR = "$$";

    /**
     * 复制对象属性
     * 
     * 将源对象的属性值复制到目标对象
     * 
     * @param source 源对象
     * @param target 目标对象
     * @param <T> 对象类型泛型
     */
    public static <T> void copy(T source, T target) {
        if (source == null) {
            throw new SystemException("source不能为空");
        }
        if (target == null) {
            throw new SystemException("target不能为空");
        }
        FieldReflections.getFields(source.getClass())
                .forEach(field -> FieldReflections.setValue(target, field, FieldReflections.getValue(source, field)));
    }

    /**
     * 获取泛型参数类型
     * 
     * 示例：
     * Map&lt;String, HashMap&lt;Integer,Double&gt;&gt; test = new HashMap&lt;String, HashMap&lt;Integer,Double&gt;&gt;(){};
     * getActualType(test.getClass(),0);  -&gt;class java.lang.String
     * getActualType(test.getClass(),1);	-&gt;java.util.HashMap&lt;java.lang.Integer, java.lang.Double&gt;
     * getActualType(test.getClass(),1,0);	-&gt;class java.lang.Integer
     * getActualType(test.getClass(),1,1);	-&gt;class java.lang.Double
     *
     * @param type 需要判断的类型
     * @param typeVariable 泛型类型变量
     * @return 泛型参数的实际类型
     */
    public static Type getActualType(Type type, TypeVariable typeVariable) {
        //取得泛型参数
        ParameterizedType genericSuperclass;
        if (type instanceof Class) {
            final Class aClass = (Class) type;
            Type genericSuper = aClass.getGenericSuperclass();
            if (genericSuper instanceof ParameterizedType) {
                genericSuperclass = (ParameterizedType) genericSuper;
            } else {
                final Class superclass = aClass.getSuperclass();
                if (superclass == Object.class) {
                    throw new SystemException(type + "无泛型参数");
                }
                return getActualType(superclass, typeVariable);
            }
        } else if (type instanceof ParameterizedType) {
            genericSuperclass = (ParameterizedType) type;
        } else {
            throw new SystemException(type + "未识别泛型参数");
        }

        final Type[] actualTypeArguments = genericSuperclass.getActualTypeArguments();
        final TypeVariable<?>[] typeParameters = ((GenericDeclaration) genericSuperclass.getRawType()).getTypeParameters();
        for (int i = 0; i < actualTypeArguments.length; i++) {
            if (typeVariable == typeParameters[i]) {
                return actualTypeArguments[i];
            }
        }
        return typeVariable;
    }


    /**
     * 获取泛型参数类型
     * 
     * 示例：
     * Map&lt;String, HashMap&lt;Integer,Double&gt;&gt; test = new HashMap&lt;String, HashMap&lt;Integer,Double&gt;gt;(){};
     * getActualType(test.getClass(),0);  -&gt;class java.lang.String
     * getActualType(test.getClass(),1);	-&gt;java.util.HashMap&lt;java.lang.Integer, java.lang.Double&gt;
     * getActualType(test.getClass(),1,0);	-&gt;class java.lang.Integer
     * getActualType(test.getClass(),1,1);	-&gt;class java.lang.Double
     *
     * @param type 需要判断的类型
     * @param indexs 泛型序号，有多级时，传递多个参数
     * @return 泛型参数的实际类型
     */
    public static Type getActualType(Type type, int... indexs) {
        int length = indexs.length;

        //无序号，默认取得第一个泛型参数或者本身
        if (length == 0) {
            if (type instanceof Class) {
                return type;
            } else if (type instanceof ParameterizedType) {
                return ((ParameterizedType) type).getActualTypeArguments()[0];
            }
        }

        //取得泛型参数
        ParameterizedType genericSuperclass;
        if (type instanceof Class) {
            Type genericSuper = ((Class) type).getGenericSuperclass();
            if (genericSuper instanceof ParameterizedType) {
                genericSuperclass = (ParameterizedType) genericSuper;
            } else {
                throw new SystemException(type + "无泛型参数");
            }
        } else if (type instanceof ParameterizedType) {
            genericSuperclass = (ParameterizedType) type;
        } else {
            throw new SystemException(type + "未识别泛型参数");
        }

        Type actualType = genericSuperclass.getActualTypeArguments()[indexs[0]];
        if (length == 1) {
            return actualType;
        }
        int[] subIndex = new int[length - 1];
        System.arraycopy(indexs, 1, subIndex, 0, length - 1);
        return getActualType(actualType, subIndex);
    }

    /**
     * 通过反射, 获得Class定义中声明的泛型参数的类型, 注意泛型必须定义在父类处
     * 如无法找到, 返回Object.class.
     * eg.
     * public UserDao extends HibernateDao&lt;User&gt;
     *
     * @param clazz 需要检查的类
     * @param <T> 泛型类型
     * @return 第一个泛型声明, 或者如果无法确定则返回 Object.class
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClassGenricType(final Class clazz) {
        return getClassGenricType(clazz, 0);
    }

    /**
     * 通过反射, 获得Class定义中声明的父类的泛型参数的类型.
     * 如无法找到, 返回Object.class.
     * <p>
     * 如public UserDao extends HibernateDao&lt;User,Long&gt;
     *
     * @param clazz 需要检查的类
     * @param index 泛型声明的索引，从0开始
     * @return 指定索引的泛型声明, 或者如果无法确定则返回 Object.class
     */
    public static Class getClassGenricType(final Class clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            log.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            log.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
                    + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            log.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }
        return (Class) params[index];
    }

    /**
     * 获取目标类（处理CGLIB代理类）
     * 
     * 如果类是CGLIB代理类，则返回其父类；否则返回自身
     * 
     * @param clazz 类
     * @return 目标类
     */
    public static Class<?> getTargetClass(Class clazz) {
        if (clazz != null && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && !Object.class.equals(superClass)) {
                return superClass;
            }
        }
        return clazz;
    }
    
    /**
     * 获取目标类（处理CGLIB代理类）
     * 
     * 如果对象是CGLIB代理，则返回其父类；否则返回对象的实际类型
     * 
     * @param instance 实例对象
     * @return 目标类
     */
    public static Class<?> getTargetClass(Object instance) {
        return getTargetClass(instance.getClass());
    }

    /**
     * 创建类的新实例
     * 
     * @param clazz 类
     * @param <T> 类型泛型
     * @return 类的新实例
     */
    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }

    /**
     * 创建类的新实例（带参数）
     * 
     * @param clazz 类
     * @param parameterTypes 参数类型数组
     * @param args 参数值数组
     * @param <T> 类型泛型
     * @return 类的新实例
     */
    public static <T> T newInstance(Class<T> clazz, final Class<?>[] parameterTypes, final Object[] args) {
        try {
            return clazz.getConstructor(parameterTypes).newInstance(args);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw convertReflectionExceptionToUnchecked(e);
        }
    }

    /**
     * 创建参数类型数组
     * 
     * @param para 参数对象数组
     * @return 参数类型数组
     */
    public static Class<?>[] mkParameterTypes(Object... para) {
        Class<?>[] aClass = new Class[para.length];
        for (int i = 0; i < para.length; i++) {
            aClass[i] = para[i] == null ? null : getTargetClass(para[i].getClass());
        }
        return aClass;
    }

    /**
     * 创建类的新实例（可变参数）
     * 
     * @param classz 类
     * @param para 参数
     * @param <T> 类型泛型
     * @return 类的新实例
     */
    public static <T> T newInstance(Class<T> classz, Object... para) {
        return newInstance(classz, mkParameterTypes(para), para);
    }
    
    /**
     * 将反射时的checked exception转换为unchecked exception
     * 
     * @param e 异常
     * @return 运行时异常
     */
    public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
        if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
                || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException(e);
        } else if (e instanceof InvocationTargetException) {
            return new RuntimeException(((InvocationTargetException) e).getTargetException());
        } else if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new RuntimeException("Unexpected Checked Exception.", e);
    }

}