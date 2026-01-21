package com.dlz.test.comm.json;

import com.dlz.comm.json.JSONMap;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * JSONMap够着方法测试
 */
public class JSONMapConstructionTest {
	/**
	 * 无参够着方法
	 */
	@Test
	public void test1(){
		JSONMap paras = new JSONMap();
		System.out.println(paras);
		//输出：{}
		paras.put("a",1);
		System.out.println(paras);
		//输出：{"a":1}
		paras.put("a","1");
		System.out.println(paras);
		//输出：{"a":"1"}
	}

	/**
	 * 参数为JSON字符串
	 */
	@Test
	public void test2(){
		JSONMap paras = new JSONMap("{\"a\":{\"b\":2}}");
		System.out.println(paras); 
		//输出：{"a":{"b":2}}
		paras.put("b","2");
		System.out.println(paras);
		//输出：{"a":{"b":2},"b":"2"}

		paras.set("c.c1","666");
		System.out.println(paras);
		//输出：{"a":{"b":2},"b":"2","c":{"c1":"666"}}
	}

	/**
	 * 参数为键值对
	 */
	@Test
	public void test3(){
		JSONMap paras = new JSONMap("a",1,"b","2");
		System.out.println(paras);
		//输出：{"a":1,"b":"2"}
		paras.put("c","3");
		System.out.println(paras);
		//输出：{"a":1,"b":"2","c":"3"}
	}

	/**
	 * 参数为Map
	 */
	@Test
	public void test4(){
		Map<String,Object> arg=new HashMap<>();
		arg.put("a",1);
		arg.put("b","2");
		JSONMap paras = new JSONMap(arg);
		System.out.println(paras);
		//输出：{"a":1,"b":"2"}
		paras.put("c","3");
		System.out.println(paras);
		//输出：{"a":1,"b":"2","c":"3"}
	}

	/**
	 * 参数为对象
	 */
	@Test
	public void test5(){
		TestBean arg=new TestBean();
		arg.setA(1);
		arg.setB("2");
		JSONMap paras = new JSONMap(arg);
		System.out.println(paras);
		//输出：{"a":1,"b":"2"}
		paras.put("c","1");
		System.out.println(paras);
		//输出：{"a":1,"b":"2","c":"1"}

		paras.put("a","12");
		TestBean as = paras.as(TestBean.class);
		System.out.println(as);
		//输出：com.dlz.test.comm.json.TestBean@10db82ae
		System.out.println(as.getA());
		//输出：12
		System.out.println(as.getB());
		//输出：2
	}
}

