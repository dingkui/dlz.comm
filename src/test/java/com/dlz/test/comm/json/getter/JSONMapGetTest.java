package com.dlz.test.comm.json.getter;

import com.dlz.comm.json.JSONList;
import com.dlz.comm.json.JSONMap;
import com.dlz.comm.util.JacksonUtil;
import com.dlz.comm.util.ValUtil;
import com.dlz.test.beans.AA;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

/**
 * JSONMap取得值类型转换
 */
public class JSONMapGetTest {
	/**
	 * 基础用法
	 */
	@Test
	public void test1(){
		//{"a":{"b":1}}
		JSONMap paras = new JSONMap("{\"a\":{\"b\":1}}");
		String strB = paras.getStr("a.b");//取得String
		Integer intB = paras.getInt("a.b");//取得Integer
		System.out.println(strB.getClass()+":"+strB);
		//输出：class java.lang.String:1
		System.out.println(intB.getClass()+":"+intB);
		//输出：class java.lang.Integer:1
	}
	@Test
	public void test2(){
		//{"a":{"b2":{"c22":"221","c21":"22"},"b":{"c1":"22","c2":"221"}}}
		JSONMap paras = new JSONMap("{\"a\":{\"b2\":{\"c22\":\"221\",\"c21\":\"22\"},\"b\":{\"c1\":\"22\",\"c2\":\"221\"}}}");
		System.out.println(paras.getStr("a.b"));
		//输出：{"c1":"22","c2":"221"}
		
		//设置数据
		HashMap<String, Object> objectObjectHashMap = new HashMap<>();
		objectObjectHashMap.put("d",1);
		paras.set("a.b.c.d",objectObjectHashMap);
		System.out.println(paras);
		//输出：{"a":{"b2":{"c22":"221","c21":"22"},"b":{"c":{"d":{"d":1}},"c1":"22","c2":"221"}}}
	}
	@Test
	public void test01(){
		JSONMap paras = new JSONMap("a",new JSONMap("b",new JSONMap("c1","22","c2","221"),"b2",new JSONMap("c21","22","c22","221")));
		System.out.println(paras);
		JSONMap p2=new JSONMap("{\"a\":{\"b2\":{\"c22\":\"221\",\"c21\":\"22\"},\"b\":{\"c1\":\"22\",\"c2\":\"221\"}}}");
		JSONMap map = p2.getMap("a");
		map.set("b.c1","999");
//		HashMap<String, Object> objectObjectHashMap = new HashMap<>();
//		objectObjectHashMap.put("d",1);
//		paras.set("a.b",objectObjectHashMap);
		System.out.println(p2);
	}

	@Test
	public void test(){
		JSONMap paras = new JSONMap();
		paras.put("puid", "1111111111111111123123123213413333333333333333333333333333333333333333333333333333333333333333333333234124312341431324.21352345324534253");
		System.out.println(paras.getBigDecimal("puid"));
		paras.put("puid1", "123");
		System.out.println(paras.getBigDecimal("puid1"));
		paras.put("puid2", 123L);
		System.out.println(paras.getBigDecimal("puid2"));
		paras.put("puid3", "123.1");
		System.out.println(paras.getBigDecimal("puid3"));
		paras.put("puid4", 123.1);
		System.out.println(paras.getBigDecimal("puid4"));
		System.out.println(paras.getFloat("puid4"));
		System.out.println(paras.getLong("puid4"));
		System.out.println(paras.getInt("puid4"));
		System.out.println(paras.getStr("puid4"));
	}
	
	/**
	 * 数字类型转换
	 */
	@Test
	public void test13(){
		JSONMap paras = new JSONMap();
		paras.put("puid", "1111111111111111123123123213413333333333333333333333333333333333333333333333333333333333333333333333234124312341431324.21352345324534253");
		System.out.println(paras.getBigDecimal("puid"));
		paras.put("puid1", "123");
		System.out.println(paras.getBigDecimal("puid1"));
		paras.put("puid2", 123L);
		System.out.println(paras.getBigDecimal("puid2"));
		paras.put("puid3", "123.1");
		System.out.println(paras.getBigDecimal("puid3"));
		paras.put("puid4", 123.1);
		System.out.println(paras.getBigDecimal("puid4"));

		JSONMap paras2 = new JSONMap(null);
		System.out.println(paras2.getFloat("puid4"));
		System.out.println(paras2.getLong("puid4"));
		System.out.println(paras2.getInt("puid4"));
		System.out.println(paras2.getStr("puid4"));
	}
	
	/**
	 * 多级查询
	 */
	@Test
	public void test3(){
		JSONMap paras = new JSONMap();
		JSONMap paras2 = new JSONMap();
		JSONMap paras3 = new JSONMap();
		paras.put("a", paras2);
		paras2.put("b", paras3);
		paras3.put("c", 1);
		System.out.println(paras.getStr("a.b.c"));
		System.out.println(paras2.getFloat("b.c"));
	}
	/**
	 * JSON构造
	 */
	@Test
	public void test4(){
		JSONMap paras = new JSONMap();
		JSONMap paras2 = new JSONMap();
		JSONMap paras3 = new JSONMap();
		paras.put("a", paras2);
		paras2.put("b", paras3);
		paras3.put("c", 1);
		
		String json1=paras.toString();
		System.out.println(json1);
		JSONMap paras4 = JacksonUtil.readValue(json1);
		System.out.println(paras4.getMap("a.b").get("c"));
		System.out.println(paras4.getStr("a.b.c"));
	}
	
	
	/**
	 * 类型转换
	 */
	@Test
	public void test5(){
		JSONMap paras = new JSONMap("{\"a\":[1,2,3],\"b\":{\"b\":1,\"a\":2}}");
		System.out.println(paras.getList("a"));
		System.out.println(JacksonUtil.getJson(paras.getArray("a")));
		System.out.println(paras.getList("b"));
		
		JSONMap c2=JacksonUtil.coverObj(paras, JSONMap.class);
		System.out.println(c2.getList("a"));
		System.out.println(JacksonUtil.getJson(c2.getArray("a")));
		System.out.println(c2.getList("b"));
		System.out.println(c2.getArray("a",Integer.class));
	}
	
	/**
	 * 类型转换
	 */
	@Test
	public void test6(){
		JSONList a=new JSONList("[\"{\\\"b\\\":1,\\\"a\\\":2}\",{\"b\":1,\"a\":2},{\"b1\":1,\"a1\":2}]");
		
		System.out.println(a.getMap(0).getStr("b"));
//		System.out.println(c2.getArray("a"));
//		System.out.println(c2.getList("b"));
	}
	
	
	
	/**
	 * 类型转换
	 */
	@Test
	public void test7(){
		JSONMap b=new JSONMap();
		JSONMap a=new JSONMap();
		b.add("info", a);
		a.add("l1", (new JSONList()).adds((new JSONMap()).add("l1_1", 1)).adds((new JSONMap()).add("l1_1", 2)));
		a.add("l1", (new JSONList()).adds((new JSONMap()).add("l1_2", 3)).adds((new JSONMap()).add("l1_2", 4)).adds((new JSONMap()).add("l1_2", 5)),3);
//		a.add("l1", (new JSONMap()).add("l1_2", 123).add("l1_2", 124));
		System.out.println(a);
		System.out.println(b);
		System.out.println(b.getList("info.l1[1]"));
		System.out.println(a.getStr("l1[1][1].l1_2"));
		System.out.println(b.getStr("info.l1[1][-1].l1_2"));
		
//		System.out.println(c2.getArray("a"));
//		System.out.println(c2.getList("b"));
	}
	/**
	 * 类型转换
	 */
	@Test
	public void test8(){
		String a="[{\"xxx\":null,\"c\":[{\"d\":1}]}]";
		final JSONList jsonList = new JSONList(a, AA.class);
		System.out.println(jsonList.get(0) instanceof JSONMap);
		System.out.println(jsonList.get(0) instanceof AA);
		System.out.println(jsonList);
	}
	
	@Test
	public void test9(){
		String a="[\"a\",1]";
		String[] c=ValUtil.toArray(a, String.class);
		System.out.println(JacksonUtil.getJson(c));


		String[] c3=ValUtil.toArray(a, String.class);
		List<String> c2=ValUtil.toList(a, String.class);
		System.out.println(c2.get(0));
		System.out.println(c[1]);
	}

	@Test
	public void test10(){
		String a="[\"a\",1]";
		String[] c=ValUtil.toArray(a, String.class);
		System.out.println(JacksonUtil.getJson(c));
		List<String> c2=ValUtil.toList(a, String.class);
		System.out.println(c2.get(0));
		System.out.println(c[1]);
	}


	@Test
	public void test11(){
		String a="{\"a\":\"1,2,3,4,5\"}";
		List<String> a1 = new JSONMap(a).getList("a",String.class);
		System.out.println(a1);
	}

	@Test
	public void test12(){
		String a="1,2,3,4,5";
		List<Integer> listObj = ValUtil.toList(a, Integer.class);
		System.out.println(listObj);
	}
}

