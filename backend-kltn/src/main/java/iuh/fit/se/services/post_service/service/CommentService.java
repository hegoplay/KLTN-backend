package iuh.fit.se.services.post_service.service;

import iuh.fit.se.services.post_service.dto.CommentRequestDto;
import iuh.fit.se.services.post_service.dto.CommentResponseDto;

public interface CommentService {
	CommentResponseDto createComment(CommentRequestDto dto);
	CommentResponseDto getCommentById(String commentId);
	
	void deleteComment(String commentId);
}
