package iuh.fit.se.errorHandler;

public class InputNotFoundException extends RuntimeException{

	private static final long serialVersionUID = -7979519453501806607L;
	private static final String message = "Input Data not found";
	
	public InputNotFoundException() {
		super(message);
	}
	
	public InputNotFoundException(String message) {
		super(message);
	}

	public InputNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
