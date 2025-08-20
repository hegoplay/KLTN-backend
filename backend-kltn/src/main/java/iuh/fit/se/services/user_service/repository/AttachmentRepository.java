package iuh.fit.se.services.user_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import iuh.fit.se.entity.Attachment;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, String> {
	
	List<Attachment> findByUserUserId(String userId);
}
