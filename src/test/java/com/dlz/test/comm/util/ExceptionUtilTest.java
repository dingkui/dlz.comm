package com.dlz.test.comm.util;

import com.dlz.comm.util.ExceptionTrace;
import com.dlz.comm.util.web.HttpEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.regex.Pattern;

@Slf4j
public class ExceptionUtilTest {
	private static Pattern COMPILE = Pattern.compile("^com\\.dlz");
	@Test
	public void test1(){
			try {
				HttpEnum.GET.send("https://xxx.ddf.ccs");
			}catch (Exception e){
				Throwable throwable = new Throwable(e);
				int size=100000;
				long l = System.currentTimeMillis();
				for (int i = 0; i < size; i++) {
					new ExceptionTrace(throwable, true,COMPILE).getStackTrace(null);
				}
				System.out.println(System.currentTimeMillis() - l);
				l = System.currentTimeMillis();
				for (int i = 0; i < size; i++) {
					new ExceptionTrace(throwable,false ,COMPILE).getStackTrace(null);
				}
				System.out.println(System.currentTimeMillis() - l);

				System.out.println(new ExceptionTrace(throwable, true,COMPILE).getStackTrace(null));
				System.out.println(new ExceptionTrace(throwable, false,COMPILE).getStackTrace(null));
			}
	}
	@Test
	public void tes2(){
		Throwable throwable = new Throwable();
		System.out.println(new ExceptionTrace(throwable, true,COMPILE).getStackTrace(null));
		System.out.println(new ExceptionTrace(throwable, false,COMPILE).getStackTrace(null));
	}

}
