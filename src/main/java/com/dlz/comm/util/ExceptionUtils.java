package com.dlz.comm.util;

import java.util.regex.Pattern;

/**
 * <p>Provides utilities for manipulating and examining
 * <code>Throwable</code> objects.</p>
 *
 * @since 1.0
 */
public class ExceptionUtils {

    /**
     * 只显示app相关的堆栈
     * @param throwable
     * @return String
     */
    public static String getStackTrace(final Throwable throwable) {
        return getStackTrace(null,throwable, true,COMPILE);
    }

    /**
     * 只显示app相关的堆栈
     * @param throwable
     */
    public static String getStackTrace(final String msg,final Throwable throwable) {
        return getStackTrace(msg,throwable, true,COMPILE);
    }

    /**
     * 打印堆栈信息
     * @param msg
     * @param throwable
     * @param onlyShowAppLog 是否只显示app相关的堆栈
     * @param marchPattern 匹配的pattern才打印
     */
    public static String getStackTrace(final String msg,final Throwable throwable, boolean onlyShowAppLog,Pattern marchPattern) {
        return new ExceptionTrace(throwable, onlyShowAppLog,marchPattern).getStackTrace(msg);
    }

    private static Pattern COMPILE = Pattern.compile("^com\\.(dlz)");

    public static void setCompiles(String compile) {
        COMPILE = Pattern.compile(compile);
//        COMPILE = Pattern.compile("^"+compile.replaceAll("\\.","\\\\."));
    }

    public static void main(String[] args) {
//        setCompiles("^com\\.(dlz)|(mileworks)");
//        System.out.println(COMPILE.matcher("com.dlz.xxx").find());
//        System.out.println(COMPILE.matcher("com.mileworks.xxx").find());
//        System.out.println(COMPILE.matcher("com.milewxxorks.xxx").find());
    }
}
