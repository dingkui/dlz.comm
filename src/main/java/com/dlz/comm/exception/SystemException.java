package com.dlz.comm.exception;

import com.dlz.comm.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

/**
 * 系统异常类
 * 
 * 继承自BaseException，用于处理系统级异常
 * 
 * @author dingkui
 * @since 2023
 */
public class SystemException extends BaseException {
	/**
	 * 序列化版本UID
	 */
	private static final long serialVersionUID = -5345825923487658213L;
	
	/**
	 * 默认错误码
	 */
	private static int DEFUALT_ERROR_CODE = 6001;

	/**
	 * 构造函数，使用指定消息和异常原因
	 * 
	 * @param message 异常消息
	 * @param cause 异常原因
	 */
	public SystemException(String message, Throwable cause) {
		super(DEFUALT_ERROR_CODE, message, cause);
	}

	/**
	 * 构造函数，使用指定消息
	 * 
	 * @param message 异常消息
	 */
	public SystemException(String message) {
		super(DEFUALT_ERROR_CODE, message);
	}

	/**
	 * 根据异常构建系统异常
	 * 
	 * 根据不同的异常类型提供相应的错误信息
	 * 
	 * @param cause 异常对象
	 * @return 系统异常实例
	 */
	public static SystemException build(Throwable cause) {
		if (cause instanceof Error) {
			new SystemException(cause.getMessage(), cause);
		} else if (cause instanceof IllegalAccessException ||
				cause instanceof IllegalArgumentException ||
				cause instanceof NoSuchMethodException) {
			return new SystemException("无效访问：" + cause.getMessage(), cause);
		} else if (cause instanceof InvocationTargetException) {
			final String message = ((InvocationTargetException) cause).getTargetException().getMessage();
			return new SystemException("无效目标：" + message, cause);
		} else if (cause instanceof RuntimeException) {
			return new SystemException("运行异常：" + cause.getMessage(), cause);
		} else if (cause instanceof InterruptedException) {
			Thread.currentThread().interrupt();
			return new SystemException("中断异常：" + cause.getMessage(), cause);
		}
		return new SystemException(cause.getMessage(), cause);
	}
	
	/**
	 * 构建带有消息和异常原因的系统异常
	 * 
	 * @param message 异常消息
	 * @param cause 异常原因
	 * @return 系统异常实例
	 */
	public static SystemException build(String message, Throwable cause) {
		return new SystemException(message, cause);
	}
	
	/**
	 * 构建带有消息的系统异常
	 * 
	 * @param message 异常消息
	 * @return 系统异常实例
	 */
	public static SystemException build(String message) {
		return new SystemException(message);
	}

	/**
	 * 断言布尔表达式为真
	 * 
	 * 如果表达式为假则抛出异常
	 *
	 * @param expression 布尔表达式
	 * @param message 异常消息
	 */
	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new SystemException(message);
		}
	}
	
	/**
	 * 断言布尔表达式为真
	 * 
	 * 如果表达式为假则抛出异常
	 *
	 * @param expression 布尔表达式
	 * @param c 异常消息提供者
	 */
	public static void isTrue(boolean expression, Supplier<String> c) {
		if (!expression) {
			throw new SystemException(c.get());
		}
	}

	/**
	 * 断言对象不为null
	 * 
	 * 如果对象为null则抛出异常
	 *
	 * @param object 待检查的对象
	 * @param message 异常消息
	 */
	public static void notNull(Object object, String message) {
		isTrue(object != null, message);
	}

	/**
	 * 断言对象不为null
	 * 
	 * 如果对象为null则抛出异常
	 *
	 * @param object 待检查的对象
	 * @param c 异常消息提供者
	 */
	public static void notNull(Object object, Supplier<String> c) {
		isTrue(object != null, c);
	}

	/**
	 * 断言对象不为空
	 * 
	 * 如果对象为空则抛出异常
	 *
	 * @param value 待检查的对象
	 * @param message 异常消息
	 */
	public static void notEmpty(Object value, String message) {
		isTrue(!StringUtils.isEmpty(value), message);
	}
	
	/**
	 * 断言对象不为空
	 * 
	 * 如果对象为空则抛出异常
	 *
	 * @param value 待检查的对象
	 * @param c 异常消息提供者
	 */
	public static void notEmpty(Object value, Supplier<String> c) {
		isTrue(!StringUtils.isEmpty(value), c);
	}

}