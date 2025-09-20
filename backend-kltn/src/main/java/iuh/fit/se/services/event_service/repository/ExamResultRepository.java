package iuh.fit.se.services.event_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import iuh.fit.se.entity.Contest;
import iuh.fit.se.entity.ExamResult;
import iuh.fit.se.entity.id_class.ExamResultId;

@Repository
public interface ExamResultRepository
		extends
			JpaRepository<ExamResult, ExamResultId> {

	public List<ExamResult> findAllByContest(Contest contest);
}
