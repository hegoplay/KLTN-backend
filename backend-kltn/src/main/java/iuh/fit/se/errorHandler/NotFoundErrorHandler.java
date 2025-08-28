package iuh.fit.se.errorHandler;

public class NotFoundErrorHandler extends RuntimeException {
	private static final String MESSAGE = "Không tìm thấy tài nguyên yêu cầu";
	private static final long serialVersionUID = 5290549048313203592L;

	public NotFoundErrorHandler(String message) {
		super(message);
	}
	
	public NotFoundErrorHandler() {
		super(MESSAGE);
	}

}
