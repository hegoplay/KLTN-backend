package iuh.fit.se.errorHandler;
public class AccessDeniedException extends RuntimeException {
    private static final long serialVersionUID = 108048820801725446L;

	public AccessDeniedException(String message) {
        super(message);
    }
}