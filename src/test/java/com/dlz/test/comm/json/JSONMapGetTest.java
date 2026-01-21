package com.dlz.test.comm.json;

import com.dlz.comm.json.JSONMap;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

/**
 * JSONMap取得数据方法测试
 */
public class JSONMapGetTest {
	/**
	 * 取得数据并进行类型转换
	 */
	@Test
	public void test1(){
		TestBean arg=new TestBean();
		arg.setA(999);
		arg.setB("bean测试");
		JSONMap paras = new JSONMap("a",1,"b","2","c",arg);
		System.out.println(paras);
		//输出：{"a":1,"b":"2","c":{"a":999,"b":"bean测试"}}

		String strA = paras.getStr("a");//取得String
		System.out.println(strA.getClass()+":"+strA);
		//输出：class java.lang.String:1

		Integer intA = paras.getInt("a");//取得Integer
		System.out.println(intA.getClass()+":"+intA);
		//输出：class java.lang.Integer:1

		Float floatA = paras.getFloat("a");//取得Float
		System.out.println(floatA.getClass()+":"+floatA);
		//输出：class java.lang.Float:1.0
	}

	/**
	 * 取得多级数据并进行类型转换
	 */
	@Test
	public void test2(){
		TestBean arg=new TestBean();
		arg.setA(999);
		arg.setB("bean测试");
		JSONMap paras = new JSONMap("a",1,"b","2","c",arg);
		System.out.println(paras);
		//输出：{"a":1,"b":"2","c":{"a":999,"b":"bean测试"}}

		String strA = paras.getStr("c.a");//取得子对象属性并转换类型
		System.out.println(strA.getClass()+":"+strA);
		//输出：class java.lang.String:999

		Integer intA = paras.getInt("c.a");//取得子对象属性并转换类型
		System.out.println(intA.getClass()+":"+intA);
		//输出：class java.lang.Integer:999
	}

	/**
	 * 取得多级数据并进行类型转换——属性为数组时取法
	 */
	@Test
	public void test3(){
		JSONMap paras = new JSONMap("{\"d\":[666,111, 222, 333, 444]}");
		System.out.println(paras);
		//输出：{"d":[666,111,222,333,444]}

		Integer intD0 = paras.getInt("d[0]");//根据子对象数组下标取得并转换类型
		System.out.println(intD0.getClass()+":"+intD0);
		//输出：class java.lang.Integer:666

		Integer intDlast = paras.getInt("d[-1]");//下标倒数第一个
		System.out.println(intDlast.getClass()+":"+intDlast);
		//输出：class java.lang.Integer:444

		Integer intDlast2 = paras.getInt("d[-2]");//下标倒数第二个
		System.out.println(intDlast2.getClass()+":"+intDlast2);
		//输出：class java.lang.Integer:555
	}

	/**
	 * 更复杂的多级数据取得
	 */
	@Test
	public void test4(){
		Integer[] v=new Integer[]{1,2};
		Map<String,List<TestBean>> map=new HashMap<>();
		map.put("b",Arrays.stream(v).map(n->{
			TestBean arg=new TestBean();
			arg.setB("测试b-"+n);
			return arg;
		}).collect(Collectors.toList()));
		map.put("c",Arrays.stream(v).map(n->{
			TestBean arg=new TestBean();
			arg.setB("测试c-"+n);
			return arg;
		}).collect(Collectors.toList()));

		JSONMap paras = new JSONMap("a",map);
		System.out.println(paras);
		//输出：{"a":{"b":[{"a":0,"b":"测试b-1"},{"a":0,"b":"测试b-2"}],"c":[{"a":0,"b":"测试c-1"},{"a":0,"b":"测试c-2"}]}}

		System.out.println("a.b:"+paras.getStr("a.b"));
		//输出：a.b:[{"a":0,"b":"测试b-1"},{"a":0,"b":"测试b-2"}]
		System.out.println("a.b[0]:"+paras.getStr("a.b[0]"));
		//输出：a.b[0]:{"a":0,"b":"测试b-1"}
		System.out.println("a.b[0].b:"+paras.getStr("a.b[0].b"));
		//输出：a.b[0].b:测试b-1

		System.out.println("a.c:"+paras.getStr("a.c"));
		//输出：a.c:[{"a":0,"b":"测试c-1"},{"a":0,"b":"测试c-2"}]
		System.out.println("a.c[1]:"+paras.getStr("a.c[1]"));
		//输出：a.c[1]:{"a":0,"b":"测试c-2"}
		System.out.println("a.c[1].b:"+paras.getStr("a.c[1].b"));
		//输出：a.c[1].b:测试c-2
	}
}

