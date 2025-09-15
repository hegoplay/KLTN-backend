package iuh.fit.se.services.training_service.specification;

import org.springframework.data.jpa.domain.Specification;

import iuh.fit.se.entity.Training;
import iuh.fit.se.entity.User;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public class EntitySpecification<T> {
	public static<T> Specification<T> hasTitle(String title) {
		return (root, query, criteriaBuilder) -> criteriaBuilder
			.like(criteriaBuilder.lower(root.get("title")),
				"%" + title.toLowerCase() + "%");
	}
	
	public static<T> Specification<T> hasDescription(String description) {
		return (root, query, criteriaBuilder) -> criteriaBuilder
			.like(criteriaBuilder.lower(root.get("description")),
				"%" + description.toLowerCase() + "%");
	}
	
	public static<T> Specification<T> hasStatus(FunctionStatus status) {
		return (root, query, criteriaBuilder) -> criteriaBuilder
			.equal(root.get("status"), status.name());
	}
	
//	public static<T> Specification<T> hasCreatorId(String creatorId) {
//		return (root, query, criteriaBuilder) -> criteriaBuilder
//			.equal(root.get("creator").get("id"), creatorId);
//	}
	
	public static<T> Specification<T> hasTimeBetween(
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
	
	public static Specification<Training> hasCreatorId(String creatorId) {
		return (root, query, criteriaBuilder) -> criteriaBuilder
			.equal(root.get("creator").get("id"), creatorId);
	}
	
	public static Specification<Training> hasMentorId(String mentorId) {
		return (root, query, criteriaBuilder) -> {
			Join<Training, User> mentorsJoin = root.join("mentors",JoinType.LEFT);
			return criteriaBuilder.equal(mentorsJoin.get("id"), mentorId);
		};
	}

}
