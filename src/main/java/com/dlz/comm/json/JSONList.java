package com.dlz.comm.json;

import com.dlz.comm.util.JacksonUtil;
import com.dlz.comm.util.ValUtil;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JSON列表类
 * 
 * 继承ArrayList<Object>，实现了IUniversalVals和IUniversalVals4List接口，提供便捷的JSON数组操作功能
 * 
 * @author dk 2017-09-05
 */
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> JSONList(Object obj, Class<T> objectClass) {
		super();
		if(obj == null) {
			return;
		}
		if(obj instanceof Collection) {
			if(objectClass != null) {
				final Iterator input2 = ((Collection) obj).iterator();
				while(input2.hasNext()) {
					final Object next = input2.next();
					if(objectClass.isAssignableFrom(next.getClass())) {
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
				for (int i = 0; i < input2.length; i++) {
					if(objectClass.isAssignableFrom(input2[i].getClass())) {
						add(input2[i]);
					} else {
						add(ValUtil.toObj(input2[i], objectClass));
					}
				}
			} else {
				Collections.addAll(this, (Object[]) obj);
			}
		} else {
			String string = null;
			if(obj instanceof CharSequence) {
				string = obj.toString().trim();
			} else {
				string = JacksonUtil.getJson(obj);
			}
			if(string == null) {
				return;
			}
			if(!JacksonUtil.isJsonArray(string)) {
				if(string.indexOf(",") > -1) {
					if(objectClass == String.class || objectClass == null) {
						Arrays.stream(string.split(",")).forEach(item -> this.add(item.trim()));
						return;
					} else if(objectClass == Integer.class) {
						Arrays.stream(string.split(",")).forEach(item -> this.add(ValUtil.toInt(item.trim())));
						return;
					} else if(objectClass == Long.class) {
						Arrays.stream(string.split(",")).forEach(item -> this.add(ValUtil.toLong(item.trim())));
						return;
					} else if(objectClass == Date.class) {
						Arrays.stream(string.split(",")).forEach(item -> this.add(ValUtil.toDate(item.trim())));
						return;
					} else if(objectClass == BigDecimal.class) {
						Arrays.stream(string.split(",")).forEach(item -> this.add(ValUtil.toBigDecimal(item.trim())));
						return;
					} else if(objectClass == Float.class) {
						Arrays.stream(string.split(",")).forEach(item -> this.add(ValUtil.toFloat(item.trim())));
						return;
					} else if(objectClass == Double.class) {
						Arrays.stream(string.split(",")).forEach(item -> this.add(ValUtil.toDouble(item.trim())));
						return;
					} else if(objectClass == Boolean.class) {
						Arrays.stream(string.split(",")).forEach(item -> this.add(ValUtil.toBoolean(item.trim())));
						return;
					}
				}
				throw new RuntimeException("参数不能转换成JSONList:" + string);
			}

			if(objectClass != null) {
				this.addAll(JacksonUtil.readListValue(string, objectClass));
			} else {
				this.addAll(JacksonUtil.readList(string));
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
		Object o = get(index);
		if(o instanceof JSONMap) {
			return (JSONMap) o;
		}
		if(o instanceof Number) {
			throw new RuntimeException("对象是简单类型【" + o.getClass().getName() + "】，不能转换为JSONMap");
		}
		if(o instanceof CharSequence) {
			if(((CharSequence) o).charAt(0) == '{') {
				return JacksonUtil.readValue(o.toString());
			}
			throw new RuntimeException("对象是简单类型【" + o.getClass().getName() + "】，不能转换为JSONMap");
		}
		return new JSONMap(o);
	}

	/**
	 * 获取指定索引处的对象
	 * 
	 * @param index 索引
	 * @return JSONMap对象
	 */
	public JSONMap getObj(int index) {
		return getObj(index, JSONMap.class);
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
	 * 获取信息对象
	 * 
	 * @return 信息对象（即自身）
	 */
	@Override
	public Object getInfoObject() {
		return this;
	}
}