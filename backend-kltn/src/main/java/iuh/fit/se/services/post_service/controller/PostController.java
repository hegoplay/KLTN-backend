package iuh.fit.se.services.post_service.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import iuh.fit.se.entity.Post;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.post_service.dto.CommentRequestDto;
import iuh.fit.se.services.post_service.dto.CommentResponseDto;
import iuh.fit.se.services.post_service.dto.PostCreateRequestDto;
import iuh.fit.se.services.post_service.dto.PostWrapperDto;
import iuh.fit.se.services.post_service.service.CommentService;
import iuh.fit.se.services.post_service.service.PostService;
import iuh.fit.se.util.PageableUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Validated
@Tag(name = "Post Management", description = "API nãy hỗ trợ các api quản lý bài truyền thông CUD (Create, Update, Delete) và các api yêu cầu JWT")
@SecurityRequirement(name = "bearerAuth")
public class PostController {
	PostService postService;
	iuh.fit.se.services.post_service.mapper.PostMapping postMapper;
	CommentService commentService;
	PagedResourcesAssembler<PostWrapperDto> pagedResourcesAssembler;

	@PostMapping
	@PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN') or hasRole('LEADER')")
	@Operation(summary = "Tạo post", description = "Tạo một bài viết mới. Chỉ có người dùng có vai trò MEMBER, ADMIN hoặc LEADER mới có quyền tạo bài viết.")
	public ResponseEntity<PostWrapperDto> createPost(
		@RequestBody @Valid PostCreateRequestDto dto,
		HttpServletRequest request
	) {
		Post createdPost = postService.createPost(dto);
		PostWrapperDto postWrapperDto = postMapper
			.toPostWrapperDto(createdPost);
		return ResponseEntity.status(HttpStatus.CREATED).body(postWrapperDto);
	}

	@PutMapping("/{postId}")
	@PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN') or hasRole('LEADER')")
	@Operation(summary = "Cập nhật post", description = """
			Cập nhật một bài viết.
			Chỉ có người dùng có vai trò MEMBER, ADMIN hoặc LEADER mới có quyền cập nhật bài viết.
			Ở giai đoạn này chỉ xảy ra trường hợp PENDING, ARCHIVED, DISABLED.
		""")
	// cập nhật trạng thái của post, chỉ có người viết post mới có quyền cập
	// nhật
	public ResponseEntity<PostWrapperDto> updatePost(
		@PathVariable String postId,
		@RequestBody @Valid PostCreateRequestDto dto,
		HttpServletRequest request
	) {
		if (!postService.isPostExist(postId)) {
			return ResponseEntity.notFound().build();
		}
		Post updatedPost = postService.updatePost(postId, dto);
		PostWrapperDto postWrapperDto = postMapper
			.toPostWrapperDto(updatedPost);
		return ResponseEntity.ok(postWrapperDto);
	}

	@DeleteMapping("/{postId}")
	@PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN') or hasRole('LEADER')")
	@Operation(summary = "Xoá post", description = """
		Xoá một bài viết.
		Chỉ có người viết hoặc người role ADMIN hoặc LEADER mới có quyền xoá bài viết.
		""")
	public ResponseEntity<Void> deletePost(
		@PathVariable String postId,
		HttpServletRequest request
	) {
		if (!postService.isPostExist(postId)) {
			return ResponseEntity.notFound().build();
		}
		postService.deletePost(postId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/user/{userId}")
	// api lấy tất cả bài viết của người dùng, bao gồm cả bài viết đã được duyệt
	// và chưa được duyệt
	// chỉ có người dùng đó hoặc người quản trị viên mới có quyền truy cập
	@Operation(summary = "Lấy bài viết của người dùng", description = "Lấy tất cả bài viết của người dùng. Chỉ có người dùng đó hoặc người quản trị viên mới có quyền truy cập.")
	public ResponseEntity<PagedModel<EntityModel<PostWrapperDto>>> getUserPost(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) String sort,
		@PathVariable String userId,
		@RequestParam(required = false) FunctionStatus status,
		@RequestParam(required = false, defaultValue = "") String title
	) {
		Pageable pageable = PageRequest
			.of(page, size, PageableUtil.parseSort(sort));
		Page<Post> allPublicPosts = postService
			.getPostsByUserId(userId, status, title, pageable);

		return ResponseEntity
			.ok(pagedResourcesAssembler
				.toModel(allPublicPosts.map(postMapper::toPostWrapperDto)));
	}

	@PostMapping("/comment")
	@Operation(summary = "Tạo bình luận", description = "Có tài khoản người dùng có thể tạo bình luận cho bài viết.")
	public ResponseEntity<CommentResponseDto> createComment(
		@RequestBody @Valid CommentRequestDto dto,
		HttpServletRequest request
	) {
		CommentResponseDto responseDto = commentService.createComment(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}

	@DeleteMapping("/comment/{commentId}")
	@Operation(summary = "Xoá bình luận", description = "Chỉ có người tạo bình luận hoặc người quản trị viên mới có quyền xoá bình luận.")
	public ResponseEntity<Void> deleteComment(@PathVariable String commentId) {
		commentService.deleteComment(commentId);
		return ResponseEntity.noContent().build();
	}

}
