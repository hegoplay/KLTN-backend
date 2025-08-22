package iuh.fit.se.services.event_service.dto;

import java.util.List;

import iuh.fit.se.entity.enumerator.OrganizerRole;
import iuh.fit.se.services.user_service.dto.UserShortInfoResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@lombok.Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventOrganizerDto {
	String id;
	UserShortInfoResponseDto organizer;
	String roleContent;
	List<OrganizerRole> roles;
	
}	
