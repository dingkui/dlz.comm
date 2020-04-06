package com.dlz.comm.json;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.dlz.comm.util.JacksonUtil;
import com.dlz.comm.util.ValUtil;

/**
 * 万能取值器
 * @author dk 2017-06-15
 *
 */
@SuppressWarnings({ "rawtypes" })
interface IUniversalVals {
	default BigDecimal getBigDecimal(String key){
		return  getBigDecimal(key,null);
	}
	default BigDecimal getBigDecimal(String key,BigDecimal defaultV){
		return ValUtil.getBigDecimal(JacksonUtil.at(getInfoObject(),key),defaultV);
	}
	default Double getDouble(String key){
		return getDouble(key,null);
	}
	default Double getDouble(String key,Double defaultV){
		return ValUtil.getDouble(JacksonUtil.at(getInfoObject(),key),defaultV);
	}
	default Float getFloat(String key){
		return  getFloat(key,null);
	}
	default Float getFloat(String key,Float defaultV){
		return ValUtil.getFloat(JacksonUtil.at(getInfoObject(),key),defaultV);
	}
	default Integer getInt(String key){
		return  getInt(key,null);
	}
	default Integer getInt(String key,Integer defaultV){
		return ValUtil.getInt(JacksonUtil.at(getInfoObject(),key),defaultV);
	}
	default Long getLong(String key){
		return  getLong(key,null);
	}
	default Long getLong(String key,Long defaultV){
		return ValUtil.getLong(JacksonUtil.at(getInfoObject(),key),defaultV);
	}
	default Object[] getArray(String key){
		return  getArray(key,null);
	}
	default Object[] getArray(String key, Object[] defaultV){
		return ValUtil.getArray(JacksonUtil.at(getInfoObject(),key),defaultV);
	}
	default <T> T[] getArrayObj(String key, Class<T> clazz, Class<? extends T[]> clazzs){
		return ValUtil.getArrayObj(JacksonUtil.at(getInfoObject(),key),clazz,clazzs);
	}
	default List getList(String key){
		return  getList(key,null);
	}
	default List getList(String key,List defaultV){
		return ValUtil.getList(JacksonUtil.at(getInfoObject(),key),defaultV);
	}
	default <T> List<T> getListObj(String key,Class<T> clazz){
		return ValUtil.getListObj(JacksonUtil.at(getInfoObject(),key),clazz);
	}
	default String getStr(String key){
		return  getStr(key,null);
	}
	default String getStr(String key,String defaultV){
		return ValUtil.getStr(JacksonUtil.at(getInfoObject(),key),defaultV);
	}
	default Boolean getBoolean(String key){
		return getBoolean(key,null);
	}
	default Boolean getBoolean(String key,Boolean defaultV){
		return ValUtil.getBoolean(JacksonUtil.at(getInfoObject(),key),defaultV);
	}
	default Date getDate(String key){
		return ValUtil.getDate(JacksonUtil.at(getInfoObject(),key));
	}
	default Date getDate(String key,String format){
		return ValUtil.getDate(JacksonUtil.at(getInfoObject(),key),format);
	}
	default String getDateStr(String key){
		return ValUtil.getDateStr(JacksonUtil.at(getInfoObject(),key));
	}
	default String getDateStr(String key,String format){
		return ValUtil.getDateStr(JacksonUtil.at(getInfoObject(),key),format);
	}
	default <T> T getObj(String key,Class<T> classs){
		return ValUtil.getObj(JacksonUtil.at(getInfoObject(),key),classs);
	}
	default <T> T at(String key,Class<T> classs){
		return ValUtil.getObj(JacksonUtil.at(getInfoObject(),key),classs);
	}
	default <T> T as(Class<T> classs){
		return JacksonUtil.coverObj(getInfoObject(),classs);
	}
	public Object getInfoObject();
}