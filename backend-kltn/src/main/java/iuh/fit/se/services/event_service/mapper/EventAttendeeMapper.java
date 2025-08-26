package iuh.fit.se.services.event_service.mapper;

import org.mapstruct.Mapper;

import iuh.fit.se.services.event_service.dto.AttendeeDto;
import iuh.fit.se.services.user_service.mapper.UserMapper;

@Mapper(
	componentModel = "spring",
	uses = {UserMapper.class}
)
public interface EventAttendeeMapper{
	AttendeeDto toAttendeeDto(iuh.fit.se.entity.Attendee attendee);
}
