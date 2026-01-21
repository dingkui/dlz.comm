package com.dlz.comm.util.jackson;

import com.dlz.comm.util.DateUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 自定义LocalDateTime反序列化器
 * 
 * 支持多种日期时间格式的解析，包括自动识别格式
 *
 * @author dk
 * @since 2026
 */
public class CustomTimeSerializer<T>  extends StdScalarSerializer<T> {
    private final DateTimeFormatter formatter;

    public CustomTimeSerializer(Class<T> t, DateTimeFormatter formatter) {
        super(t);
        this.formatter = formatter;
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException{
        if(value==null){
            return;
        }
        if(handledType() == LocalDateTime.class){
            gen.writeString(((LocalDateTime)value).format(formatter));
        }else if (handledType() == LocalDate.class){
            gen.writeString(((LocalDate)value).format(formatter));
        }else if (handledType() == LocalTime.class){
            gen.writeString(((LocalTime)value).format(formatter));
        }else if (handledType() == Date.class){
            gen.writeString(DateUtil.getLocalDateTime( (Date) value).format(formatter));
        }
    }
}