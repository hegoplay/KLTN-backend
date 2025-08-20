package iuh.fit.se.services.post_service.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import iuh.fit.se.entity.Comment;
import iuh.fit.se.entity.Post;
import iuh.fit.se.services.post_service.dto.CommentRequestDto;
import iuh.fit.se.services.post_service.dto.CommentResponseDto;
import iuh.fit.se.services.post_service.dto.PostWrapperDto;
import iuh.fit.se.services.user_service.dto.ShortUserInfoResponseDto;
import iuh.fit.se.services.user_service.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class CommentMapping {

	@Autowired
	private UserMapper userMapper;

	public abstract CommentResponseDto toCommentResponseDto(Comment post);

	public abstract Comment toComment(CommentRequestDto dto);

	@AfterMapping
	protected void afterToPostWrapperDto(
		Comment post,
		@MappingTarget CommentResponseDto dto
	) {
		if (post != null && dto != null && post.getCommenter() != null) {
			// Sử dụng UserMapper để map writer
			ShortUserInfoResponseDto writerDto = userMapper
				.toShortUserInfoResponseDto(post.getCommenter());
			dto.setCommenter(writerDto);
		}

	}

}
