package com.dlz.comm.util;

import com.dlz.comm.exception.SystemException;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalQuery;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * 日期工具类
 * 
 * 提供便捷的日期格式化、解析、转换等功能
 * 
 * @author dingkui
 * @since 2023
 */
@Slf4j
public class DateUtil {
    /** 年份格式模式 */
    public static final String PATTERN_YEAR = "yyyy";
    /** 年月格式模式 */
    public static final String PATTERN_MONTH = "yyyy-MM";
    /** 简化年月格式模式 */
    public static final String PATTERN_MONTH_MINI = "yyyyMM";
    /** 日期格式模式 */
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    /** 简化日期格式模式 */
    public static final String PATTERN_DATE_MINI = "yyyyMMdd";
    /** 时间格式模式 */
    public static final String PATTERN_TIME = "HH:mm:ss";
    /** 日期时间格式模式 */
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    /** 简化日期时间格式模式 */
    public static final String PATTERN_DATETIME_MINI = "yyyyMMddHHmmss";
    /** UTC格式模式 */
    public static final String PATTERN_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /** 年份格式化器 */
    public static final DateFormat YEAR = DateFormat.of(PATTERN_YEAR);
    /** 年月格式化器 */
    public static final DateFormat MONTH = DateFormat.of(PATTERN_MONTH);
    /** 简化年月格式化器 */
    public static final DateFormat MONTH_MINI = DateFormat.of(PATTERN_MONTH_MINI);
    /** 日期格式化器 */
    public static final DateFormat DATE = DateFormat.of(PATTERN_DATE);
    /** 简化日期格式化器 */
    public static final DateFormat DATE_MINI = DateFormat.of(PATTERN_DATE_MINI);
    /** 时间格式化器 */
    public static final DateFormat TIME = DateFormat.of(PATTERN_TIME);
    /** 日期时间格式化器 */
    public static final DateFormat DATETIME = DateFormat.of(PATTERN_DATETIME);
    /** 简化日期时间格式化器 */
    public static final DateFormat DATETIME_MINI = DateFormat.of(PATTERN_DATETIME_MINI);
    /** UTC格式化器 */
    public static final DateFormat UTC = DateFormat.of(PATTERN_UTC, TimeZone.getTimeZone("UTC"));

    /** 格式化器映射表 */
    public static Map<String, DateFormat> formatterMap = new HashMap<>();
    static {
        formatterMap.put(PATTERN_YEAR, YEAR);
        formatterMap.put(PATTERN_MONTH, MONTH);
        formatterMap.put(PATTERN_MONTH_MINI, MONTH_MINI);
        formatterMap.put(PATTERN_DATE, DATE);
        formatterMap.put(PATTERN_DATE_MINI, DATE_MINI);
        formatterMap.put(PATTERN_TIME, TIME);
        formatterMap.put(PATTERN_DATETIME, DATETIME);
        formatterMap.put(PATTERN_DATETIME_MINI, DATETIME_MINI);
        formatterMap.put(PATTERN_UTC, UTC);
    }

    /** 日期格式转换规则数组 */
    private final static VAL<String, Pattern>[] date_trans = new VAL[]{
            VAL.of("yyyy-MM-dd HH:mm:ss", Pattern.compile("^\\d{4}-[0,1]?\\d-[0-3]?\\d \\d{2}:\\d{2}:\\d{2}.*")),
            VAL.of("yyyy-MM-dd", Pattern.compile("^\\d{4}-\\d{1,2}-\\d{1,2}$")),
            VAL.of("yyyy-MM", Pattern.compile("^\\d{4}-\\d{1,2}$")),
            VAL.of("yyyy-MM-dd HH:mm", Pattern.compile("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}$")),
            VAL.of("yyyy年MM月dd日 HH时mm分ss秒", Pattern.compile("^\\d{4}年[0,1]?\\d月[0-3]?\\d日 \\d{2}时\\d{2}分\\d{2}秒"))
    };
    // 提取为静态常量，避免重复编译
    /** 时间格式1的正则表达式 */
    private static final Pattern TIME_PATTERN_1 = Pattern.compile("^\\d{1,2}:\\d{1,2}$");
    /** 时间格式2的正则表达式 */
    private static final Pattern TIME_PATTERN_2 = Pattern.compile("^\\d{1,2}:\\d{1,2}:\\d{1,2}$");
    /** UTC格式的正则表达式 */
    private static final Pattern UTC_PATTERN = Pattern.compile("^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{3}Z$");

    /**
     * 获取指定格式的日期格式化器
     * 
     * @param pattern 日期格式模式
     * @return 日期格式化器
     */
    public static DateFormat formatter(String pattern) {
        final DateFormat dateFormatter = formatterMap.get(pattern);
        return dateFormatter != null ? dateFormatter : DateFormat.of(pattern);
    }
    
    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date now() {
        return new Date();
    }

    /**
     * 格式化日期为指定格式的字符串
     *
     * @param date 待格式化的日期
     * @param pattern 日期格式模式
     * @return 格式化后的字符串
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        return formatter(pattern).format(date);
    }

    /**
     * 格式化当前时间为指定格式的字符串
     *
     * @param format 日期格式模式
     * @return 格式化后的字符串
     */
    public static String formatNow(final String format) {
        return formatter(format).formatNow();
    }


    /**
     * 获取指定日期的字符串表示，格式为YYYY-MM-DD
     *
     * @param date 待格式化的日期
     * @return 格式化后的字符串
     */
    public static String getDateStr(Date date) {
        return DATE.format(date);
    }

    /**
     * 获取当前日期的字符串表示，格式为YYYY-MM-DD
     *
     * @return 格式化后的字符串
     */
    public static String getDateStr() {
        return DATE.formatNow();
    }

    /**
     * 获取指定日期时间的字符串表示，格式为YYYY-MM-DD HH:mm:ss
     *
     * @param date 待格式化的日期
     * @return 格式化后的字符串
     */
    public static String getDateTimeStr(Date date) {
        return DATETIME.format(date);
    }

    /**
     * 获取当前日期时间的字符串表示，格式为YYYY-MM-DD HH:mm:ss
     *
     * @return 格式化后的字符串
     */
    public static String getDateTimeStr() {
        return DATETIME.formatNow();
    }

    /**
     * 将UTC格式的字符串转换为日期
     *
     * @param dateVal UTC格式的日期字符串
     * @return 解析后的日期，解析失败返回null
     */
    public static Date parseUTCDate(String dateVal) {
        try {
            return UTC.parse(dateVal);
        } catch (Exception e) {
            log.error("parseUTCDate error! input:{}", dateVal);
            log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }
    
    /**
     * 将字符串转换为指定类型的时间对象
     *
     * @param dateStr 时间字符串
     * @param pattern 日期格式模式
     * @param query 时间查询器
     * @param <T> 时间类型
     * @return 解析后的时间对象
     */
    public static <T> T parse(String dateStr, String pattern, TemporalQuery<T> query) {
        return DateTimeFormatter.ofPattern(pattern).parse(dateStr, query);
    }
    
    /**
     * 将字符串转换为日期
     *
     * @param dateStr 时间字符串
     * @param pattern 日期格式模式
     * @return 解析后的日期，解析失败返回null
     */
    private static Date parse(String dateStr, String pattern) {
        try {
            return DateUtil.formatter(pattern).parse(dateStr);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }
    
    /**
     * 将字符串转换为LocalDateTime
     *
     * @param dateStr 时间字符串
     * @param pattern 日期格式模式
     * @return 解析后的LocalDateTime，解析失败返回null
     */
    private static LocalDateTime parseLocalDateTime(String dateStr, String pattern) {
        try {
            return DateUtil.formatter(pattern).parse2LocalDate(dateStr);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    /**
     * 将字符串自动识别并转换为日期
     *
     * @param input 输入的日期字符串
     * @return 解析后的日期，解析失败返回null
     */
    public static Date getDate(String input) {
        if (input == null) {
            return null;
        }
        String input2 = input.replaceAll("/", "-").replaceAll("\"", "");
        for (int i = 0; i < date_trans.length; i++) {
            if (date_trans[i].v2.matcher(input2).matches()) {
                return parse(input2, date_trans[i].v1);
            }
        }
        if (TIME_PATTERN_1.matcher(input2).matches()) {
            return DATETIME.parse(DateUtil.getDateStr() + " " + input2 + ":00");
        } else if (TIME_PATTERN_2.matcher(input2).matches()) {
            return DATETIME.parse(DateUtil.getDateStr() + " " + input2);
        } else if (UTC_PATTERN.matcher(input2).matches()) {
            return parseUTCDate(input2);
        }
        log.error("转换日期格式未识别：" + input);
        return null;
    }

    /**
     * 将字符串按指定格式转换为日期
     *
     * @param input 输入的日期字符串
     * @param format 日期格式模式，如果为null则自动识别
     * @return 解析后的日期，解析失败返回null
     */
    public static Date getDate(String input, String format) {
        if (input == null) {
            return null;
        }
        if (format != null) {
            return parse(input, format);
        }
        return getDate(input);
    }
    
    /**
     * 将LocalDateTime转换为Date
     *
     * @param dateTime LocalDateTime对象
     * @return Date对象
     */
    public static Date getDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    /**
     * 将LocalDate转换为Date
     *
     * @param localDate LocalDate对象
     * @return Date对象
     */
    public static Date getDate(final LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将字符串按指定格式转换为LocalDateTime
     *
     * @param input 输入的日期字符串
     * @param format 日期格式模式，如果为null则自动识别
     * @return 解析后的LocalDateTime，解析失败返回null
     */
    public static LocalDateTime getLocalDateTime(String input, String format) {
        if (input == null) {
            return null;
        }
        if (format != null) {
            return formatter(format).parse2LocalDate(input);
        }
        return getLocalDateTime(input);
    }

    /**
     * 将Date转换为LocalDateTime
     *
     * @param input Date对象
     * @return LocalDateTime对象
     */
    public static LocalDateTime getLocalDateTime(Date input) {
        if (input == null) {
            return null;
        }
        return input.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    
    /**
     * 将字符串自动识别并转换为LocalDateTime
     *
     * @param input 输入的日期字符串
     * @return 解析后的LocalDateTime，解析失败返回null
     */
    public static LocalDateTime getLocalDateTime(String input) {
        if (input == null) {
            return null;
        }
        String input2 = input.replaceAll("/", "-").replaceAll("\"", "");
        for (int i = 0; i < date_trans.length; i++) {
            if (date_trans[i].v2.matcher(input2).matches()) {
                return DateUtil.formatter(date_trans[i].v1).parse2LocalDate(input2);
            }
        }
        if (TIME_PATTERN_1.matcher(input2).matches()) {
            return DateUtil.DATETIME.parse2LocalDate(DateUtil.getDateStr() + " " + input2 + ":00");
        } else if (TIME_PATTERN_2.matcher(input2).matches()) {
            return DateUtil.DATETIME.parse2LocalDate(DateUtil.getDateStr() + " " + input2);
        } else if (UTC_PATTERN.matcher(input2).matches()) {
            return parseUTCLocalDateTime(input2);
        }
        log.error("转换日期格式未识别：" + input);
        return null;
    }
    
    /**
     * 将符合 ISO 8601 格式的 UTC 时间字符串转换为 LocalDateTime（使用系统默认时区）
     *
     * @param utcTimeStr UTC 时间字符串，如 "2023-12-25T14:30:00.000Z"
     * @return 解析后的 LocalDateTime 对象，失败返回 null
     */
    public static LocalDateTime parseUTCLocalDateTime(String utcTimeStr) {
        try {
            if(utcTimeStr==null){
                return null;
            }
            // 将字符串解析为 Instant（UTC 时间）
            Instant instant = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(utcTimeStr));
            // 转换为系统默认时区的 LocalDateTime
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        } catch (DateTimeParseException e) {
            log.warn("parseUTCLocalDateTime error! input:{} ", utcTimeStr);
            log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

}