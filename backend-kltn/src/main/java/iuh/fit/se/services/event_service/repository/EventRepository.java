package iuh.fit.se.services.event_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import iuh.fit.se.entity.Event;

@Repository
public interface EventRepository
	extends
		JpaRepository<Event, String>,
		JpaSpecificationExecutor<Event> {
	@Query("SELECT e FROM Event e WHERE TYPE(e) = :eventType")
	Page<Event> findByEventType(
		Class<? extends Event> eventType,
		Pageable pageable
	);
}
