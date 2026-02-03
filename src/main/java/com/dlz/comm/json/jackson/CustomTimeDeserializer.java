package com.dlz.comm.json.jackson;

import com.dlz.comm.util.DateUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;

/**
 * 自定义时间类型反序列化器
 */
class CustomTimeDeserializer<T extends Temporal> extends StdScalarDeserializer<T> {
    private final Class<T> type;
    private final DateTimeFormatter formatter;

    public CustomTimeDeserializer(Class<T> type, DateTimeFormatter formatter) {
        super(type);
        this.type = type;
        this.formatter = formatter;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.getCurrentToken();
        String value;

        if (t == JsonToken.VALUE_STRING) {
            value = p.getText().trim();
        } else {
            throw ctxt.mappingException("Expected string value for " + type.getSimpleName() + ", got " + t.asString());
        }

        if (value.isEmpty()) {
            return null;
        }

        try {
            if (type == LocalDateTime.class) {
                return (T) DateUtil.getLocalDateTime(value, formatter.toString());
            } else if (type == LocalDate.class) {
                return (T) LocalDate.parse(value, formatter);
            } else if (type == LocalTime.class) {
                return (T) LocalTime.parse(value, formatter);
            }
        } catch (DateTimeParseException e) {
            throw ctxt.weirdStringException(value, type, "Unrecognized date-time format: " + value);
        }

        throw ctxt.weirdStringException(value, type, "Unsupported temporal type: " + type.getName());
    }
}

/**
 * 自定义时间类型序列化器
 */
class CustomTimeSerializer<T extends Temporal> extends StdScalarSerializer<T> {
    private final Class<T> type;
    private final DateTimeFormatter formatter;

    public CustomTimeSerializer(Class<T> type, DateTimeFormatter formatter) {
        super(type);
        this.type = type;
        this.formatter = formatter;
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        if (type == LocalDateTime.class) {
            gen.writeString(((LocalDateTime) value).format(formatter));
        } else if (type == LocalDate.class) {
            gen.writeString(((LocalDate) value).format(formatter));
        } else if (type == LocalTime.class) {
            gen.writeString(((LocalTime) value).format(formatter));
        }
    }
}