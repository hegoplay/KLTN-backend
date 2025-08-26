package iuh.fit.se.services.event_service.dto.request;

import java.util.List;

public record ManualTriggerRequestDto(List<String> attendeeIds) {

}
