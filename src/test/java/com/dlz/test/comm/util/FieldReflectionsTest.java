package com.dlz.test.comm.util;

import com.dlz.comm.fn.DlzFn;
import com.dlz.comm.util.system.FieldReflections;
import com.dlz.test.beans.AA;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.lang.reflect.Field;

@Slf4j
public class FieldReflectionsTest<T> {
	@Test
	public void test1(){
		FieldReflectionsTest<AA> t = new FieldReflectionsTest<>();
		t.t(AA::getA1);
		t.t(AA::getXxx);
	}

	private  void t(DlzFn<T,?> property){
		Field field = FieldReflections.getFn(property).v2;
		String fieldName = field.getName();
		System.out.println(fieldName);
	}

}
