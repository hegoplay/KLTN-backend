package iuh.fit.se.services.event_service.dto;

import java.util.List;

import iuh.fit.se.entity.enumerator.OrganizerRole;

public record EventRequestOrganizerDto(
	String userId,
	String roleContent,
	List<OrganizerRole> roles
) {
}
