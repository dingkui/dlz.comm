package com.dlz.comm.util.jackson;

import com.dlz.comm.util.DateUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 自定义LocalDateTime反序列化器
 * 
 * 支持多种日期时间格式的解析，包括自动识别格式
 *
 * @author dk
 * @since 2026
 */
public class CustomTimeDeserializer<T>  extends StdScalarDeserializer<T> {
    private final DateTimeFormatter formatter;

    public CustomTimeDeserializer(Class<T> t, DateTimeFormatter formatter) {
        super(t);
        this.formatter = formatter;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.getCurrentToken();
        String value;
        
        if (t == JsonToken.VALUE_STRING) {
            value = p.getText().trim();
        } else {
            throw ctxt.mappingException("Expected string value for LocalDateTime, got " + t.asString());
        }

        if (value.isEmpty()) {
            return null;
        }

        LocalDateTime localDateTime = DateUtil.getLocalDateTime(value);
        T result = null;
        if(localDateTime==null){
            // 如果自动识别失败，尝试使用DATETIME格式
            try {
                localDateTime = LocalDateTime.parse(value, formatter);
            } catch (DateTimeParseException e) {
                // 尝试其他常见格式
                try {
                    // 尝试ISO格式
                    localDateTime =  LocalDateTime.parse(value);
                } catch (DateTimeParseException ex) {
                    throw ctxt.weirdStringException(value, LocalDateTime.class,
                            "Unrecognized date-time format: " + value);
                }
            }
        }
        if(localDateTime!=null){
            // 使用DateUtil的自动识别功能
            if(handledType() == LocalDateTime.class){
                return  (T) localDateTime;
            }else if (handledType() == LocalDate.class){
                return (T) localDateTime.toLocalDate();
            }else if (handledType() == LocalTime.class){
                return (T) DateUtil.getDate(localDateTime);
            }else if (handledType() == java.util.Date.class){
                return (T) DateUtil.getDate(localDateTime);
            }
        }
        return null;
    }
}