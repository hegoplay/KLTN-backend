package iuh.fit.se.services.post_service.serviceImpl;

import org.springframework.stereotype.Service;

import iuh.fit.se.entity.Comment;
import iuh.fit.se.services.post_service.aop.CommentPermission;
import iuh.fit.se.services.post_service.aop.CommentPermission.ActionType;
import iuh.fit.se.services.post_service.dto.CommentRequestDto;
import iuh.fit.se.services.post_service.dto.CommentResponseDto;
import iuh.fit.se.services.post_service.mapper.CommentMapping;
import iuh.fit.se.services.post_service.repository.CommentRepository;
import iuh.fit.se.services.post_service.service.CommentService;
import iuh.fit.se.services.post_service.service.PostService;
import iuh.fit.se.services.user_service.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
	CommentRepository commentRepository;
	CommentMapping commentMapper;
	UserService userService;
	PostService postService;
	
	@Override
	public CommentResponseDto createComment(CommentRequestDto dto) {
		// TODO Auto-generated method stub
		Comment comment = commentMapper.toComment(dto);
		comment.setCommenter(userService.getCurrentUser());
		comment.setPost(postService.getAcceptedPostById(dto.postId()));
		comment = commentRepository.save(comment);
		return commentMapper.toCommentResponseDto(comment);
		
	}

	@Override
	public CommentResponseDto getCommentById(String commentId) {
		// TODO Auto-generated method stub
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new RuntimeException("Comment not found"));
		return commentMapper.toCommentResponseDto(comment);
	}

	@Override
	@CommentPermission(action = ActionType.DELETE, commentIdParam = "commentId")
	public void deleteComment(String commentId) {
		// TODO Auto-generated method stub
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new RuntimeException("Comment not found"));
		commentRepository.delete(comment);
	}

}
