package iuh.fit.se.services.post_service.service;

import iuh.fit.se.services.post_service.dto.CommentCreateRequestDto;
import iuh.fit.se.services.post_service.dto.CommentResponseDto;

public interface CommentService {
	CommentResponseDto createComment(CommentCreateRequestDto dto);
	CommentResponseDto getCommentById(String commentId);
	
	void deleteComment(String commentId);
	
	boolean isCommentExists(String commentId);
	
	CommentResponseDto updateComment(String commentId, String content, String userId);
}
