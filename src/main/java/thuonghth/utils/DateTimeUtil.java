package thuonghth.utils;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeUtil {
	private static final String ZONE_ID = "Asia/Ho_Chi_Minh";

	private static final String DATE_FORMAT = "yyyy-MM-dd";

	public static Date getCurrentDate() {
		return Date.valueOf(LocalDate.now(ZoneId.of(ZONE_ID)));
	}

	public static Date getDate(String dateStr) {
		return Date.valueOf(dateStr);
	}

	public static String getDateStr(Date date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
		return formatter.format(date.toLocalDate());
	}

	public static String getCurrentDateStr() {
		return getDateStr(getCurrentDate());
	}

	public static boolean isValidDate(String dateStr) {
		try {
			LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_FORMAT));
		} catch (DateTimeParseException e) {
			return false;
		}
		return true;
	}

	public static boolean isValidDateInterval(String beginDateStr, String endDateStr) {
		return !LocalDate.parse(beginDateStr).isAfter(LocalDate.parse(endDateStr));
	}

	public static boolean isFromToday(String dateStr) {
		return !LocalDate.parse(dateStr).isBefore(LocalDate.now(ZoneId.of(ZONE_ID)));
	}

	public static Date getNextPeriodDays(Date date, int numberOfDays) {
		return Date.valueOf(date.toLocalDate().plus(Period.ofDays(numberOfDays)));
	}
}
