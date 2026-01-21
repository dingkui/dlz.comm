package com.dlz.comm.util.jackson;

import com.dlz.comm.util.DateUtil;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Java 8 时间默认序列化模块
 * 
 * 用于处理Java 8 时间类型（LocalDateTime、LocalDate、LocalTime）的序列化和反序列化
 *
 * @author dk
 * @since 2023
 */
public class DlzJavaTimeModule extends SimpleModule {
	/**
	 * 单例实例
	 */
	public static final DlzJavaTimeModule INSTANCE = new DlzJavaTimeModule();

	/**
	 * 私有构造函数，初始化时间类型序列化器和反序列化器
	 */
	private DlzJavaTimeModule() {
		super();
		// 添加LocalDateTime的反序列化器和序列化器
		this.addDeserializer(LocalDateTime.class, new CustomTimeDeserializer(LocalDateTime.class,DateUtil.DATETIME.formatter));
		// 添加LocalDate的反序列化器和序列化器
		this.addDeserializer(LocalDate.class, new CustomTimeDeserializer(LocalDate.class,DateUtil.DATE.formatter));
		// 添加LocalTime的反序列化器和序列化器
		this.addDeserializer(LocalTime.class, new CustomTimeDeserializer(LocalTime.class,DateUtil.TIME.formatter));
		// 添加LocalDateTime的序列化器
		this.addSerializer(LocalDateTime.class, new CustomTimeSerializer(LocalDateTime.class,DateUtil.DATETIME.formatter));
		// 添加LocalDate的序列化器
		this.addSerializer(LocalDate.class, new CustomTimeSerializer(LocalDate.class,DateUtil.DATE.formatter));
		// 添加LocalTime的序列化器
		this.addSerializer(LocalTime.class, new CustomTimeSerializer(LocalTime.class,DateUtil.TIME.formatter));
	}

}