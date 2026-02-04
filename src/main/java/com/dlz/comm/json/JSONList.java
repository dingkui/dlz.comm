package com.dlz.comm.json;

import com.dlz.comm.util.JacksonUtil;
import com.dlz.comm.util.ValUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JSON列表类
 * 继承ArrayList<Object>，实现了IUniversalVals和IUniversalVals4List接口，提供便捷的JSON数组操作功能
 *
 * @author dk 2017-09-05
 */
@Slf4j
public class JSONList extends ArrayList<Object> implements IUniversalVals, IUniversalVals4List {
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 7554800764909179290L;

    /**
     * 构造函数，指定初始容量
     *
     * @param initialCapacity 初始容量
     */
    public JSONList(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * 无参构造函数
     */
    public JSONList() {
        super();
    }

    /**
     * 使用对象构造JSONList
     *
     * @param obj 源对象
     */
    public JSONList(Object obj) {
        this(obj, null);
    }

    /**
     * 使用集合构造JSONList
     *
     * @param collection 源集合
     */
    public JSONList(Collection<?> collection) {
        super();
        if(collection == null) {
            return;
        }
        addAll(collection);
    }

    /**
     * 使用数组构造JSONList
     *
     * @param objs 源数组
     */
    public JSONList(Object[] objs) {
        super();
        if(objs == null) {
            return;
        }
        Collections.addAll(this, objs);
    }

    /**
     * 使用集合和目标类型构造JSONList
     *
     * @param collection 源集合
     * @param objectClass 目标类型
     * @param <T> 目标类型泛型
     */
    public <T> JSONList(Collection<?> collection, Class<T> objectClass) {
        super();
        if(collection == null) {
            return;
        }
        if(objectClass != null) {
            final Iterator input2 = collection.iterator();
            while(input2.hasNext()) {
                add(ValUtil.toObj(input2.next(), objectClass));
            }
        } else {
            addAll(collection);
        }
    }

    /**
     * 使用数组和目标类型构造JSONList
     *
     * @param objs 源数组
     * @param objectClass 目标类型
     * @param <T> 目标类型泛型
     */
    public <T> JSONList(Object[] objs, Class<T> objectClass) {
        super();
        if(objs == null) {
            return;
        }
        if(objectClass != null) {
            for (int i = 0; i < objs.length; i++) {
                add(ValUtil.toObj(objs[i], objectClass));
            }
        } else {
            Collections.addAll(this, objs);
        }
    }

    /**
     * 使用对象和目标类型构造JSONList
     *
     * @param obj 源对象
     * @param objectClass 目标类型
     * @param <T> 目标类型泛型
     */
    @SuppressWarnings({ "rawtypes" })
    public <T> JSONList(Object obj, Class<T> objectClass) {
        super();
        if(obj == null) {
            return;
        }
        if(obj instanceof Collection) {
            if(objectClass != null) {
                for (Object next : (Collection) obj) {
                    if (objectClass.isAssignableFrom(next.getClass())) {
                        add(next);
                    } else {
                        add(ValUtil.toObj(next, objectClass));
                    }
                }
            } else {
                addAll((Collection) obj);
            }
        } else if(obj instanceof Object[]) {
            if(objectClass != null) {
                final Object[] input2 = (Object[]) obj;
                for (Object o : input2) {
                    if (objectClass.isAssignableFrom(o.getClass())) {
                        add(o);
                    } else {
                        add(ValUtil.toObj(o, objectClass));
                    }
                }
            } else {
                Collections.addAll(this, (Object[]) obj);
            }
        } else {
            String str = null;
            if(obj instanceof CharSequence) {
                str = obj.toString().trim();
            } else {
                str = JacksonUtil.getJson(obj);
            }
            if(str == null) {
                return;
            }
            if(!JacksonUtil.isJsonArray(str)) {
                if(objectClass == null) {
                    if(JacksonUtil.isJsonObj(str)|| !str.contains(",")){
                        adds(obj);
                        return;
                    }
                    Arrays.stream(str.split(",")).forEach(item -> this.add(item.trim()));
                    return;
                } else if(objectClass == String.class) {
                    if(JacksonUtil.isJsonObj(str)){
                        adds(str);
                        return;
                    }
                    Arrays.stream(str.split(",")).forEach(item -> this.add(item.trim()));
                    return;
                } else if(objectClass == Integer.class) {
                    Arrays.stream(str.split(",")).forEach(item -> this.add(ValUtil.toInt(item.trim())));
                    return;
                } else if(objectClass == Long.class) {
                    Arrays.stream(str.split(",")).forEach(item -> this.add(ValUtil.toLong(item.trim())));
                    return;
                } else if(objectClass == Date.class) {
                    Arrays.stream(str.split(",")).forEach(item -> this.add(ValUtil.toDate(item.trim())));
                    return;
                } else if(objectClass == BigDecimal.class) {
                    Arrays.stream(str.split(",")).forEach(item -> this.add(ValUtil.toBigDecimal(item.trim())));
                    return;
                } else if(objectClass == Float.class) {
                    Arrays.stream(str.split(",")).forEach(item -> this.add(ValUtil.toFloat(item.trim())));
                    return;
                } else if(objectClass == Double.class) {
                    Arrays.stream(str.split(",")).forEach(item -> this.add(ValUtil.toDouble(item.trim())));
                    return;
                } else if(objectClass == Boolean.class) {
                    Arrays.stream(str.split(",")).forEach(item -> this.add(ValUtil.toBoolean(item.trim())));
                    return;
                }
            }

            if(objectClass != null) {
                this.addAll(JacksonUtil.readListValue(str, objectClass));
            } else {
                this.addAll(JacksonUtil.readList(str));
            }
        }
    }

    /**
     * 添加元素到列表
     *
     * @param obj 要添加的对象
     * @return 当前实例
     */
    public JSONList adds(Object obj) {
        add(obj);
        return this;
    }

    /**
     * 创建JSONList实例
     *
     * @param json 源JSON数据
     * @return JSONList实例
     */
    public static JSONList createJsonList(Object json) {
        return new JSONList(json);
    }

    /**
     * 将JSONList转换为指定类型的列表
     *
     * @param objectClass 目标类型
     * @param <T> 目标类型泛型
     * @return 指定类型的列表
     */
    public <T> List<T> asList(Class<T> objectClass) {
        return this.stream().map(item -> ValUtil.toObj(item, objectClass)).collect(Collectors.toList());
    }

    /**
     * 将JSONList转换为JSONMap类型的列表
     *
     * @return JSONMap类型的列表
     */
    public List<JSONMap> asList() {
        return asList(JSONMap.class);
    }

    /**
     * 获取指定索引处的JSONMap对象
     *
     * @param index 索引
     * @return JSONMap对象
     */
    public JSONMap getMap(int index) {
        Object o = getIndexObject(index, null);
        if(o==null){
            return null;
        }
        if(o instanceof JSONMap) {
            return (JSONMap) o;
        }
        if(o instanceof Number || o instanceof Boolean || o instanceof Date || o instanceof TemporalAccessor) {
            throw new RuntimeException("对象是简单类型【" + o.getClass().getName() + "】，不能转换为JSONMap");
        }
        if(o instanceof CharSequence) {
            String str = o.toString();
            if(JacksonUtil.isJsonObj(str)) {
                return new JSONMap(o);
            }
            throw new RuntimeException("对象是简单类型【" + o.getClass().getName() + "】，不能转换为JSONMap");
        }
        return new JSONMap(o);
    }

    /**
     * 返回JSON字符串表示
     *
     * @return JSON字符串
     */
    public String toString() {
        return JacksonUtil.getJson(this);
    }

    /**
     * 获取指定索引处的对象
     *
     * @param index 索引
     * @return 指定索引处的对象
     */
    @Override
    public Object getIndexObject(int index) {
        return get(index);
    }

    /**
     * 获取指定索引处的对象
     *
     * @param index 索引
     * @return 指定索引处的对象
     */
    @Override
    public Object getIndexObject(int index,Object defaultV) {
        try {
            if(index<0){
                index = size()+index;
            }
            Object re = get(index);
            if(re == null) {
                return defaultV;
            }
            return re;
        } catch (IndexOutOfBoundsException e) {
            if(defaultV!=null){
                return defaultV;
            }
            throw e;
        }
    }

    /**
     * 获取信息对象
     *
     * @return 信息对象（即自身）
     */
    @Override
    public Object getInfoObject() {
        return this;
    }
}