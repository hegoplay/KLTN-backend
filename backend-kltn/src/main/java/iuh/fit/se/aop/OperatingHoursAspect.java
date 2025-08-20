package iuh.fit.se.aop;

import java.time.LocalTime;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import iuh.fit.se.errorHandler.OutsideOperatingHoursException;

@Aspect
@Component
public class OperatingHoursAspect {

    private static final LocalTime OPENING_TIME = LocalTime.of(6, 0); // 6:00 AM
    private static final LocalTime CLOSING_TIME = LocalTime.of(22, 0); // 10:00 PM

    @Before("@annotation(operatingHoursCheck)")
    public void checkOperatingHours(JoinPoint joinPoint, OperatingHoursCheck operatingHoursCheck) {
        LocalTime currentTime = LocalTime.now();
        
        if (currentTime.isBefore(OPENING_TIME) || currentTime.isAfter(CLOSING_TIME)) {
            throw new OutsideOperatingHoursException(
                "Hệ thống chỉ hoạt động từ " + OPENING_TIME + " đến " + CLOSING_TIME);
        }
    }
}