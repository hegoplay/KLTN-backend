package iuh.fit.se.services.training_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import iuh.fit.se.entity.Training;
import iuh.fit.se.entity.TrainingEvent;

@Repository
public interface TrainingEventRepository extends JpaRepository<TrainingEvent, String>{
	List<TrainingEvent> findAllByTraining(Training training);
}
