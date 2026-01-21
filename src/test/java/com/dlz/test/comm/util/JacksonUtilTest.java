package com.dlz.test.comm.util;

import com.dlz.comm.util.JacksonUtil;
import com.fasterxml.jackson.databind.JavaType;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JacksonUtilTest {
	@Test
	public void atTest(){
		String data="{\"info\":{\"a\":[[{\"b\":1},{\"c\":2}],[{\"d\":3},{\"e\":4},{\"f\":5}]]}}";
		System.out.println("c的值："+JacksonUtil.at(data,"info.a[0][1].c"));
		System.out.println("f的值："+JacksonUtil.at(data,"info.a[1][2].f"));
		System.out.println("f所在对象："+JacksonUtil.at(data,"info.a[1][2]"));
		System.out.println("f所在对象："+JacksonUtil.at(data,"info.a[1][-1]"));
	}


	@Test
	public void coverString(){
		log.debug("123123");
	}

	@Test
	public void JacsonArrayTest() throws ClassNotFoundException {
		Map<String,Float>[] c=new HashMap[0];
		System.out.println(c.getClass());
		JavaType javaType2 = JacksonUtil.mkJavaType(Map.class,String.class,Float.class);
		JavaType javaType = JacksonUtil.mkJavaTypeByJavaTypes(Class.forName("[L"),javaType2);
		String a="[{\"a\":1}]";
		Map<String,Float>[] b = JacksonUtil.readValue(a,javaType);
		log.debug("123123");
	}
}
