package com.dlz.comm.util.encry;


import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * @version 1.3
 */
public class Base64 {
	// 编码文件对象所指的文件
	public static char[] encode(File file) {
		if (!file.exists()) {
			System.err.println("错误:文件不存在！");
			return null;
		}
		return encode(ByteUtil.readBytes(file));
	}
	public static char[] encode(String str,String charset){
		return encode(ByteUtil.getBytes(str,charset));
	}
	public static char[] encode(String str){
		return encode(ByteUtil.getBytes(str));
	}
	public static String encode2Str(byte[] data){
		return new String(encode(data));
	}	
	public static String encode2Str(String str) {
		return new String(encode(ByteUtil.getBytes(str)));
	}
	
	public static String decode2Str(String str,String charset ){
		return ByteUtil.getStr(decode(str),charset);
	}
	public static String decode2Str(String str){
		return ByteUtil.getStr(decode(str));
	}
	public static byte[] decode(String str){
		return decode(str.toCharArray());
	}
	
	// 编码传入的字节数组，输出编码后的字符数组
	static public char[] encode(byte[] data) {
		char[] out = new char[((data.length + 2) / 3) * 4];

		// 
		// 对字节进行Base64编码,每三个字节转化为4个字符.
		// 输出总是能被4整除的偶数个字符
		// 
		for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
			boolean quad = false;
			boolean trip = false;

			int val = (0xFF & (int) data[i]);
			val <<= 8;
			if ((i + 1) < data.length) {
				val |= (0xFF & (int) data[i + 1]);
				trip = true;
			}
			val <<= 8;
			if ((i + 2) < data.length) {
				val |= (0xFF & (int) data[i + 2]);
				quad = true;
			}
			out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];
			val >>= 6;
			out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];
			val >>= 6;
			out[index + 1] = alphabet[val & 0x3F];
			val >>= 6;
			out[index + 0] = alphabet[val & 0x3F];
		}
		return out;
	}
	
	static public byte[] decode(char[] data) {
		// 程序中有判断如果有回车、空格等非法字符，则要去掉这些字符
		// 这样就不会计算错误输出的内容
		int tempLen = data.length;
		for (int ix = 0; ix < data.length; ix++) {
			if ((data[ix] > 255) || codes[data[ix]] < 0)
				--tempLen; // 去除无效的字符
		}

		// 计算byte的长度
		// -- 每四个有效字符输出三个字节的内容
		// -- 如果有额外的3个字符，则还要加上2个字节,
		// 或者如果有额外的2个字符，则要加上1个字节

		int len = (tempLen / 4) * 3;
		if ((tempLen % 4) == 3)
			len += 2;
		if ((tempLen % 4) == 2)
			len += 1;

		byte[] out = new byte[len];

		int shift = 0;
		int accum = 0;
		int index = 0;

		// 一个一个字符地解码（注意用的不是tempLen的值进行循环）
		for (int ix = 0; ix < data.length; ix++) {
			int value = (data[ix] > 255) ? -1 : codes[data[ix]];

			if (value >= 0) // 忽略无效字符
			{
				accum <<= 6;
				shift += 6;
				accum |= value;
				if (shift >= 8) {
					shift -= 8;
					out[index++] = (byte) ((accum >> shift) & 0xff);
				}
			}
		}

		// 如果数组长度和实际长度不符合，那么抛出错误
		if (index != out.length) {
			throw new Error("数据长度不一致(实际写入了 " + index + "字节，但是系统指示有"
					+ out.length + "字节)");
		}

		return out;
	}
	
	static public ByteArrayInputStream decodeAsInputSream(String base64Str) {
		return new ByteArrayInputStream(decode(base64Str.toCharArray()));
	}

	// 
	// 用于编码的字符
	// 
	static private char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="
			.toCharArray();

	// 
	// 用于解码的字节（0-255）
	// 
	static private byte[] codes = new byte[256];
	static {
		for (int i = 0; i < 256; i++)
			codes[i] = -1;
		for (int i = 'A'; i <= 'Z'; i++)
			codes[i] = (byte) (i - 'A');
		for (int i = 'a'; i <= 'z'; i++)
			codes[i] = (byte) (26 + i - 'a');
		for (int i = '0'; i <= '9'; i++)
			codes[i] = (byte) (52 + i - '0');
		codes['+'] = 62;
		codes['/'] = 63;
	}

}