package com.dlz.test.comm.json;

import com.dlz.comm.json.JSONMap;
import org.junit.Test;

/**
 * JSONMap取得数据方法测试
 */
public class JSONMapPutSetTest {
	/**
	 * put方法与Map的put方法一致，即key不做解析，直接put到map中
	 */
	@Test
	public void testPut(){
		System.out.println(new JSONMap().put("a","1"));
		//输出：{"a":"1"}

		System.out.println(new JSONMap().put("a.b","1"));
		//输出：{"a":"1","a.b":"1"}

		System.out.println(new JSONMap().put("a.c[1]","1"));
		//输出：{"a":"1","a.b":"1","a.c[1]":"1"}
	}
	/**
	 * 解析key值中的.号，找到对应的对象或直接构造除以对应位置的数据
	 */
	@Test
	public void testSet(){
		System.out.println(new JSONMap().set("a","1"));
		//输出：{"a":"1"}

		System.out.println(new JSONMap().set("a.b","1"));
		//输出：{"a":{"b":"1"}}

		System.out.println(new JSONMap().set("a.b.c","1"));
		//输出：{"a":{"b":"1"}}

		//注意：set方法不支持数组下标
		System.out.println(new JSONMap().set("a.b[0].c","1"));
		//输出：{"a":{"b[0]":{"c":"1"}}}
	}
}

