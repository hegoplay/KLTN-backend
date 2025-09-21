package iuh.fit.se.services.post_service.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import iuh.fit.se.entity.Post;
import iuh.fit.se.entity.enumerator.FunctionStatus;

public interface PostRepository extends JpaRepository<Post, String> {

	Page<Post> findAllByStatusAndTitleContainingIgnoreCaseAndPostTimeBetween(
		FunctionStatus status,
		String title,
		LocalDateTime startDate,
		LocalDateTime endDate,
		Pageable pageable
	);
	
	Page<Post> findAllByTitleContainingIgnoreCaseAndPostTimeBetween(
		String title,
		LocalDateTime startDate,
		LocalDateTime endDate,
		Pageable pageable
	);
	
//	hàm này dùng để tìm kiếm bài viết theo userId, status và title
	Page<Post> findAllByWriter_IdAndStatusAndTitleContainingIgnoreCase(
		String userId,
		FunctionStatus status,
		String title,
		Pageable pageable
	);
	
	Page<Post> findAllByWriter_IdAndTitleContainingIgnoreCase(
		String userId,
		String title,
		Pageable pageable
	);
}
