package iuh.fit.se.services.event_service.dto.request;

import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.enumerator.EventCategory;

public record EventStatusUpdateRequestDto(FunctionStatus status) {

}
