package com.xwtech.es;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author Created by zl on 2017/2/15.
 *
 */
public class DateFormatUtil {
	/* Create Date Formats */
	final static String[] possibleDateFormats = {
			/* RFC 1123 with 2-digit Year */"EEE, dd MMM yy HH:mm:ss z",
			/* RFC 1123 with 4-digit Year */"EEE, dd MMM yyyy HH:mm:ss z",
			/* RFC 1123 with no Timezone */"EEE, dd MMM yy HH:mm:ss",
			/* Variant of RFC 1123 */"EEE, MMM dd yy HH:mm:ss",
			/* RFC 1123 with no Seconds */"EEE, dd MMM yy HH:mm z",
			/* Variant of RFC 1123 */"EEE dd MMM yyyy HH:mm:ss",
			/* RFC 1123 with no Day */"dd MMM yy HH:mm:ss z",
			/* RFC 1123 with no Day or Seconds */"dd MMM yy HH:mm z",
			/* ISO 8601 slightly modified */"yyyy-MM-dd'T'HH:mm:ssZ",
			/* ISO 8601 slightly modified */"yyyy-MM-dd'T'HH:mm:ss'Z'",
			/* ISO 8601 slightly modified */"yyyy-MM-dd'T'HH:mm:sszzzz",
			/* ISO 8601 slightly modified */"yyyy-MM-dd'T'HH:mm:ss z",
			/* ISO 8601 */"yyyy-MM-dd'T'HH:mm:ssz",
			/* ISO 8601 slightly modified */"yyyy-MM-dd'T'HH:mm:ss.SSSz",
			/* ISO 8601 slightly modified */"yyyy-MM-dd'T'HHmmss.SSSz",
			/* ISO 8601 slightly modified */"yyyy-MM-dd'T'HH:mm:ss",
			/* ISO 8601 w/o seconds */"yyyy-MM-dd'T'HH:mmZ",
			/* ISO 8601 w/o seconds */"yyyy-MM-dd'T'HH:mm'Z'",
			/* RFC 1123 without Day Name */"dd MMM yyyy HH:mm:ss z",
			/* RFC 1123 without Day Name and Seconds */"dd MMM yyyy HH:mm z",
			/* Simple Date Format */"yyyy-MM-dd", /* Simple Date Format */"MMM dd, yyyy" };

	public final static String GTMFormatMilSSS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	public final static String GTMFormatMil = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	public final static String DateFormatMil = "yyyy-MM-dd HH:mm:ss";

	/**
	 * GTM时间转换+8小时，格式自定义
	 * 
	 * @param date
	 * @param GTMFormat
	 * @param DateFormat
	 * @return String date
	 */
	public static String DateFormatGMT(String date, String GTMFormat, String DateFormat) {
		SimpleDateFormat sf = new SimpleDateFormat(GTMFormat);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateFormat);
		try {
			Date parseDate = sf.parse(date);
			Long timeStamp = parseDate.getTime()+8*60*60*1000L;
			date = simpleDateFormat.format(new Date(timeStamp));
		} catch (ParseException e) {
			return date;
		}
		return date;
	}
	
	
	/**
	 * GTM时间转换+8小时的时间戳
	 * 
	 * @param date
	 * @param GTMFormat
	 * @param DateFormat
	 * @return String date
	 */
	public static Long DateFormatGMT(String date, String GTMFormat) {
		SimpleDateFormat sf = new SimpleDateFormat(GTMFormat);
		Long timeStamp = new Date().getTime();
		try {
			Date parseDate = sf.parse(date);
		    timeStamp = parseDate.getTime()+8*60*60*1000L;
			
		} catch (ParseException e) {
			return timeStamp;
		}
		return timeStamp;
	}
	
	/**
	 * String时间 转换成GTM时间-8小时，格式自定义
	 * @param date
	 * @param GTMFormat
	 * @param DateFormat
	 * @return
	 * @throws ParseException 
	 */
	public static String StringFormatGMTDate(String date, String GTMFormat, String DateFormat){
		Date date1 = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(DateFormat);
		SimpleDateFormat sf = new SimpleDateFormat(GTMFormat);
		
		try {
		    date1 = formatter.parse(date);
		    Long timeStamp = date1.getTime()-8*60*60*1000L;
		    date = sf.format(new Date(timeStamp));
		} catch (Exception e) {
			return date;
		}
		return date;
	}

	/**
	 * 秒级别，时间戳转换成时间,格式自定义
	 * 
	 * @param date
	 * @param DateFormat
	 * @return String date
	 */
	public static String DateStampFormat(String date, String DateFormat) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateFormat);
			long timeStamp = new Long(Long.valueOf(date) * 1000L);
			Date dateStamp = new Date(timeStamp);
			date = simpleDateFormat.format(dateStamp);
		} catch (Exception e) {
			return date;
		}
		return date;
	}

	/**
	 * 秒级别，距离当前时间戳转成时间，格式自定义
	 * 
	 * @param date
	 * @param nowTime
	 * @param DateFormat
	 * @return
	 */
	public static String DateStampFormat(String date, Long nowTime, String DateFormat) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateFormat);
			long timeStamp = new Long(Long.valueOf(date) * 1000L);
			Date dateUp = new Date(nowTime - timeStamp);
			date = simpleDateFormat.format(dateUp);
		} catch (Exception e) {
			return date;
		}
		return date;
	}
	
}
