package com.dlz.test.comm.util;

import com.dlz.comm.util.StringUtils;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.regex.Pattern;

public class MessageFormatTest {
	private static Pattern msgPattern = Pattern.compile("\\{[\\w]*[^\\d]+[\\w]*\\}");
	static String formatMsg(Object message, Object... paras) {
		String msg = message.toString();
		if (msgPattern.matcher(msg).find()) {
			return msg;
		}
		return MessageFormat.format(msg, paras);
	}
	/**
	 * 格式化文本, {} 表示占位符<br>
	 * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
	 * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
	 * 例：<br>
	 * 通常使用：formatMsg("this is {} for {}", "a", "b") -> this is a for b<br>
	 * 指定下标： formatMsg("this is {1} for {}", "a", "b") -> this is b for b<br>
	 * 说明+明确下标： formatMsg("this is {b1} for {}", "a", "b") -> this is b for b<br>
	 * 说明+明确下标： formatMsg("this is {xx1x_1} for {}", "a", "b") -> this is b for b<br>
	 * 下标无效： formatMsg("this is {9} for {}", "a", "b") -> this is {9} for b<br>
	 * 下标无效： formatMsg("this is {} for {} and {}", "a", "b") -> this is a for b and {}<br>
	 *
	 * @return 格式化后的文本
	 */
	@Test
	public void stringUtilsTest() {
		System.out.println(StringUtils.formatMsg("this is {} for {}", "a", "b"));// -> this is a for b<br>
		System.out.println(StringUtils.formatMsg("this is {1} for {}", "a", "b"));//-> this is b for b<br>
		System.out.println(StringUtils.formatMsg("this is {b1} for {}", "a", "b"));// -> this is b for b<br>
		System.out.println(StringUtils.formatMsg("this is {xx1x_1} for {}", "a", "b"));// -> this is b for b<br>
		System.out.println(StringUtils.formatMsg("this is {9} for {}", "a", "b"));// -> this is {9} for b<br>
		System.out.println(StringUtils.formatMsg("this is {} for {} and {}", "a", "b"));// -> this is a for b and {}<br>
	}
}
