package com.dlz.test.comm.util;

import com.dlz.comm.util.ValUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ValUtilTest {
	@Test
	public void coverString(){
		Double aDouble = ValUtil.toDouble("3.35");
		System.out.println(aDouble.toString());
		System.out.println((ValUtil.toBigDecimal(aDouble).toString()));
	}
	@Test
	public void getArray(){
		List a=new ArrayList();
		a.add(1l);
		a.add(2);
		Map[] array = ValUtil.toArray(a, Map.class);
		System.out.println(array);
	}

}
