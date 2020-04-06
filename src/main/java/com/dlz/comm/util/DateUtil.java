package com.dlz.comm.util;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

@Slf4j
public class DateUtil {
    public static String getDateStr(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        if (date == null) {
            return "";
        }
        return format.format(date);
    }
	private static Date transDate(String dateStr, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		try {
			return format.parse(dateStr);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

    /**
     * getDate get a string with format YYYY-MM-DD from a Date object
     *
     * @param date date
     * @return String
     */
    public static String getDate(Date date) {
        return getDateStr(date,"yyyy-MM-dd");
    }

    public static String getDate() {
        return getDateStr(new Date(),"yyyy-MM-dd");
    }

    /**
     * getDateStr get a string with format YYYY-MM-DD HH:mm:ss from a Date object
     *
     * @param date date
     * @return String
     */
    static public String getDateTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return getDateStr(date,"yyyy-MM-dd HH:mm:ss");
    }

    static public String getDateTime() {
        return getDateStr(new Date(),"yyyy-MM-dd HH:mm:ss");
    }

	public static Date toUTCDate(String dateVal) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		// 设置时区UTC
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			return formatter.parse(dateVal);
		} catch (ParseException e) {
			return null;
		}
	}

	private final static Map<String,Pattern> date_trans = new LinkedHashMap<>();
	static {
		date_trans.put("yyyy-MM-dd HH:mm:ss",Pattern.compile("^\\d{4}-[0,1]?\\d-[0-3]?\\d \\d{2}:\\d{2}:\\d{2}.*"));
		date_trans.put("yyyy年MM月dd日 HH时mm分ss秒",Pattern.compile("^\\d{4}年[0,1]?\\d月[0-3]?\\d日 \\d{2}时\\d{2}分\\d{2}秒"));
		date_trans.put("yyyy-MM-dd",Pattern.compile("^\\d{4}-\\d{1,2}-\\d{1,2}$"));
		date_trans.put("yyyy-MM",Pattern.compile("^\\d{4}-\\d{1,2}$"));
		date_trans.put("yyyy-MM-dd HH:mm",Pattern.compile("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}$"));
	}

	public static Date getDate(String input,String format){
		if(input==null){
			return null;
		}
		if(format!=null){
			return transDate(input,format);
		}
		String input2 = input.replaceAll("/", "-").replaceAll("\"", "");
		for(Map.Entry<String,Pattern> entry: date_trans.entrySet()){
			if(entry.getValue().matcher(input2).matches()){
				return transDate(input,entry.getKey());
			}
		}
		if (input2.matches("^\\d{1,2}:\\d{1,2}$")) {
			return transDate(DateUtil.getDate()+ " " + input2, "yyyy-MM-dd HH:mm");
		} else if (input2.matches("^\\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
			return transDate(DateUtil.getDate()+ " " + input2, "yyyy-MM-dd HH:mm:ss");
		}else if(input2.matches("^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{3}Z$")){
			return toUTCDate(input2);
		}
		log.error("转换日期格式未识别："+input);
		return null;
	}
}