package com.dlz.comm.util.jackson;

import com.dlz.comm.util.DateUtil;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

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
		super(PackageVersion.VERSION);
		// 添加LocalDateTime的反序列化器和序列化器
		this.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateUtil.DATETIME.formatter));
		// 添加LocalDate的反序列化器和序列化器
		this.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateUtil.DATE.formatter));
		// 添加LocalTime的反序列化器和序列化器
		this.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateUtil.TIME.formatter));
		// 添加LocalDateTime的序列化器
		this.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateUtil.DATETIME.formatter));
		// 添加LocalDate的序列化器
		this.addSerializer(LocalDate.class, new LocalDateSerializer(DateUtil.DATE.formatter));
		// 添加LocalTime的序列化器
		this.addSerializer(LocalTime.class, new LocalTimeSerializer(DateUtil.TIME.formatter));
	}

}