package com.dlz.comm.util.encry;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class ByteUtil {
    // 从文本文件对象中读取内容并转换为字符数组
    public static char[] readChars(File file) {
        CharArrayWriter caw = new CharArrayWriter();
        try {
            Reader fr = new FileReader(file);
            Reader in = new BufferedReader(fr);
            int count = 0;
            char[] buf = new char[16384];
            while ((count = in.read(buf)) != -1) {
                if (count > 0)
                    caw.write(buf, 0, count);
            }
            in.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return caw.toCharArray();
    }

    // 从字符串对象中读取内容并转换为字符数组
    public static char[] readChars(String string) {
        CharArrayWriter caw = new CharArrayWriter();
        try {
            Reader sr = new StringReader(string.trim());
            Reader in = new BufferedReader(sr);
            int count = 0;
            char[] buf = new char[16384];
            while ((count = in.read(buf)) != -1) {
                if (count > 0)
                    caw.write(buf, 0, count);
            }
            in.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return caw.toCharArray();
    }

    // 从二进制文件对象中读取内容并转换为字节数组
    public static byte[] readBytes(File file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            InputStream fis = new FileInputStream(file);
            InputStream is = new BufferedInputStream(fis);
            int count = 0;
            byte[] buf = new byte[16384];
            while ((count = is.read(buf)) != -1) {
                if (count > 0)
                    baos.write(buf, 0, count);
            }
            is.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return baos.toByteArray();
    }

    final static String CHARSET_UTF8 = "UTF-8";

    public static byte[] getBytes(String str, String charset) {
        try {
            return str.getBytes(charset);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getBytes(long num) {
        return getBytes(String.valueOf(num));
    }

    public static byte[] getBytes(String str) {
        return getBytes(str, CHARSET_UTF8);
    }

    public static String getStr(byte[] bytes, String charset) {
        try {
            return new String(bytes, charset);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getStr(byte[] bytes) {
        return getStr(bytes, CHARSET_UTF8);
    }

    // 写字节数组内容到二进制文件
    public static void writeBytes(File file, byte[] data) {
        try {
            OutputStream fos = new FileOutputStream(file);
            OutputStream os = new BufferedOutputStream(fos);
            os.write(data);
            os.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    // 写字符数组内容到文本文件
    public static void writeChars(File file, char[] data) {
        try {
            Writer fos = new FileWriter(file);
            Writer os = new BufferedWriter(fos);
            os.write(data);
            os.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}