package iuh.fit.se.services.event_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import iuh.fit.se.entity.EventOrganizer;
import iuh.fit.se.entity.id_class.EventOrganizerId;

public interface EventOrganizerRepository extends JpaRepository<EventOrganizer, EventOrganizerId>{
	
}
