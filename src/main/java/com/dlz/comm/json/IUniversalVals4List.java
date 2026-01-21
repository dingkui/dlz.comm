package com.dlz.comm.json;

import com.dlz.comm.util.ValUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 万能取值器4列表
 * @author dk 2017-06-15
 *
 */
@SuppressWarnings({ "rawtypes" })
interface IUniversalVals4List {
	default BigDecimal getBigDecimal(int index){
		return  getBigDecimal(index,null);
	}
	default BigDecimal getBigDecimal(int index,BigDecimal defaultV){
		return ValUtil.toBigDecimal(getIndexObject(index),defaultV);
	}
	default Double getDouble(int index){
		return getDouble(index,null);
	}
	default Double getDouble(int index,Double defaultV){
		return ValUtil.toDouble(getIndexObject(index),defaultV);
	}
	default Float getFloat(int index){
		return  getFloat(index,null);
	}
	default Float getFloat(int index,Float defaultV){
		return ValUtil.toFloat(getIndexObject(index),defaultV);
	}
	default Integer getInt(int index){
		return  getInt(index,null);
	}
	default Integer getInt(int index,Integer defaultV){
		return ValUtil.toInt(getIndexObject(index),defaultV);
	}
	default Long getLong(int index){
		return  getLong(index,null);
	}
	default Long getLong(int index,Long defaultV){
		return ValUtil.toLong(getIndexObject(index),defaultV);
	}
	default Object[] getArray(int index){
		return  getArray(index,null);
	}
	default Object[] getArray(int index,Object[] defaultV){
		return ValUtil.toArray(getIndexObject(index),defaultV);
	}
	default List getList(int index){
		return  getList(index,null);
	}
	default List getList(int index,List defaultV){
		return ValUtil.toList(getIndexObject(index),defaultV);
	}
	default String getStr(int index){
		return  getStr(index,null);
	}
	default String getStr(int index,String defaultV){
		return ValUtil.toStr(getIndexObject(index),defaultV);
	}
	default Boolean getBoolean(int index){
		return getBoolean(index,null);
	}
	default Boolean getBoolean(int index,Boolean defaultV){
		return ValUtil.toBoolean(getIndexObject(index),defaultV);
	}
	default Date getDate(int index){
		return ValUtil.toDate(getIndexObject(index));
	}
	default Date getDate(int index,String format){
		return ValUtil.toDate(getIndexObject(index),format);
	}
	default String getDateStr(int index){
		return ValUtil.toDateStr(getIndexObject(index));
	}
	default String getDateStr(int index,String format){
		return ValUtil.toDateStr(getIndexObject(index),format);
	}
	default <T> T getObj(int index,Class<T> classs){
		return ValUtil.toObj(getIndexObject(index),classs);
	}
	Object getIndexObject(int index);
}