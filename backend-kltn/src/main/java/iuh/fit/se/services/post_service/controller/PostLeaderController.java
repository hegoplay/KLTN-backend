package iuh.fit.se.services.post_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import iuh.fit.se.entity.Post;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.post_service.dto.PostWrapperDto;
import iuh.fit.se.services.post_service.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/leader/posts")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Tag(name = "Post Management with LEADER role", description = "API này hỗ trợ các chức năng phê duyệt và từ chối bài viết cho người dùng có vai trò LEADER.")
@SecurityRequirement(name = "bearerAuth")
public class PostLeaderController {
	PostService postService;
	iuh.fit.se.services.post_service.mapper.PostMapping postMapper;

	@PatchMapping("/{postId}/approve")
	@Operation(summary = "chấp nhận bài viết và cho lên hệ thống", description = """
			Phê duyệt bài viết, chỉ có leader mới có quyền phê duyệt bài viết.
		""")
	public ResponseEntity<PostWrapperDto> approvePost(
		@PathVariable String postId,
		HttpServletRequest request
	) {
		if (!postService.isPostExist(postId)) {
			return ResponseEntity.notFound().build();
		}
		Post approvedPost = postService
			.modifyPost(postId, FunctionStatus.ACCEPTED);
		PostWrapperDto postWrapperDto = postMapper
			.toPostWrapperDto(approvedPost);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(postWrapperDto);
	}

	@PatchMapping("/{postId}/reject")
	@Operation(summary = "chấp nhận bài viết và cho lên hệ thống", description = """
			Không phê duyệt bài viết, chỉ có leader mới có quyền từ chối bài viết. 
			Bài viết vẫn được hiện thị trong danh sách hiện tại nhưng không được công khai.
		""")
	public ResponseEntity<PostWrapperDto> rejectPost(
		@PathVariable String postId,
		HttpServletRequest request
	) {
		if (!postService.isPostExist(postId)) {
			return ResponseEntity.notFound().build();
		}
		Post rejectedPost = postService
			.modifyPost(postId, FunctionStatus.REJECTED);
		PostWrapperDto postWrapperDto = postMapper
			.toPostWrapperDto(rejectedPost);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(postWrapperDto);
	}

}
