package iuh.fit.se.services.event_service.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import iuh.fit.se.entity.Attendee;
import iuh.fit.se.entity.id_class.AttendeeId;

@Repository
public interface EventAttendeeRepository
	extends
		JpaRepository<Attendee, AttendeeId> {
	Optional<Attendee> findByEventIdAndUserId(String eventId, String userId);
	
	Page<Attendee> findByEventId(String eventId, org.springframework.data.domain.Pageable pageable);
}
