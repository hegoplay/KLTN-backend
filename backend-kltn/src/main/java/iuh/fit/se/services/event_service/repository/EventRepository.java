package iuh.fit.se.services.event_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import iuh.fit.se.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {

}
