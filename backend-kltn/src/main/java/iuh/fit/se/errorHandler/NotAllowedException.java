package iuh.fit.se.errorHandler;

public class NotAllowedException extends RuntimeException {

	private static final long serialVersionUID = -1133853183710079558L;
	private static final String message = "This action is not allowed";
	
	public NotAllowedException() {
		super(message);
	}
	
	public NotAllowedException(String message) {
		super(message);
	}

	public NotAllowedException(String message, Throwable cause) {
		super(message, cause);
	}

}
