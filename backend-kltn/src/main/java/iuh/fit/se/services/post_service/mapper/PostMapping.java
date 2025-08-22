package iuh.fit.se.services.post_service.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import iuh.fit.se.entity.Post;
import iuh.fit.se.services.post_service.dto.PostCreateRequestDto;
import iuh.fit.se.services.post_service.dto.PostDetailDto;
import iuh.fit.se.services.post_service.dto.PostWrapperDto;
import iuh.fit.se.services.user_service.dto.UserShortInfoResponseDto;
import iuh.fit.se.services.user_service.mapper.UserMapper;
import iuh.fit.se.services.user_service.repository.AttachmentRepository;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class PostMapping {

	@Autowired
	protected UserMapper userMapper;

	@Autowired
	protected AttachmentRepository attachmentRepository;

	public abstract PostWrapperDto toPostWrapperDto(Post post);

	public abstract PostDetailDto toPostDetailDto(Post post);

	public abstract Post toPost(PostCreateRequestDto dto);

	public abstract Post updatePost(
		PostCreateRequestDto dto,
		@MappingTarget Post post
	);

	@AfterMapping
	protected void afterToPostWrapperDto(
		Post post,
		@MappingTarget PostWrapperDto.PostWrapperDtoBuilder dto
	) {
		if (post != null && dto != null && post.getWriter() != null) {
			// Sử dụng UserMapper để map writer
			UserShortInfoResponseDto writerDto = userMapper
				.toShortUserInfoResponseDto(post.getWriter());
			dto.writer(writerDto);
		}

	}

	@AfterMapping
	protected void afterToPost(
		PostCreateRequestDto dto,
		@MappingTarget Post.PostBuilder post
	) {
		if (dto != null && post != null) {
			if (dto.featureImageName() != null) {
				post
					.featureImage(attachmentRepository
						.findById(dto.featureImageName())
						.orElse(null));
			} else {
				post.featureImage(null);
			}
		}
	}
}
