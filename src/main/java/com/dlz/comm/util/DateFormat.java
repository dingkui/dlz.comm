package com.dlz.comm.util;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;
import java.util.Queue;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 参考tomcat8中的并发DateFormat
 * <p>
 * {@link SimpleDateFormat}的线程安全包装器。
 * 不使用ThreadLocal，创建足够的SimpleDateFormat对象来满足并发性要求。
 * </p>
 *
 * @author dk
 */
@Slf4j
public class DateFormat {
	public final String pattern;
	private final Locale locale;
	private final TimeZone timezone;
	private final Queue<SimpleDateFormat> queue = new ConcurrentLinkedQueue<>();
	public final DateTimeFormatter formatter;
	private DateFormat(String pattern, Locale locale, TimeZone timezone) {
		this.pattern = pattern;
		this.locale = locale;
		this.timezone = timezone;
		this.formatter = DateTimeFormatter.ofPattern(pattern);
		SimpleDateFormat initial = createInstance();
		queue.add(initial);
	}

	public static DateFormat of(String pattern) {
		return new DateFormat(pattern, Locale.getDefault(), TimeZone.getDefault());
	}

	public static DateFormat of(String pattern, TimeZone timezone) {
		return new DateFormat(pattern, Locale.getDefault(), timezone);
	}

	public static DateFormat of(String pattern, Locale locale, TimeZone timezone) {
		return new DateFormat(pattern, locale, timezone);
	}

	public String format(Date date) {
		SimpleDateFormat sdf = queue.poll();
		if (sdf == null) {
			sdf = createInstance();
		}
		String result = sdf.format(date);
		queue.add(sdf);
		return result;
	}

	/**
	 * 格式化日期
	 *
	 * @param date
	 * @return String
	 */
	public String format(TemporalAccessor date) {
		if (date == null) {
			return "";
		}
		return formatter.format(date);
	}
	public LocalDateTime parse2LocalDate(String dateStr) {
		try {
			return LocalDateTime.parse(dateStr, formatter);
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
			return null;
		}
	}

	public Date parse(String source){
		try {
			SimpleDateFormat sdf = queue.poll();
			if (sdf == null) {
				sdf = createInstance();
			}
			Date result = sdf.parse(source);
			queue.add(sdf);
			return result;
		} catch (ParseException e) {
			log.error(ExceptionUtils.getStackTrace(e));
			return null;
		}
	}

	private SimpleDateFormat createInstance() {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
		sdf.setTimeZone(timezone);
		return sdf;
	}

	public String formatNow() {
		return format(LocalDateTime.now());
	}

	/**
	 * 格式化日期
	 * @return  String
	 */
	public String toString() {
		return pattern;
	}
}
