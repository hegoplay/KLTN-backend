package iuh.fit.se.services.post_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import iuh.fit.se.entity.Post;
import iuh.fit.se.services.post_service.dto.PostDetailDto;
import iuh.fit.se.services.post_service.dto.PostRequestDto;
import iuh.fit.se.services.post_service.dto.PostWrapperDto;
import iuh.fit.se.services.user_service.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface PostMapping {

//	@Autowired
//	protected UserMapper userMapper;
//	
//
//	@Autowired
//	protected AttachmentRepository attachmentRepository;

	public PostWrapperDto toPostWrapperDto(Post post);

	public PostDetailDto toPostDetailDto(Post post);

	public Post toPost(PostRequestDto dto);

	public Post updatePost(
		PostRequestDto dto,
		@MappingTarget Post post
	);

	
}
