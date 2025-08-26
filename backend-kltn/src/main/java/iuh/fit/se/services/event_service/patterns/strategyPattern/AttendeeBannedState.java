package iuh.fit.se.services.event_service.patterns.strategyPattern;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import iuh.fit.se.entity.Attendee;
import iuh.fit.se.entity.enumerator.AttendeeStatus;


@Component
@Scope("prototype")
public class AttendeeBannedState implements AttendeeStausState{

	
	
	@Override
	public void triggerRegister(Attendee attendee) {
//		if (attendee.getStatus() == AttendeeStatus.REGISTERED) {
//			
//		}
	}

	@Override
	public void triggerCheckIn(Attendee attendee) {
		
	}

	@Override
	public void triggerBan(Attendee attendee) {
		
	}

	@Override
	public AttendeeStatus getStatus() {
		return null;
	}

}
