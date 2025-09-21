package iuh.fit.se.services.event_service.mapper;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import iuh.fit.se.entity.EventOrganizer;
import iuh.fit.se.errorHandler.NotFoundErrorHandler;
import iuh.fit.se.services.event_service.dto.EventOrganizerDto;
import iuh.fit.se.services.event_service.dto.request.EventRequestOrganizerDto;
import iuh.fit.se.services.user_service.mapper.UserMapper;
import iuh.fit.se.services.user_service.repository.UserRepository;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class EventOrganizerMapper {
    
    @Autowired
    protected UserRepository userRepository;
    
    public EventOrganizer toEventOrganizer(EventRequestOrganizerDto dto) {
//    	TODO: có thời gian sẽ đập đi xây lại do vi phạm nguyên tắc mapper chỉ để chuyển đổi
        return EventOrganizer.builder()
            .organizer(userRepository
                .findById(dto.organizerId())
                .orElseThrow(() -> new NotFoundErrorHandler("user with id " + dto.organizerId() + " not found")))
            .roleContent(dto.roleContent())
            .roles(dto.roles())
            .build();
    }
    
    public abstract EventOrganizerDto toEventOrganizerDto(EventOrganizer entity);
}