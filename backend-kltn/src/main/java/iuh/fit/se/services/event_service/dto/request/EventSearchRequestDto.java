package iuh.fit.se.services.event_service.dto.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import io.swagger.v3.oas.annotations.media.Schema;
import iuh.fit.se.services.event_service.dto.enumerator.EventSearchType;

public record EventSearchRequestDto(
    String keyword,
    EventSearchType type, // Enum: SEMINAR, CONTEST, TRAINING
    Boolean isDone,
    int page,
    int size,
    Sort sortBy
) {
	@Schema(hidden = true)
    public Pageable toPageable() {
        return PageRequest.of(page, size, sortBy);
    }
}

