package iuh.fit.se.services.event_service.mapper;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import iuh.fit.se.entity.EventOrganizer;
import iuh.fit.se.errorHandler.InputNotFoundException;
import iuh.fit.se.services.event_service.dto.EventRequestOrganizerDto;
import iuh.fit.se.services.user_service.repository.UserRepository;

@Mapper(componentModel = "spring")
public abstract class EventOrganizerMapper {
    
    @Autowired
    protected UserRepository userRepository;
    
    public EventOrganizer toEventOrganizer(EventRequestOrganizerDto dto) {
        return EventOrganizer.builder()
            .organizerId(null)
            .organizer(userRepository
                .findById(dto.userId())
                .orElseThrow(() -> new InputNotFoundException("user with id " + dto.userId() + " not found")))
            .roleContent(dto.roleContent())
            .roles(dto.roles())
            .build();
    }
}