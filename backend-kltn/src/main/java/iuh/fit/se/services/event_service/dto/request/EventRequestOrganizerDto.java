package iuh.fit.se.services.event_service.dto.request;

import java.util.Set;

import iuh.fit.se.entity.enumerator.OrganizerRole;

public record EventRequestOrganizerDto(
	String userId,
	String roleContent,
	Set<OrganizerRole> roles
) {
}
