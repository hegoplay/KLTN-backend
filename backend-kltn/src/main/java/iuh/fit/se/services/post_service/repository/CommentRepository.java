package iuh.fit.se.services.post_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import iuh.fit.se.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, String> {
	
}
