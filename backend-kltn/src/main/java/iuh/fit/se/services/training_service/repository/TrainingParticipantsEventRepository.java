package iuh.fit.se.services.training_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import iuh.fit.se.entity.User;

@Repository
public interface TrainingParticipantsEventRepository extends JpaRepository<User, String>{
	@Query("SELECT u FROM Training t JOIN t.participants u WHERE t.id = :trainingId")
    Page<User> findParticipantsByTrainingId(@Param("trainingId") String trainingId, Pageable pageable);
}