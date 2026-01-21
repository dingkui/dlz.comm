package com.dlz.comm.json;

import com.dlz.comm.util.JacksonUtil;
import com.dlz.comm.util.ValUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 万能取值器接口
 * 
 * 提供便捷的数据获取方法，支持多种数据类型的转换
 * 
 * @author dk 2017-06-15
 */
@SuppressWarnings({ "rawtypes" })
public interface IUniversalVals {
	/**
	 * 获取指定键的BigDecimal值
	 * 
	 * @param key 键名
	 * @return BigDecimal值
	 */
	default BigDecimal getBigDecimal(String key) {
		return getBigDecimal(key, null);
	}
	
	/**
	 * 获取指定键的BigDecimal值
	 * 
	 * @param key 键名
	 * @param defaultV 默认值
	 * @return BigDecimal值
	 */
	default BigDecimal getBigDecimal(String key, BigDecimal defaultV) {
		return ValUtil.toBigDecimal(getKeyVal(key), defaultV);
	}
	
	/**
	 * 获取指定键的Double值
	 * 
	 * @param key 键名
	 * @return Double值
	 */
	default Double getDouble(String key) {
		return getDouble(key, null);
	}
	
	/**
	 * 获取指定键的Double值
	 * 
	 * @param key 键名
	 * @param defaultV 默认值
	 * @return Double值
	 */
	default Double getDouble(String key, Double defaultV) {
		return ValUtil.toDouble(getKeyVal(key), defaultV);
	}
	
	/**
	 * 获取指定键的Float值
	 * 
	 * @param key 键名
	 * @return Float值
	 */
	default Float getFloat(String key) {
		return getFloat(key, null);
	}
	
	/**
	 * 获取指定键的Float值
	 * 
	 * @param key 键名
	 * @param defaultV 默认值
	 * @return Float值
	 */
	default Float getFloat(String key, Float defaultV) {
		return ValUtil.toFloat(getKeyVal(key), defaultV);
	}
	
	/**
	 * 获取指定键的Integer值
	 * 
	 * @param key 键名
	 * @return Integer值
	 */
	default Integer getInt(String key) {
		return getInt(key, null);
	}
	
	/**
	 * 获取指定键的Integer值
	 * 
	 * @param key 键名
	 * @param defaultV 默认值
	 * @return Integer值
	 */
	default Integer getInt(String key, Integer defaultV) {
		return ValUtil.toInt(getKeyVal(key), defaultV);
	}
	
	/**
	 * 获取指定键的Long值
	 * 
	 * @param key 键名
	 * @return Long值
	 */
	default Long getLong(String key) {
		return getLong(key, null);
	}
	
	/**
	 * 获取指定键的Long值
	 * 
	 * @param key 键名
	 * @param defaultV 默认值
	 * @return Long值
	 */
	default Long getLong(String key, Long defaultV) {
		return ValUtil.toLong(getKeyVal(key), defaultV);
	}
	
	/**
	 * 获取指定键的数组值
	 * 
	 * @param key 键名
	 * @return 数组值
	 */
	default Object[] getArray(String key) {
		return getArray(key, (Object[]) null);
	}
	
	/**
	 * 获取指定键的数组值
	 * 
	 * @param key 键名
	 * @param clazz 数组元素类型
	 * @param <T> 数组元素类型泛型
	 * @return 指定类型的数组值
	 */
	default <T> T[] getArray(String key, Class<T> clazz) {
		return ValUtil.toArray(getKeyVal(key), clazz);
	}
	
	/**
	 * 获取指定键的数组值
	 * 
	 * @param key 键名
	 * @param defaultV 默认值
	 * @return 数组值
	 */
	default Object[] getArray(String key, Object[] defaultV) {
		return ValUtil.toArray(getKeyVal(key), defaultV);
	}
	
	/**
	 * 获取指定键的JSONList值
	 * 
	 * @param key 键名
	 * @return JSONList值
	 */
	default JSONList getList(String key) {
		return getList(key, (List) null);
	}
	
	/**
	 * 获取指定键的JSONList值
	 * 
	 * @param key 键名
	 * @param defaultV 默认值
	 * @return JSONList值
	 */
	default JSONList getList(String key, List defaultV) {
		return ValUtil.toList(getKeyVal(key), defaultV);
	}
	
	/**
	 * 获取指定键的JSONMap值
	 * 
	 * @param key 键名
	 * @return JSONMap值
	 */
	default JSONMap getMap(String key) {
		return getObj(key, JSONMap.class);
	}
	
	/**
	 * 获取指定键的列表值
	 * 
	 * @param key 键名
	 * @param clazz 列表元素类型
	 * @param <T> 列表元素类型泛型
	 * @return 指定类型的列表值
	 */
	default <T> List<T> getList(String key, Class<T> clazz) {
		return ValUtil.toList(getKeyVal(key), clazz);
	}
	
	/**
	 * 获取指定键的字符串值
	 * 
	 * @param key 键名
	 * @return 字符串值
	 */
	default String getStr(String key) {
		return getStr(key, null);
	}
	
	/**
	 * 获取指定键的字符串值
	 * 
	 * @param key 键名
	 * @param defaultV 默认值
	 * @return 字符串值
	 */
	default String getStr(String key, String defaultV) {
		return ValUtil.toStr(getKeyVal(key), defaultV);
	}
	
	/**
	 * 获取指定键的布尔值
	 * 
	 * @param key 键名
	 * @return 布尔值
	 */
	default Boolean getBoolean(String key) {
		return getBoolean(key, null);
	}
	
	/**
	 * 获取指定键的布尔值
	 * 
	 * @param key 键名
	 * @param defaultV 默认值
	 * @return 布尔值
	 */
	default Boolean getBoolean(String key, Boolean defaultV) {
		return ValUtil.toBoolean(getKeyVal(key), defaultV);
	}
	
	/**
	 * 获取指定键的日期值
	 * 
	 * @param key 键名
	 * @return 日期值
	 */
	default Date getDate(String key) {
		return ValUtil.toDate(getKeyVal(key));
	}
	
	/**
	 * 获取指定键的日期值
	 * 
	 * @param key 键名
	 * @param format 日期格式
	 * @return 日期值
	 */
	default Date getDate(String key, String format) {
		return ValUtil.toDate(getKeyVal(key), format);
	}

	/**
	 * 获取指定键的LocalDateTime值
	 * 
	 * @param key 键名
	 * @return LocalDateTime值
	 */
	default LocalDateTime getLocalDateTime(String key) {
		return ValUtil.toLocalDateTime(getKeyVal(key));
	}
	
	/**
	 * 获取指定键的LocalDateTime值
	 * 
	 * @param key 键名
	 * @param format 日期时间格式
	 * @return LocalDateTime值
	 */
	default LocalDateTime getLocalDateTime(String key, String format) {
		return ValUtil.toLocalDateTime(getKeyVal(key), format);
	}

	/**
	 * 获取指定键的日期字符串值
	 * 
	 * @param key 键名
	 * @return 日期字符串值
	 */
	default String getDateStr(String key) {
		return ValUtil.toDateStr(getKeyVal(key));
	}
	
	/**
	 * 获取指定键的日期字符串值
	 * 
	 * @param key 键名
	 * @param format 日期格式
	 * @return 日期字符串值
	 */
	default String getDateStr(String key, String format) {
		return ValUtil.toDateStr(getKeyVal(key), format);
	}
	
	/**
	 * 获取指定键的对象值
	 * 
	 * @param key 键名
	 * @param classs 目标类型
	 * @param <T> 目标类型泛型
	 * @return 指定类型的对象值
	 */
	default <T> T getObj(String key, Class<T> classs) {
		return ValUtil.toObj(getKeyVal(key), classs);
	}
	
	/**
	 * 获取指定键的对象值
	 * 
	 * @param key 键名
	 * @param classs 目标类型
	 * @param <T> 目标类型泛型
	 * @return 指定类型的对象值
	 */
	default <T> T at(String key, Class<T> classs) {
		return ValUtil.toObj(getKeyVal(key), classs);
	}
	
	/**
	 * 将当前对象转换为指定类型
	 * 
	 * @param classs 目标类型
	 * @param <T> 目标类型泛型
	 * @return 指定类型的对象
	 */
	default <T> T as(Class<T> classs) {
		return JacksonUtil.coverObj(getInfoObject(), classs);
	}
	
	/**
	 * 根据键获取值
	 * 
	 * @param key 键名
	 * @return 对应的值
	 */
	default Object getKeyVal(String key) {
		return JacksonUtil.at(getInfoObject(), key);
	}
	
	/**
	 * 获取信息对象
	 * 
	 * @return 信息对象
	 */
	Object getInfoObject();
}