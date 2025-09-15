package iuh.fit.se.util;

import java.time.LocalDateTime;

public class TimeCheckUtil {
	public static void checkCreateObjectValid(
		LocalDateTime startTime,
		LocalDateTime endTime
	) {
		checkTimeRangeValid(startTime, endTime);
		if (startTime.isBefore(LocalDateTime.now())) {
			throw new IllegalArgumentException("Start time must be in the future");
		}
	}
	
	public static void checkTimeRangeValid(
		LocalDateTime startTime,
		LocalDateTime endTime
	) {
		if (startTime == null || endTime == null) {
			throw new IllegalArgumentException("Start time and end time must not be null");
		}
		if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
			throw new IllegalArgumentException("Start time must be before end time");
		}
	}
}
