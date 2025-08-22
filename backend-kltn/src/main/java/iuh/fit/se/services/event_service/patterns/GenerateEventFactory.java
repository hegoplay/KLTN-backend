package iuh.fit.se.services.event_service.patterns;

import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.User;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.EventCreateRequestDto;
import iuh.fit.se.services.event_service.dto.EventDetailResponseDto;
import iuh.fit.se.services.user_service.service.UserService;

public abstract class GenerateEventFactory {
//	public  
	
	public Event createEvent(EventCreateRequestDto dto, UserService userService) {
		
		if (!dto.isCreateAble()) {
			throw new IllegalArgumentException("Event cannot be created with the provided status: " + dto.status());
		}
		if (dto.location().getEndTime().isBefore(dto.location().getStartTime())) {
			throw new IllegalArgumentException("End time must be after start time");
		}
		
		// Generate the event using the abstract method
		Event e = generateEvent(dto);
		if (dto.status() == null) {
			e.setStatus(FunctionStatus.ARCHIVED);
		}
		if (dto.multiple() == null) {
			e.setMultiple(1);
		}
		
		User currentUser = userService.getCurrentUser();
		e.setHost(currentUser);
		
		return e;
	}
	public abstract EventDetailResponseDto toEventDetailResponseDto(Event e); 
	
	protected abstract Event generateEvent(EventCreateRequestDto dto);
	
}
