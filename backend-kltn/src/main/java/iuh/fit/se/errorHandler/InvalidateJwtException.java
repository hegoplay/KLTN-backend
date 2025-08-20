package iuh.fit.se.errorHandler;

public class InvalidateJwtException extends RuntimeException {

	private static final long serialVersionUID = -4956634070883407936L;
	private static final String message = "Invalid JWT token";
	
	public InvalidateJwtException() {
		super(message);
	}
	
	public InvalidateJwtException(String message) {
		super(message);
	}

	public InvalidateJwtException(String message, Throwable cause) {
		super(message, cause);
	}

}
