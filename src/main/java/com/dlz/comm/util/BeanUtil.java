package com.dlz.comm.util;

import com.dlz.comm.json.JSONList;
import com.dlz.comm.json.JSONMap;
import com.dlz.comm.util.system.FieldReflections;
import com.dlz.comm.util.system.annotation.SetValue;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 对象转换工具类
 *
 * @author dk 2017-11-03
 */
@Slf4j
public class BeanUtil {

    /**
     * 获取对象属性值,无视private/protected修饰符, 不经过getter函数，支持多级取值
     *
     * @param obj       支持pojo对象,map,数组和list
     * @param fieldName 支持多级取值
     *                  如： {a:1}  取值1： fieldNames="a"
     *                  {a:{b:"xx"}}  取值xx： fieldNames="a.b"
     *                  {a:{b:[1,2]}}  取值2： fieldNames="a.b.1"
     * @param ignore    忽略空值或错误的属性
     * @param <T>
     */
    public static <T> T getValue(final Object obj, final String fieldName, final boolean ignore) {
        Object object = obj;
        Object res = null;
        final int i = fieldName.indexOf(".");
        if (i > -1) {
            String name = fieldName.substring(0, i);
            String subFieldName = fieldName.substring(i + 1);
            return getValue(getValue(obj, name, ignore), subFieldName, ignore);
        }

        if (object instanceof CharSequence) {
            if (StringUtils.isNumber(fieldName)) {
                res = new JSONList(object.toString()).get(Integer.parseInt(fieldName));
            } else {
                res = new JSONMap(object.toString()).get(fieldName);
            }
        } else if (object instanceof Map) {
            res = ((Map) object).get(fieldName);
        } else if (object instanceof List || object.getClass().isArray()) {
            if (!StringUtils.isLongOrInt(fieldName)) {
                if (ignore) {
                    return null;
                }
                throw new IllegalArgumentException("can't getValue [" + fieldName + "] from [" + obj + "]");
            }
            final int i1 = Integer.parseInt(fieldName);
            if (object instanceof List) {
                res = ((List) object).get(i1);
            } else if (object.getClass().isArray()) {
                res = ((Object[]) object)[i1];
            }
        } else {
            res = FieldReflections.getValue(object, fieldName, ignore);
        }
        return (T) res;
    }

    /**
     * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.支持多级属性
     *
     * @param obj       支持pojo对象,map,数组和list
     * @param fieldName 支持多级属性
     * @param value     属性值
     * @param ignore    忽略空值或错误的属性
     */
    public static Object setValue(final Object obj, final String fieldName, final Object value, final boolean ignore) {
        if (obj == null) {
            if (ignore) {
                return obj;
            }
            throw new IllegalArgumentException("can't setValue [" + fieldName + "] from [" + obj + "]");
        }
        Object object = obj;
        final int i = fieldName.indexOf(".");
        if (i > -1) {
            //多级处理
            String name = fieldName.substring(0, i);
            String subFieldName = fieldName.substring(i + 1);
            Object subObject = setValue(getValue(obj, name, true), subFieldName, value, ignore);
            return setValue(obj, name, subObject, true);
        }

        //一级处理
        if (obj instanceof CharSequence) {
            if (StringUtils.isNumber(fieldName)) {
                final int index = Integer.parseInt(fieldName);
                JSONList list = new JSONList(object.toString());
                if (list.size() <= index) {
                    if (ignore) {
                        return obj;
                    }
                    throw new IllegalArgumentException("can't setValue [" + fieldName + "] from [" + obj + "]");
                }
                list.set(index, value);
                return list.toString();
            } else {
                JSONMap map = new JSONMap(object.toString());
                map.put(fieldName, value);
                return map.toString();
            }
        } else if (object instanceof Map) {
            ((Map) object).put(fieldName, value);
        } else if (object instanceof List || object.getClass().isArray()) {
            if (!StringUtils.isLongOrInt(fieldName)) {
                if (ignore) {
                    return obj;
                }
                throw new IllegalArgumentException("can't setValue [" + fieldName + "] from [" + obj + "]");
            }
            final int i1 = Integer.parseInt(fieldName);
            if (object instanceof List) {
                final List list = (List) object;
                if (list.size() <= i1) {
                    if (ignore) {
                        return obj;
                    }
                    throw new IllegalArgumentException("can't setValue [" + fieldName + "] from [" + obj + "]");
                }
                list.set(i1, value);
            } else if (object.getClass().isArray()) {
                final Object[] array = (Object[]) object;
                if (array.length <= i1) {
                    if (ignore) {
                        return obj;
                    }
                    throw new IllegalArgumentException("can't setValue [" + fieldName + "] from [" + obj + "]");
                }
                array[i1] = value;
            }
        } else {
            FieldReflections.setValue(object, fieldName, value, ignore);
        }
        return object;
    }

    public static void join(Object source, Map target) {
        target.putAll(new JSONMap(source));
    }

    public static void join(Object source, List target) {
        if (source instanceof Collection) {
            target.addAll((Collection) source);
        }else if (source.getClass().isArray()) {
            final Object[] objects = (Object[]) source;
            for (Object object : objects) {
                target.add(object);
            }
        }else if (target instanceof CharSequence) {
            target.addAll(ValUtil.toList(source));
        }
    }

    /**
     * 对象拷贝 将 源数据对象拷贝成目标数据，根据目标数据的属性进行赋值
     * @param source 源数据，支持pojo对象,map,数组和list
     * @param target 支持pojo对象
     * @param onlySetValue 是否只复制注解了@SetValue的属性
     */
    public static <T> T copyAsTarget(Object source, T target, boolean onlySetValue) {
        if (target instanceof CharSequence || target instanceof Map || target instanceof List || target.getClass().isArray()) {
            throw new IllegalArgumentException("不支持的复制类型：" + target.getClass());
        }
        FieldReflections.getFields(target.getClass()).parallelStream().forEach(method -> {
            SetValue annotation = method.getAnnotation(SetValue.class);
            if (annotation == null && onlySetValue) {
                return;
            }

            String name = method.getName();
            String sourceName = name;
            if (annotation != null && !"".equals(annotation.value())) {
                sourceName = annotation.value() + "." + name;
            }

            Object o = ValUtil.toObj(getValue(source, sourceName, true), method.getType());
            setValue(target, name, o, true);
        });
        return target;
    }
    /**
     * 对象拷贝 将 pojo对象转换成多级的map
     * @param source pojo对象，支持 SetValue 注解，复制到目标生成多级属性
     * @param target 目标数据，支持JSONMap
     * @param onlySetValue 是否只复制注解了@SetValue的属性
     */
    public static void copyAsSource(Object source, JSONMap target, boolean onlySetValue) {
        if (source instanceof CharSequence || source instanceof Map || source instanceof List || source.getClass().isArray()) {
            throw new IllegalArgumentException("不支持的复制类型：" + source.getClass());
        }
        List<Field> fields = FieldReflections.getFields(source.getClass());
        fields.forEach(method ->{
            String name = method.getName();
            Object o = getValue(source, name, true);

            SetValue annotation = method.getAnnotation(SetValue.class);
            String annotationValue = annotation==null?"":annotation.value();;
            if(annotation==null && onlySetValue){
                return;
            }
            if(!"".equals(annotationValue)){
                target.set(annotationValue+"."+name,o);
            }else{
                target.set(name,o);
            }
        });
    }
//    public static void main(String[] args) {
//        System.out.println(toDate("2018-21-24 19:23:39.583"));
//        System.out.println(toDate("2018-1-24 19:23"));
//        System.out.println(toDate("19:23"));
//        System.out.println(toDate("2018-1-24"));
//    }
}