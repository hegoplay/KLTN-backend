package iuh.fit.se.services.post_service.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import iuh.fit.se.constant.TimeConstant;
import iuh.fit.se.entity.Post;
import iuh.fit.se.entity.User;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.entity.enumerator.UserRole;
import iuh.fit.se.errorHandler.InputNotFoundException;
import iuh.fit.se.services.post_service.aop.PostPermission;
import iuh.fit.se.services.post_service.aop.PostPermission.ActionType;
import iuh.fit.se.services.post_service.dto.PostRequestDto;
import iuh.fit.se.services.post_service.dto.SearchPostRequestDto;
import iuh.fit.se.services.post_service.mapper.PostMapping;
import iuh.fit.se.services.post_service.repository.PostRepository;
import iuh.fit.se.services.post_service.service.PostService;
import iuh.fit.se.services.user_service.repository.AttachmentRepository;
import iuh.fit.se.services.user_service.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

	PostRepository postRepository;
	PostMapping postMapper;
	UserService userService;
	AttachmentRepository attachmentRepository;

	@Override
	public Page<Post> getAllPublicPosts(
		Pageable pageable,
		SearchPostRequestDto searchPostRequest
	) {
		// TODO Auto-generated method stub
		return postRepository
			.findAllByStatusAndTitleContainingIgnoreCaseAndPostTimeBetween(
				FunctionStatus.ACCEPTED, searchPostRequest.getSearchKeyword(),
				TimeConstant.toLocalDateTime(searchPostRequest.getFromDate()),
				TimeConstant.toLocalDateTime(searchPostRequest.getToDate()),
				pageable);
	}

	@Override
	public boolean isPostExist(String postId) {
		return postRepository.existsById(postId);
	}

	@Override
	@PostPermission(action = ActionType.CREATE)
	@Transactional
	public Post createPost(PostRequestDto postDto) {

		if (!postDto.isCreateAble()) {
			throw new RuntimeException(
				"Post status cannot be DISABLED or ACCEPTED or ACCEPTED when creating a new post");
		}
		Post post = postMapper.toPost(postDto);
		post = updateCurrentUser(post);
		post = updatePostImage(postDto, post);

		Post postResponse = postRepository.save(post);
		log.info("PostDto info :{}", postDto.toString());
		return postResponse;
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ADMIN') or hasRole('LEADER')")
	public Post validatePost(String postId) {
		User user = userService.getCurrentUser();
		if (!user.isLeader()) {
			throw new RuntimeException(
				"User is not authorized to validate post");
		}
		log.info("Validating post with ID: {}", postId);
		Post post = getPostById(postId);
		if (post.getStatus() != FunctionStatus.PENDING) {
			throw new RuntimeException("Post is not pending");
		}
		post.setStatus(FunctionStatus.ACCEPTED);
		return postRepository.save(post);
	}

	@Override
	public Post getAcceptedPostById(String postId) {
		Post post = getPostById(postId);
		if (post.getStatus() != FunctionStatus.ACCEPTED) {
			throw new RuntimeException("Post is not accepted");
		}
		return post;
	}

	private Post getPostById(String postId) {
		return postRepository
			.findById(postId)
			.orElseThrow(() -> new RuntimeException("Post not found"));
	}

	@Override
	@Transactional
	@PostPermission(action = ActionType.UPDATE)
	// hàm này giúp người dùng có thể cập nhật bài viết của mình
	public Post updatePost(String postId, PostRequestDto post) {
		if (FunctionStatus.ableToUpdate(post.status())) {
			throw new RuntimeException(
				"Post status cannot be REJECTED or ACCEPTED when updating a post");
		}
		if (postRepository.existsById(postId)) {
			throw new InputNotFoundException("Post not found");
		}
		Post existingPost = getPostById(postId);
		postMapper.updatePost(post, existingPost);
		existingPost = updatePostImage(post, existingPost);
		
		return postRepository.save(existingPost);
	}

	@Override
	@PostPermission(action = ActionType.GRANT)
//	hàm giúp leader hoặc admin có thể thay đổi trạng thái của bài viết
	public Post modifyPost(String postId, FunctionStatus status) {
		Post post = getPostById(postId);
		if (post.getStatus() != FunctionStatus.PENDING) {
			throw new RuntimeException("Required post status is PENDING");
		}
		post.setStatus(status);
		return postRepository.save(post);
	}

	@Override
	@PostPermission(action = ActionType.DELETE)
	public void deletePost(String postId) {
		if (!isPostExist(postId)) {
			throw new RuntimeException("Post not found");
		}
		postRepository.deleteById(postId);
	}

	@Override
	public Page<Post> getPostsByUserId(
		String userId,
		FunctionStatus status,
		String title,
		Pageable pageable
	) {
		User user = userService.getCurrentUser();
		if (!(user.isLeader() || user.getId().equals(userId))) {
			throw new RuntimeException(
				"User is not authorized to view posts of this user");
		}
		if (status == null) {
			return postRepository
				.findAllByWriter_IdAndTitleContainingIgnoreCase(userId,
					title, pageable);
		} else {
			return postRepository
				.findAllByWriter_IdAndStatusAndTitleContainingIgnoreCase(
					userId, status, title, pageable);
		}
	}

//	hàm này giúp cập nhật ảnh đại diện của bài viết
	 private Post updatePostImage(PostRequestDto postDto, Post existingPost) {
		 if (postDto.featureImageName()!= null && !postDto.featureImageName().isEmpty()) {
			 var attachment = attachmentRepository.findById(postDto.featureImageName())
				 .orElseThrow(() -> new RuntimeException("Attachment not found"));
			 existingPost.setFeatureImage(attachment);
		 } else {
			 existingPost.setFeatureImage(null);
		 }
		 return existingPost;
	 }
	 
//	 hàm này giúp cập nhật người viết bài hiện tại
	 private Post updateCurrentUser(Post existingPost) {
		 User user = userService.getCurrentUser();
		 existingPost.setWriter(user);
		 return existingPost;
	 }
}
