package sigtuna.discord.classes;

import java.util.Arrays;
import java.util.List;

import sigtuna.discord.exception.DateFormatException;

public class DateFormat {
	public DateFormat(int year, int month, int day, int hour, int minutes, int second) throws DateFormatException {
		if(year < 0) {
			throw new DateFormatException("年份必須為大於0之整數");
		}
		if(month < 0 || month > 12) {
			throw new DateFormatException("年份必須為大於0且小於12之整數");
		}
		if(day < 0 || day > 31) {
			throw new DateFormatException("年份必須為大於0且小於31之整數");
		}
		if(hour < 0 || hour > 24) {
			throw new DateFormatException("小時必須為大於0且小於24之整數");
		}
		if(minutes < 0 || minutes > 60) {
			throw new DateFormatException("分鐘必須為大於0且小於60之整數");
		}
		if(second < 0 || second > 60) {
			throw new DateFormatException("秒數必須為大於0且小於60之整數");
		}
		List<Integer> exceptMonth = Arrays.asList(4,6,9,11);
		if(day == 31 && exceptMonth.contains(month)) {
			throw new DateFormatException(month + "月是小月，沒有31號");
		}
		if(!(year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) && day == 29) {
			throw new DateFormatException(year + "年不是閏年，沒有29號");
		}
		if(day > 29 && month == 2) {
			throw new DateFormatException(month + "月沒有大於29號的天數");
		}
	}
}
