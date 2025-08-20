package iuh.fit.se.errorHandler;

public class OutsideOperatingHoursException extends RuntimeException {

	private static final long serialVersionUID = 70608917732862298L;

	public OutsideOperatingHoursException() {
		super("The operation is not allowed outside of operating hours.");
	}
	public OutsideOperatingHoursException(String message) {
		super(message);
	}
}
