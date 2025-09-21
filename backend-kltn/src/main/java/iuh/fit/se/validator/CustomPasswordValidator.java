package iuh.fit.se.validator;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CustomPasswordValidator 
    implements ConstraintValidator<CustomPassword, String> {

    @Override
    public void initialize(CustomPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
    	String regex = "^(?=.*[A-Z0-9]).{8,32}$";
    	Pattern pattern = Pattern.compile(regex);
    	return pattern.matcher(password).matches();
    }
}