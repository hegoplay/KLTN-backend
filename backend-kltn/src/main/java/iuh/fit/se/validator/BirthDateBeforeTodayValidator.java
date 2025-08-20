package iuh.fit.se.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class BirthDateBeforeTodayValidator 
    implements ConstraintValidator<BirthDateBeforeToday, LocalDate> {

    @Override
    public void initialize(BirthDateBeforeToday constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if (birthDate == null) {
            return true; // Cho phép null, nếu muốn bắt buộc thì return false
        }
        return birthDate.isBefore(LocalDate.now());
    }
}