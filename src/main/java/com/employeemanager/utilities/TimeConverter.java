package com.employeemanager.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class TimeConverter {

	public static Long EpochMillis_Now() {
		Instant machineTimestamp = Instant.now();
		ZonedDateTime TimeinLA = machineTimestamp.atZone(ZoneId.systemDefault());
		return TimeinLA.toEpochSecond() * 1000;
	}

	public static LocalDateTime EpochMils_to_LocalDateTime(Long epocMils) {

		LocalDateTime Date1 = (Instant.ofEpochMilli(epocMils).atZone(ZoneId.systemDefault())).toLocalDateTime();

		return Date1;
	}

	public static LocalDate EpochMils_to_LocalDate(Long epocMils) {

		LocalDate Date1 = (Instant.ofEpochMilli(epocMils).atZone(ZoneId.systemDefault())).toLocalDate();

		return Date1;
	}

	public static Long LocalDate_to_EpochMilli_DayStart(LocalDate date) {

		return date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	public static Long LocalDate_to_EpochMilli_DayEnd(LocalDate date) {

		return date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	public static Long LocalDateTime_to_EpochMilli(LocalDateTime date) {

		return date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	public static Long LocalTime_to_Milli(LocalTime time) {

		return (time.getHour() * 60 * 60000L) + (time.getMinute() * 60000L) + (time.getSecond() * 1000L);
	}

	public static Long Millis_To_Days(Long millis) {

		return ((millis) / (24 * 3600000));
	}

	public static Long Millis_To_Hours(Long millis) {

		return ((millis) / (3600000));
	}

	public static Integer Millis_to_Years(Long millis) {
		int year = 0;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(millis);
		year = cal.get(Calendar.YEAR);

		return year;
	}

	public static LocalDate String_to_LocalDate(String datetime) {

		if (datetime != null) {
			if (datetime.equals("")) {
				return null;

			} else {
				SimpleDateFormat dMy = new SimpleDateFormat("dd/MM/yyyy");
				SimpleDateFormat mdy = new SimpleDateFormat("MM/dd/yyyy");
				SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat dmy = new SimpleDateFormat("dd/M/yyyy");
				try {
					dmy.parse(datetime);
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/M/yyyy");
					LocalDate dateTime = LocalDate.parse(datetime, formatter);
					return dateTime;

				} catch (ParseException e) {

				}

				try {
					dMy.parse(datetime);
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					LocalDate dateTime = LocalDate.parse(datetime, formatter);
					return dateTime;

				} catch (ParseException e) {

				}

				try {
					mdy.parse(datetime);
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
					LocalDate dateTime = LocalDate.parse(datetime, formatter);
					return dateTime;

				} catch (ParseException e) {

				}

				try {
					ymd.parse(datetime);
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

					LocalDate dateTime = LocalDate.parse(datetime, formatter);

					return dateTime;

				} catch (ParseException e) {

				}

				return LocalDate.ofEpochDay(0);

			}

		} else {
			return null;
		}

	}

	public static Date EpochMils_to_Date(Long epocMils) {

		Date date = Date.from((Instant.ofEpochMilli(epocMils).atZone(ZoneId.systemDefault())).toInstant());

		return date;
	}
}
