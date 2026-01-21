package com.dlz.comm.json;

import com.dlz.comm.exception.SystemException;
import com.dlz.comm.util.JacksonUtil;
import com.dlz.comm.util.StringUtils;
import com.dlz.comm.util.ValUtil;

import java.util.*;

/**
 * JSON映射类
 * 
 * 继承HashMap<String, Object>，实现了IUniversalVals接口，提供便捷的JSON数据操作功能
 * 
 * @author dk 2017-06-15
 */
public class JSONMap extends HashMap<String, Object> implements IUniversalVals {
	/**
	 * 序列化版本UID
	 */
	private static final long serialVersionUID = 7554800764909179290L;
	
	/**
	 * 无参构造函数
	 */
	public JSONMap() {
		super();
	}
	
	/**
	 * 使用对象构造JSONMap
	 * 
	 * @param obj 源对象，可以是Map或其他对象
	 */
	public JSONMap(Object obj) {
		super();
		if(obj == null) {
			return;
		}
		if(obj instanceof Map) {
			putAll((Map) obj);
		} else {
			String string = JacksonUtil.getJson(obj);
			if(string == null) {
				return;
			}
			putAll(JacksonUtil.readValue(string));
		}
	}
	
	/**
	 * 使用字符序列构造JSONMap
	 * 
	 * @param obj 字符序列，必须是有效的JSON字符串
	 */
	public JSONMap(CharSequence obj) {
		super();
		if(obj == null) {
			return;
		}
		String str = obj.toString().trim().replaceAll("//.*", "");
		if(JacksonUtil.isJsonObj(str)) {
			putAll(JacksonUtil.readValue(str));
		} else {
			throw new SystemException("参数不能转换成JSONMap:" + str);
		}
	}
	
	/**
	 * 使用键值对构造JSONMap
	 * 
	 * @param key 第一个键
	 * @param val 第一个值
	 * @param value 键值对数组，必须是偶数个元素
	 */
	public JSONMap(String key, Object val, Object... value) {
		super();
		int length = value.length;
		if(length % 2 == 1) {
			throw new SystemException("参数个数只能是偶数");
		}
		put(key, val);
		for(int index = 0; index < length - 1; index += 2) {
			if(!(value[index] instanceof String)) {
				throw new SystemException("键名必须为String");
			}
			put((String) value[index], value[index + 1]);
		}
	}
	
	/**
	 * 创建JSONMap实例
	 * 
	 * @param json 源JSON数据
	 * @return JSONMap实例
	 */
	public static JSONMap createJsonMap(Object json) {
		return new JSONMap(json);
	}
	
	/**
	 * 将JSONMap转换为指定类型的Map
	 * 
	 * @param objectClass 目标对象类型
	 * @param <T> 目标类型泛型
	 * @return 指定类型的Map
	 */
	public <T> Map<String, T> asMap(Class<T> objectClass) {
		 this.forEach((key, value) -> this.put(key, ValUtil.toObj(value, objectClass)));
		 return (Map<String, T>) this;
	}
	
	/**
	 * 将JSONMap转换为JSONMap类型的Map
	 * 
	 * @return JSONMap类型的Map
	 */
	public Map<String, JSONMap> asMap() {
		return asMap(JSONMap.class);
	}
	
	/**
	 * 将JSONMap转换为JSONList类型的Map
	 * 
	 * @return JSONList类型的Map
	 */
	public Map<String, JSONList> asMapList() {
		return asMap(JSONList.class);
	}
	
	/**
	 * 清除空属性
	 * 
	 * @return 当前实例
	 */
	public JSONMap clearEmptyProp() {
		List<String> emptyKeys = new ArrayList<>();
		for(Entry<String, Object> entry : this.entrySet()) {
			if(StringUtils.isEmpty(entry.getValue())) {
				emptyKeys.add(entry.getKey());
			}
		}
		for(String key : emptyKeys) {
			this.remove(key);
		}
		return this;
	}
	
	/**
	 * 按层次设定值，设定的对象需要是JSONMap对象
	 * 采用合并方式
	 * 
	 * @param key 层次键，如：a.b.c.d
	 * @param value 要设定的值
	 * @return 当前实例
	 */
	public JSONMap set(String key, Object value) {
		return set(key, value, 1);
	}

	/**
	 * 按层次设定值，设定的对象需要是JSONMap对象
	 * 
	 * @param key 层次键，如：a.b.c.d
	 * @param value 要设定的值
	 * @param joinMethod 合并方式
	 * 		0: 替换
	 * 		1: 合并
	 * @return 当前实例
	 */
	public JSONMap set(String key, Object value, int joinMethod) {
		if(value == null) {
			return this;
		}
		int i = key.indexOf(".");
		if(i > -1) {
			String key0 = key.substring(0, i);
			String key1 = key.substring(i + 1);
			Object o = this.get(key0);
			if(o == null) {
				o = new JSONMap();
				put(key0, o);
			}
			if(!(o instanceof JSONMap)) {
				throw new SystemException("不支持的设定信息:" + o.getClass() + key.substring(i));
			}
			this.put(key0, ((JSONMap) o).set(key1, value));
		} else {
			Object o = this.get(key);
			if(o instanceof JSONMap) {
				if(!Map.class.isAssignableFrom(value.getClass())) {
					throw new SystemException("设定类型不一致:" + value.getClass() + "→" + o.getClass());
				} else {
					if(joinMethod == 0) {
						put(key, value);
					} else {
						((JSONMap) o).putAll((Map) value);
					}
				}
			} else {
				put(key, value);
			}
		}
		return this;
	}
	
	/**
	 * 将值添加到JSONMap子值中
	 * 
	 * @param key 设定的key
	 * @param obj 设定对象
	 * @param joinMethod 合并方式
	 * 		0: 替换原有信息
	 * 		1: 加入到原有数组中
	 * 		2: 合并到原有数组中
	 * 		3: 把原有数据跟新数据构造新数组
	 * @return 当前实例
	 */
	public JSONMap add(String key, Object obj, int joinMethod) {
		Object o = this.get(key);
		if(o == null) {
			put(key, obj);
			return this;
		}
		switch(joinMethod) {
			case 0:
				put(key, obj);
				break;
			case 1:
				if(o instanceof Collection || o instanceof Object[]) {
					List list = ValUtil.toList(o);
					list.add(obj);
					put(key, list);
				}
				break;
			case 2:
				List list;
				if(o instanceof Collection || o instanceof Object[]) {
					list = ValUtil.toList(o);
				} else {
					list = new ArrayList();
					list.add(o);
				}
				if(obj instanceof Collection || obj instanceof Object[]) {
					list.addAll(ValUtil.toList(obj));
				} else {
					list.add(obj);
				}
				put(key, list);
				break;
		}
		return this;
	}
	
	/**
	 * 将值添加到JSONMap子值中（默认合并方式）
	 * 
	 * @param key 设定的key
	 * @param obj 设定对象
	 * @return 当前实例
	 */
	public JSONMap add(String key, Object obj) {
		return add(key, obj, 2);
	}
	
	/**
	 * 将值添加到JSONMap的列表中
	 * 
	 * @param key 设定的key
	 * @param obj 设定对象
	 * @return 当前实例
	 */
	public JSONMap add2List(String key, Object obj) {
		List list = this.getList(key);
		if(list == null) {
			list = new ArrayList();
		}
		if(obj instanceof Collection || obj instanceof Object[]) {
			list = ValUtil.toList(obj);
		} else {
			list.add(obj);
		}
		put(key, list);
		return this;
	}
	
	/**
	 * 设置键值对
	 * 
	 * @param key 键
	 * @param value 值
	 * @return 当前实例
	 */
	@Override
	public JSONMap put(String key, Object value) {
		super.put(key, value);
		return this;
	}
	
	/**
	 * 返回JSON字符串表示
	 * 
	 * @return JSON字符串
	 */
	@Override
	public String toString() {
		return JacksonUtil.getJson(this);
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