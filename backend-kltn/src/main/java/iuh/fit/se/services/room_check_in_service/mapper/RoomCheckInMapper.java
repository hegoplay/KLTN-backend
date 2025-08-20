package iuh.fit.se.services.room_check_in_service.mapper;

import org.mapstruct.Mapper;

import iuh.fit.se.entity.RoomCheckIn;
import iuh.fit.se.services.room_check_in_service.dto.CheckInHistoryResponseDto;

@Mapper(componentModel = "spring")
public interface RoomCheckInMapper {
	CheckInHistoryResponseDto toCheckInResponseDto(RoomCheckIn roomCheckIn);
}


