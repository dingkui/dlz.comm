package com.dlz.test.beans;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 2013 2013-9-13 下午4:54:15
 */
@Data
public class TestC extends TestB{
	private String c1;
	private List<Map> c;
}
