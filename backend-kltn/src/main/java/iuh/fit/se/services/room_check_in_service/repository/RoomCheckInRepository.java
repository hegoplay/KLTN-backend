package iuh.fit.se.services.room_check_in_service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import iuh.fit.se.entity.RoomCheckIn;

@Repository
public interface RoomCheckInRepository extends JpaRepository<RoomCheckIn, String> {
	
//	find latest check-in by user ID
	Optional<RoomCheckIn> findFirstByUser_IdAndStartTimeAfterOrderByStartTimeDesc(String userId,LocalDateTime startTime);
	
	Page<RoomCheckIn> findByUser_Id(String userId, Pageable pageable);
}
