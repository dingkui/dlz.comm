package com.dlz.comm.exception;

/**
 * SDK基础异常类
 *
 * 作为所有自定义异常的基类，提供错误码、错误信息等通用属性
 *
 * @author dingkui
 * @since 2023
 */
public class BaseException extends RuntimeException {
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = -5345825923487658213L;

    /**
     * 错误码，参见Errors类
     */
    private int code;

    /**
     * 错误码对应的错误信息
     */
    private String info;

    /**
     * 错误消息
     */
    private String msg;

    /**
     * 构造函数，使用指定错误码和异常对象
     *
     * @param code 错误码
     * @param exception 异常对象
     */
    public BaseException(int code, Throwable exception) {
        this(code, exception.getMessage(), exception);
    }

    /**
     * 构造函数，使用指定错误码、消息和原因
     *
     * @param code 错误码
     * @param message 错误消息
     * @param cause 异常原因
     */
    public BaseException(int code, String message, Throwable cause) {
        this(code, message);
        if (cause != null) {
            this.addSuppressed(cause);
        }
    }

    /**
     * 构造函数，使用指定错误码和消息
     *
     * @param code 错误码
     * @param message 错误消息
     */
    public BaseException(int code, String message) {
        super(code + ":"  + (message == null ? "" : "[" + message + "]"));
        this.code = code;
        this.info = ExceptionErrors.getInfo(code);
        this.msg = message;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取错误信息
     *
     * @return 错误信息
     */
    public String getInfo() {
        return info;
    }

    /**
     * 获取错误消息
     *
     * @return 错误消息
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 检查错误码是否与指定值相等
     *
     * @param code 要比较的错误码
     * @return 如果错误码相等则返回true，否则返回false
     */
    public boolean is(int code) {
        return this.code == code;
    }

}