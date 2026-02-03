package com.dlz.comm.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * DateUtil 工具类单元测试
 * 
 * 测试日期格式化、解析、转换等核心功能
 * 
 * @author test
 */
public class DateUtilTest {
    
    private static final Logger log = LoggerFactory.getLogger(DateUtilTest.class);
    
    // 测试数据
    private static final String TEST_DATE_STR = "2023-12-25";
    private static final String TEST_DATETIME_STR = "2023-12-25 14:30:45";
    private static final String TEST_UTC_STR = "2023-12-25T14:30:45.123Z";
    private static final String TEST_TIME_STR1 = "14:30";
    private static final String TEST_TIME_STR2 = "14:30:45";
    
    @Test
    public void testNow() {
        Date now = DateUtil.now();
        assertNotNull("当前时间不应为null", now);
        log.info("当前时间: {}", now);
    }
    
    @Test
    public void testFormat_Date_String() {
        Date date = DateUtil.getDate(TEST_DATE_STR);
        String formatted = DateUtil.format(date, DateUtil.PATTERN_DATE);
        assertEquals("日期格式化应该正确", TEST_DATE_STR, formatted);
        
        // 测试null输入
        String nullResult = DateUtil.format(null, DateUtil.PATTERN_DATE);
        assertEquals("null日期应返回空字符串", "", nullResult);
    }
    
    @Test
    public void testFormatNow() {
        String nowStr = DateUtil.formatNow(DateUtil.PATTERN_DATE);
        assertNotNull("当前时间格式化不应为null", nowStr);
        assertTrue("格式化结果应该是有效的日期格式", nowStr.matches("\\d{4}-\\d{2}-\\d{2}"));
        log.info("当前日期: {}", nowStr);
    }
    
    @Test
    public void testGetDateStr() {
        Date date = DateUtil.getDate(TEST_DATE_STR);
        String dateStr = DateUtil.getDateStr(date);
        assertEquals("获取日期字符串应该正确", TEST_DATE_STR, dateStr);
        
        // 测试无参数版本
        String currentDateStr = DateUtil.getDateStr();
        assertNotNull("无参数版本不应返回null", currentDateStr);
        assertTrue("应该是有效的日期格式", currentDateStr.matches("\\d{4}-\\d{2}-\\d{2}"));
    }
    
    @Test
    public void testGetDateTimeStr() {
        Date date = DateUtil.getDate(TEST_DATETIME_STR);
        String dateTimeStr = DateUtil.getDateTimeStr(date);
        assertEquals("获取日期时间字符串应该正确", TEST_DATETIME_STR, dateTimeStr);
        
        // 测试无参数版本
        String currentDateTimeStr = DateUtil.getDateTimeStr();
        assertNotNull("无参数版本不应返回null", currentDateTimeStr);
        assertTrue("应该是有效的日期时间格式", 
                  currentDateTimeStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }
    
    @Test
    public void testParseUTCDate() {
        Date date = DateUtil.parseUTCDate(TEST_UTC_STR);
        assertNotNull("UTC日期解析不应为null", date);
        
        // 验证解析结果
        String formatted = DateUtil.format(date, DateUtil.PATTERN_UTC);
        log.info("UTC解析结果: {} -> {}", TEST_UTC_STR, formatted);
        
        // 测试无效格式
        Date invalidDate = DateUtil.parseUTCDate("invalid-date");
        assertNull("无效UTC日期应返回null", invalidDate);
    }
    
    @Test
    public void testGetDate_String() {
        // 测试标准日期格式
        Date date1 = DateUtil.getDate(TEST_DATE_STR);
        assertNotNull("标准日期格式解析不应为null", date1);
        
        // 测试日期时间格式
        Date date2 = DateUtil.getDate(TEST_DATETIME_STR);
        assertNotNull("日期时间格式解析不应为null", date2);
        
        // 测试时间格式1 (HH:mm)
        Date date3 = DateUtil.getDate(TEST_TIME_STR1);
        assertNotNull("时间格式(HH:mm)解析不应为null", date3);
        
        // 测试时间格式2 (HH:mm:ss)
        Date date4 = DateUtil.getDate(TEST_TIME_STR2);
        assertNotNull("时间格式(HH:mm:ss)解析不应为null", date4);
        
        // 测试UTC格式
        Date date5 = DateUtil.getDate(TEST_UTC_STR);
        assertNotNull("UTC格式解析不应为null", date5);
        
        // 测试null输入
        Date nullDate = DateUtil.getDate((String)null);
        assertNull("null输入应返回null", nullDate);
        
        // 测试无效格式
        Date invalidDate = DateUtil.getDate("invalid-format");
        assertNull("无效格式应返回null", invalidDate);
    }
    
    @Test
    public void testGetDate_String_String() {
        // 测试指定格式解析
        Date date = DateUtil.getDate(TEST_DATE_STR, DateUtil.PATTERN_DATE);
        assertNotNull("指定格式解析不应为null", date);
        
        // 测试null格式（自动识别）
        Date autoDate = DateUtil.getDate(TEST_DATE_STR, null);
        assertNotNull("null格式应自动识别", autoDate);
        
        // 测试null输入
        Date nullInput = DateUtil.getDate(null, DateUtil.PATTERN_DATE);
        assertNull("null输入应返回null", nullInput);
        
        // 测试无效格式
        Date invalidFormatDate = DateUtil.getDate(TEST_DATE_STR, "invalid-pattern");
        assertNull("无效格式应返回null", invalidFormatDate);
    }
    
    @Test
    public void testGetDate_LocalDateTime() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 12, 25, 14, 30, 45);
        Date date = DateUtil.getDate(localDateTime);
        assertNotNull("LocalDateTime转换为Date不应为null", date);
        
        // 验证转换结果
        String formatted = DateUtil.format(date, DateUtil.PATTERN_DATETIME);
        assertEquals("转换结果应该匹配", "2023-12-25 14:30:45", formatted);
    }
    
    @Test
    public void testGetDate_LocalDate() {
        LocalDate localDate = LocalDate.of(2023, 12, 25);
        Date date = DateUtil.getDate(localDate);
        assertNotNull("LocalDate转换为Date不应为null", date);
        
        // 验证转换结果
        String formatted = DateUtil.format(date, DateUtil.PATTERN_DATE);
        assertEquals("转换结果应该匹配", "2023-12-25", formatted);
        
        // 测试null输入
        Date nullDate = DateUtil.getDate((LocalDate) null);
        assertNull("null LocalDate应返回null", nullDate);
    }
    
    @Test
    public void testGetLocalDateTime_String_String() {
        // 测试指定格式解析
        LocalDateTime dateTime1 = DateUtil.getLocalDateTime(TEST_DATETIME_STR, DateUtil.PATTERN_DATETIME);
        assertNotNull("指定格式解析不应为null", dateTime1);
        
        // 测试null格式（自动识别）
        LocalDateTime dateTime2 = DateUtil.getLocalDateTime(TEST_DATETIME_STR, null);
        assertNotNull("null格式应自动识别", dateTime2);
        
        // 测试null输入
        LocalDateTime nullInput = DateUtil.getLocalDateTime(null, DateUtil.PATTERN_DATETIME);
        assertNull("null输入应返回null", nullInput);
    }
    
    @Test
    public void testGetLocalDateTime_Date() {
        Date date = DateUtil.getDate(TEST_DATETIME_STR);
        LocalDateTime localDateTime = DateUtil.getLocalDateTime(date);
        assertNotNull("Date转换为LocalDateTime不应为null", localDateTime);
        
        // 测试null输入
        LocalDateTime nullResult = DateUtil.getLocalDateTime((Date) null);
        assertNull("null Date应返回null", nullResult);
    }
    
    @Test
    public void testGetLocalDateTime_String() {
        // 测试各种格式的自动识别
        LocalDateTime dateTime1 = DateUtil.getLocalDateTime(TEST_DATE_STR);
        assertNull("日期格式解析不应为null", dateTime1);
        
        LocalDateTime dateTime2 = DateUtil.getLocalDateTime(TEST_DATETIME_STR);
        assertNotNull("日期时间格式解析不应为null", dateTime2);
        
        LocalDateTime dateTime3 = DateUtil.getLocalDateTime(TEST_TIME_STR1);
        assertNotNull("时间格式解析不应为null", dateTime3);
        
        LocalDateTime dateTime4 = DateUtil.getLocalDateTime(TEST_UTC_STR);
        assertNotNull("UTC格式解析不应为null", dateTime4);
        
        // 测试null输入
        LocalDateTime nullResult = DateUtil.getLocalDateTime((String) null);
        assertNull("null输入应返回null", nullResult);
        
        // 测试无效格式
        LocalDateTime invalidResult = DateUtil.getLocalDateTime("invalid-format");
        assertNull("无效格式应返回null", invalidResult);
    }
    
    @Test
    public void testParseUTCLocalDateTime() {
        LocalDateTime dateTime = DateUtil.parseUTCLocalDateTime(TEST_UTC_STR);
        assertNotNull("UTC字符串解析为LocalDateTime不应为null", dateTime);
        
        // 验证年份
        assertEquals("年份应该正确", 2023, dateTime.getYear());
        assertEquals("月份应该正确", 12, dateTime.getMonthValue());
        assertEquals("日期应该正确", 25, dateTime.getDayOfMonth());
        
        // 测试无效格式
        LocalDateTime invalidResult = DateUtil.parseUTCLocalDateTime("invalid-utc-format");
        assertNull("无效UTC格式应返回null", invalidResult);
        
        // 测试null输入
        LocalDateTime nullResult = DateUtil.parseUTCLocalDateTime(null);
        assertNull("null输入应返回null", nullResult);
    }
    
    @Test
    public void testFormatter() {
        // 测试预定义格式
        DateFormat yearFormatter = DateUtil.formatter(DateUtil.PATTERN_YEAR);
        assertNotNull("年份格式化器不应为null", yearFormatter);
        
        DateFormat monthFormatter = DateUtil.formatter(DateUtil.PATTERN_MONTH);
        assertNotNull("月份格式化器不应为null", monthFormatter);
        
        DateFormat dateFormatter = DateUtil.formatter(DateUtil.PATTERN_DATE);
        assertNotNull("日期格式化器不应为null", dateFormatter);
        
        // 测试自定义格式
        DateFormat customFormatter = DateUtil.formatter("yyyy/MM/dd");
        assertNotNull("自定义格式化器不应为null", customFormatter);
    }
    
    @Test
    public void testDateFormatConsistency() {
        Date date = DateUtil.getDate(TEST_DATETIME_STR);
        LocalDateTime localDateTime = DateUtil.getLocalDateTime(TEST_DATETIME_STR);
        
        // 验证Date和LocalDateTime转换的一致性
        Date convertedDate = DateUtil.getDate(localDateTime);
        LocalDateTime convertedLocalDateTime = DateUtil.getLocalDateTime(date);
        
        assertNotNull("转换后的Date不应为null", convertedDate);
        assertNotNull("转换后的LocalDateTime不应为null", convertedLocalDateTime);
        
        String dateStr1 = DateUtil.format(date, DateUtil.PATTERN_DATETIME);
        String dateStr2 = DateUtil.format(convertedDate, DateUtil.PATTERN_DATETIME);
        assertEquals("Date转换前后应该一致", dateStr1, dateStr2);
    }
    
    @Test
    public void testSpecialFormats() {
        // 测试各种预定义格式
        Date date = DateUtil.getDate(TEST_DATETIME_STR);
        
        String yearFormat = DateUtil.format(date, DateUtil.PATTERN_YEAR);
        assertEquals("年份格式应该正确", "2023", yearFormat);
        
        String monthFormat = DateUtil.format(date, DateUtil.PATTERN_MONTH);
        assertEquals("月份格式应该正确", "2023-12", monthFormat);
        
        String dateFormat = DateUtil.format(date, DateUtil.PATTERN_DATE);
        assertEquals("日期格式应该正确", TEST_DATE_STR, dateFormat);
        
        String timeFormat = DateUtil.format(date, DateUtil.PATTERN_TIME);
        assertEquals("时间格式应该正确", "14:30:45", timeFormat);
        
        String dateTimeFormat = DateUtil.format(date, DateUtil.PATTERN_DATETIME);
        assertEquals("日期时间格式应该正确", TEST_DATETIME_STR, dateTimeFormat);
    }
    
    @Test
    public void testMiniFormats() {
        Date date = DateUtil.getDate(TEST_DATETIME_STR);
        
        String miniDateFormat = DateUtil.format(date, DateUtil.PATTERN_DATE_MINI);
        assertEquals("简化日期格式应该正确", "20231225", miniDateFormat);
        
        String miniDateTimeFormat = DateUtil.format(date, DateUtil.PATTERN_DATETIME_MINI);
        assertEquals("简化日期时间格式应该正确", "20231225143045", miniDateTimeFormat);
    }
}