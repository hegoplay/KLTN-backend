package iuh.fit.se.services.training_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import iuh.fit.se.entity.Training;

public interface TrainingRepository extends JpaRepository<Training, String> {

}
