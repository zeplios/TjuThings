package edu.tju.ina.things.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

	/**
	 * set separator = true, return 2014年12月19日
	 * e.g. toCN = '-', return 2014-12-19
	 * @return
	 */
	public static String getCurrentYYYYMMDD(String separator) {
		Calendar calendar = Calendar.getInstance(Locale.getDefault());
		if (separator == null) {
			return calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月"
					+ calendar.get(Calendar.DAY_OF_MONTH) + "日";
		} else {
			return calendar.get(Calendar.YEAR) + separator
					+ (calendar.get(Calendar.MONTH) + 1) + separator
					+ calendar.get(Calendar.DAY_OF_MONTH);
		}
	}
	
	public static String getYYYYMMDD(String timestamp, String separator) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.getDefault());
		Calendar calendar = Calendar.getInstance(Locale.getDefault());
        try {
			Date d = (Date) f.parseObject(timestamp);
			calendar.setTime(d);
			if (separator == null) {
				return calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月"
						+ calendar.get(Calendar.DAY_OF_MONTH) + "日";
			} else {
				return calendar.get(Calendar.YEAR) + separator 
						+ (calendar.get(Calendar.MONTH) + 1) + separator
						+ calendar.get(Calendar.DAY_OF_MONTH);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean timeAfter(String timestamp) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.getDefault());
		Calendar calendar = Calendar.getInstance(Locale.getDefault());
		Calendar now = Calendar.getInstance(Locale.getDefault());
        try {
			Date d = (Date) f.parseObject(timestamp);
			calendar.setTime(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return now.after(calendar);
	}
}
