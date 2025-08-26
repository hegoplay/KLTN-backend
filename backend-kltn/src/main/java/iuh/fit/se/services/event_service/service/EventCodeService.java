package iuh.fit.se.services.event_service.service;

public interface EventCodeService {
	
	String generateOrUpdateEventCode(String eventId);
	String getCurrentEventCode(String eventId);
	boolean verifyEventCode(String eventId, String enteredCode);
	void disableEventCode(String eventId);
	boolean hasEventCode(String eventId);
}
