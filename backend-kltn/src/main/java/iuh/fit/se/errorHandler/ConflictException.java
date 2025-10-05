package iuh.fit.se.errorHandler;
public class ConflictException extends RuntimeException {

	private static final long serialVersionUID = -5549322336940535680L;

	public ConflictException() {
		super("Conflict occurred");
	}
	
	public ConflictException(String message) {
        super(message);
    }
}