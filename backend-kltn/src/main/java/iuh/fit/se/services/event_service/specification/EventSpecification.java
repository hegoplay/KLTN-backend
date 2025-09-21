package iuh.fit.se.services.event_service.specification;

import org.springframework.data.jpa.domain.Specification;

import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.EventOrganizer;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public class EventSpecification {

	public static Specification<Event> hasTitleContaining(String keyword) {
		return (root, query, criteriaBuilder) -> {
			if (keyword == null || keyword.isEmpty()) {
				return criteriaBuilder.conjunction();
			}
			return criteriaBuilder
				.like(criteriaBuilder.lower(root.get("title")),
					"%" + keyword.toLowerCase() + "%");
		};
	}

	public static Specification<Event> hasDescriptionContaining(String keyword) {
		return (root, query, criteriaBuilder) -> {
			if (keyword == null || keyword.isEmpty()) {
				return criteriaBuilder.conjunction();
			}
			return criteriaBuilder
				.like(criteriaBuilder.lower(root.get("description")),
					"%" + keyword.toLowerCase() + "%");
		};
	}

	public static Specification<Event> hasStatus(FunctionStatus status) {
		return (root, query, criteriaBuilder) -> {
			if (status == null) {
				return criteriaBuilder.conjunction();
			}
			return criteriaBuilder.equal(root.get("status"), status);
		};
	}

	public static Specification<Event> isOfType(
		Class<? extends Event> eventType
	) {
		return (root, query, criteriaBuilder) -> {
			if (eventType == null) {
				return criteriaBuilder.conjunction();
			}
			return criteriaBuilder.equal(root.type(), eventType);
		};
	}

	public static Specification<Event> hasHostedUserId(String userId) {
		return (root, query, criteriaBuilder) -> criteriaBuilder
			.equal(root.get("host").get("id"), userId);
	}

	public static Specification<Event> hasHostedUsername(String username) {
		return (root, query, criteriaBuilder) -> criteriaBuilder
			.equal(root.get("host").get("username"), username);
	}

	public static Specification<Event> includeOrganizerId(String organizerId) {
		
		
		return (root, query, criteriaBuilder) -> {
			
			// Join với bảng organizers
			Join<Event, EventOrganizer> organizersJoin = root.join("organizers", JoinType.LEFT);			
			
			return criteriaBuilder
			.equal(organizersJoin.get("organizer").get("id"), organizerId);
		};
	}

	public static Specification<Event> isNotDone() {
		return (root, query, criteriaBuilder) -> criteriaBuilder
			.isFalse(root.get("done"));
	}

	public static Specification<Event> exceptOfType(
		Class<? extends Event> eventType
	) {
		return (root, query, criteriaBuilder) -> {
			if (eventType == null) {
				return criteriaBuilder.conjunction();
			}
			return criteriaBuilder.notEqual(root.type(), eventType);
		};
	}

	public static Specification<Event> isSingle(boolean single) {
		if (!single) {
			return (root, query, criteriaBuilder) -> criteriaBuilder
				.isFalse(root.get("single"));
		}
		return (root, query, criteriaBuilder) -> criteriaBuilder
			.isTrue(root.get("single"));
	}
	
	public static Specification<Event> hasTimeBetween(
		java.time.LocalDateTime start,
		java.time.LocalDateTime end
	) {
		return (root, query, criteriaBuilder) -> {
			if (start != null && end != null) {
				return criteriaBuilder.between(root.get("location").get("startTime"), start, end);
			} else if (start != null) {
				return criteriaBuilder.greaterThanOrEqualTo(root.get("location").get("startTime"), start);
			} else if (end != null) {
				return criteriaBuilder.lessThanOrEqualTo(root.get("location").get("startTime"), end);
			} else {
				return criteriaBuilder.conjunction();
			}
		};
	}
}