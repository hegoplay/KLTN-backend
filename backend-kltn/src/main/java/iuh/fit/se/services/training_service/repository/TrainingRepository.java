package iuh.fit.se.services.training_service.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import iuh.fit.se.entity.Training;

@Repository
public interface TrainingRepository
	extends
		JpaRepository<Training, String>,
		JpaSpecificationExecutor<Training> {

	Page<Training> findAllByCreator_Id(String creatorId, Pageable pageable);

	@Query("SELECT t FROM Training t LEFT JOIN FETCH t.participants WHERE t.id = :id")
	Optional<Training> findByIdFetchParticipants(String id);

	@Query("SELECT t FROM Training t LEFT JOIN FETCH t.mentors WHERE t.id = :id")
	Optional<Training> findByIdFetchMentors(String id);

	@Query("""
		SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
		FROM Training t
		JOIN t.participants p
		WHERE p.id = :participantId AND t.id = :trainingId
		""")
	boolean participantsExistByIdAndTraining_Id(
		String participantId,
		String trainingId
	);
}
