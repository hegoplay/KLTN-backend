package iuh.fit.se.services.event_service.patterns.strategyPattern;

import iuh.fit.se.entity.Attendee;
import iuh.fit.se.entity.enumerator.AttendeeStatus;

public interface AttendeeStausState {
	
	void triggerRegister(Attendee attendee);
	void triggerCheckIn(Attendee attendee);
	void triggerBan(Attendee attendee);
	AttendeeStatus getStatus();
}
