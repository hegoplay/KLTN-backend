package iuh.fit.se.services.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import iuh.fit.se.entity.Contest;
import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	Optional<User> findByUsername(String username);
	Optional<User> findByUsernameOrEmail(String username, String email);
	@Modifying
	@Query("UPDATE User u SET u.attendancePoint = u.attendancePoint + :increment "
		+ "WHERE u IN (SELECT a.user FROM Attendee a "
		+ "WHERE a.event = :event AND a.status = iuh.fit.se.entity.enumerator.AttendeeStatus.CHECKED)")
	int incrementScoreForEventAttendees(
		@Param("event") Event event,
		@Param("increment") int increment
	);

	@Modifying
	@Query("UPDATE User u SET u.contributionPoint = u.contributionPoint + :increment "
		+ "WHERE u IN (SELECT e.organizer FROM EventOrganizer e "
		+ "WHERE e.event = :event )")
	int incrementScoreForEventOrganizers(
		@Param("event") Event event,
		@Param("increment") int increment
	);

	@Modifying
	@Query("UPDATE ExamResult ex SET ex.student.attendancePoint = ex.student.attendancePoint + :increment * point "
		+ "WHERE ex.contest = :contest")
	int incrementScoreForContestWinners(
		@Param("contest") Contest contest,
		@Param("increment") int increment
	);

	@Modifying
	@Query("UPDATE User u SET u.contributionPoint = 0")
	void resetAllContributionPoint();
	
	@Modifying
	@Query("UPDATE User u SET u.attendancePoint = 0")
	void resetAllAttendancePoint();
}
