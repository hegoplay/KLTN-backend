package iuh.fit.se.services.event_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.EventOrganizer;
import iuh.fit.se.entity.id_class.EventOrganizerId;

@Repository
public interface EventOrganizerRepository extends JpaRepository<EventOrganizer, EventOrganizerId>{
	List<EventOrganizer> findAllByEvent(Event event);
}
