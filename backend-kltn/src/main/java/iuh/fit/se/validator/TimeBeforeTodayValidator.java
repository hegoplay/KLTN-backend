package iuh.fit.se.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TimeBeforeTodayValidator 
    implements ConstraintValidator<BirthDateBeforeToday, LocalDateTime> {

    @Override
    public void initialize(BirthDateBeforeToday constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDateTime time, ConstraintValidatorContext context) {
        if (time == null) {
        	return false;
        }
        return time.isBefore(LocalDate.now().atStartOfDay());
    }
}