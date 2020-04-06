package com.dlz.comm.json;

import com.dlz.comm.util.JacksonUtil;
import com.dlz.comm.util.ValUtil;

import java.util.*;

/**
 * JSONList
 * @author dk 2017-09-05
 *
 */
public class JSONList extends ArrayList<Object> implements IUniversalVals,IUniversalVals4List{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7554800764909179290L;
	
	public JSONList(int initialCapacity){
		super(initialCapacity);
	}
	public JSONList(){
		super();
	}
	public JSONList(Object obj){
		this(obj,null);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> JSONList(Object obj,Class<T> objectClass){
		super();
		if(obj==null){
			return;
		}
		if(obj instanceof Collection){
			if(objectClass!=null){
				final Iterator input2 = ((Collection)obj).iterator();
				while(input2.hasNext()){
					final Object next = input2.next();
					if(objectClass.isAssignableFrom(next.getClass())){
						add(next);
					}else{
						add(ValUtil.getObj(next, objectClass));
					}
				}
			}else{
				addAll((Collection)obj);
			}
		}else if(obj instanceof Object[]){
			if(objectClass!=null){
				final Object[] input2 = (Object[])obj;
				for (int i = 0; i < input2.length; i++) {
					if(objectClass.isAssignableFrom(input2[i].getClass())){
						add(input2[i]);
					}else{
						add(ValUtil.getObj(input2[i], objectClass));
					}
				}
			}else{
				Collections.addAll(this, (Object[])obj);
			}
		}else {
			String string=null;
			if(obj instanceof CharSequence){
				string=obj.toString().trim();
			}else{
				string=JacksonUtil.getJson(obj);
			}
			if(string==null){
				return;
			}
			if(string.startsWith("[") && string.endsWith("]")){
				if(objectClass!=null){
					addAll(JacksonUtil.readListValue(string, objectClass));
				}else{
					addAll(JacksonUtil.readValue(string,JSONList.class));
				}
			}else{
				throw new RuntimeException("参数不能转换成JSONList:"+string);
			}
		}
	}
	
	public JSONList adds(Object obj){
		add(obj);
		return this;
	}
	
	public static JSONList createJsonList(Object json){
		return new JSONList(json);
	}
	
	public <T> List<T> asList(Class<T> objectClass){
		return (List)this;
	}
	public List<JSONMap> asList(){
		return (List)this;
	}
	
	public JSONMap getMap(int index){
		Object o=get(index);
		if(o instanceof JSONMap){
			return (JSONMap)o;
		}
		if(o instanceof Number){
			throw new RuntimeException("对象是简单类型【"+o.getClass().getName()+"】，不能转换为JSONMap");
		}
		if(o instanceof CharSequence){
			if(((CharSequence)o).charAt(0)=='{'){
				return JacksonUtil.readValue(o.toString(), JSONMap.class);
			}
			throw new RuntimeException("对象是简单类型【"+o.getClass().getName()+"】，不能转换为JSONMap");
		}
		return new JSONMap(o);
	}

	public JSONMap getObj(int index){
		return getObj(index,JSONMap.class);
	}

	public String toString(){
		return JacksonUtil.getJson(this);
	}
	@Override
	public Object getIndexObject(int index) {
		return get(index);
	}
	@Override
	public Object getInfoObject() {
		return this;
	}
}