package com.dlz.comm.consts;

import com.dlz.comm.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

/**
 * 字符集常量接口
 * 
 * 定义常用的字符集常量及字符集转换方法
 * 
 * @author dingkui
 * @since 2023
 */
public interface Charsets {
    /** Content-Type 头部名称 */
    String CONTENT_TYPE_NAME = "Content-type";

    /** ISO-8859-1 字符集 */
    Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;
    /** ISO-8859-1 字符集名称 */
    String ISO_8859_1_NAME = ISO_8859_1.name();

    /** GBK 字符集名称 */
    String GBK_NAME = "GBK";
    /** GBK 字符集 */
    Charset GBK = Charset.forName(GBK_NAME);

    /** UTF-8 字符集 */
    Charset UTF_8 = StandardCharsets.UTF_8;
    /** UTF-8 字符集名称 */
    String UTF_8_NAME = UTF_8.name();

    /**
     * 转换为Charset对象
     *
     * @param charsetName 字符集名称，为空则返回默认字符集
     * @return Charset对象
     * @throws UnsupportedCharsetException 编码不支持异常
     */
    static Charset charset(String charsetName) throws UnsupportedCharsetException {
        return StringUtils.isBlank(charsetName) ? Charset.defaultCharset() : Charset.forName(charsetName);
    }


    /**
     * 编码转换
     *
     * @param value 待转换的字符串
     * @param inputCharset 输入字符串的字符集
     * @param outCharset 输出字符串的字符集
     * @return 转换后的字符串
     * @throws UnsupportedCharsetException 编码不支持异常
     * @throws UnsupportedEncodingException 不支持的编码异常
     */
    static String convert(String value, String inputCharset, String outCharset) throws UnsupportedCharsetException, UnsupportedEncodingException {
        return new String(value.getBytes(inputCharset), outCharset);
    }

    /**
     * 将指定字符集的字符串转换为UTF-8编码
     *
     * @param value 待转换的字符串
     * @param inputCharset 输入字符串的字符集
     * @return 转换为UTF-8编码的字符串
     * @throws UnsupportedCharsetException 编码不支持异常
     * @throws UnsupportedEncodingException 不支持的编码异常
     */
    static String toUtf8(String value, String inputCharset) throws UnsupportedCharsetException, UnsupportedEncodingException {
        return new String(value.getBytes(inputCharset), UTF_8);
    }

    /**
     * 将ISO-8859-1编码的字符串转换为UTF-8编码
     *
     * @param value ISO-8859-1编码的字符串
     * @return 转换为UTF-8编码的字符串
     * @throws UnsupportedCharsetException 编码不支持异常
     */
    static String iso2Utf8(String value) throws UnsupportedCharsetException {
        return new String(value.getBytes(ISO_8859_1), UTF_8);
    }

    /**
     * 将GBK编码的字符串转换为UTF-8编码
     *
     * @param value GBK编码的字符串
     * @return 转换为UTF-8编码的字符串
     * @throws UnsupportedCharsetException 编码不支持异常
     */
    static String gbk2Utf8(String value) throws UnsupportedCharsetException {
        return new String(value.getBytes(GBK), UTF_8);
    }
}