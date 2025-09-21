package iuh.fit.se.constant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeConstant {
	public static final LocalTime OPENING_TIME = LocalTime.of(6, 0); // 6:00 AM
	public static final LocalTime CLOSING_TIME = LocalTime.of(22, 0); // 10:00 PM
	public static final LocalDate MIN_DATE = LocalDate.of(1970, 1, 1);
	public static final LocalDate MAX_DATE = LocalDate.of(3000, 1, 1);
	
	public static LocalDateTime getCurrentCloseTime() {
		LocalDateTime now = LocalDateTime.now();
		return LocalDateTime.of(now.toLocalDate(), CLOSING_TIME);
	}	
	
	public static LocalDateTime getCurrentOpenTime() {
		LocalDateTime now = LocalDateTime.now();
		return LocalDateTime.of(now.toLocalDate(), OPENING_TIME);
	}
	public static LocalDateTime fromLocalDate(LocalDate date) {
		return LocalDateTime.of(date, OPENING_TIME.minusMinutes(1));
	}
	
	public static LocalDateTime toLocalDateTime(LocalDate date) {
		return LocalDateTime.of(date,LocalTime.MAX);
	}
}
