package com.dlz.comm.util.encry;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {
	public static String md5(byte[] input){
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}	
		return byte2hex(md.digest(input));
	}
	public static String md5(String input){
		return md5(ByteUtil.getBytes(input));
	}
	public static String md5(String input,String charset){
		return md5(ByteUtil.getBytes(input,charset));
	}
	public static String md5(File file){
		if (!file.exists()) {
			throw new RuntimeException("文件"+file.getAbsolutePath()+"不存在！");
		}
		return md5(ByteUtil.readBytes(file));
	}
	
	public static String md5(long input){
		return md5(ByteUtil.getBytes(input));
	}
	
	private static String byte2hex(byte[] b) {
		StringBuffer hs = new StringBuffer();
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs.append("0").append(stmp);
			else
				hs.append(stmp);
		}
		return hs.toString();
	}

}