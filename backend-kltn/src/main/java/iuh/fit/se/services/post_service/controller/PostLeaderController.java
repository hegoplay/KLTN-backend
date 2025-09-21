package iuh.fit.se.services.post_service.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import iuh.fit.se.entity.Post;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.post_service.dto.PostWrapperDto;
import iuh.fit.se.services.post_service.dto.SearchPostRequestDto;
import iuh.fit.se.services.post_service.service.PostService;
import iuh.fit.se.util.PageableUtil;
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
@Tag(name = "Post Leader Management", description = "API này hỗ trợ các chức năng phê duyệt và từ chối bài viết cho người dùng có vai trò LEADER.")
@SecurityRequirement(name = "bearerAuth")
public class PostLeaderController {
	PostService postService;
	iuh.fit.se.services.post_service.mapper.PostMapping postMapper;
	PagedResourcesAssembler<PostWrapperDto> pagedResourcesAssembler;

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
	
	
	
	@GetMapping("/search")
	@Operation(summary = "Tìm kiếm bài viết", description = """
		Tìm kiếm các bài viết dựa trên tiêu chí tìm kiếm. 
		Trả về danh sách các bài viết phù hợp với tiêu chí tìm kiếm.
		""")
	public ResponseEntity<PagedModel<EntityModel<PostWrapperDto>>> searchAllPosts(
		@RequestParam(required = false, defaultValue = "") String keyword,
		@Parameter(
	        description = "Trường để xác định đầu khoảng thời gian đăng là lúc nào",
	        example = "2023-01-01",
	        required = false
	    )
		@RequestParam(required = false) LocalDate startDate,
		@Parameter(
	        description = "Trường để xác định cuối khoảng thời gian đăng là lúc nào",
	        example = "2023-12-31",
	        required = false
	    )
		@RequestParam(required = false) LocalDate endDate,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@Parameter(
	        description = "Trường để sắp xếp. Định dạng: fieldName,asc|desc. Ví dụ: createdAt,desc",
	        example = "postTime,desc",
	        required = false
	    )
		@RequestParam(required = false) String sort
	) {
		SearchPostRequestDto searchCriteria = new SearchPostRequestDto(keyword, startDate, endDate);
		
		Pageable pageable = PageRequest
			.of(page, size, PageableUtil.parseSort(sort));
		Page<Post> allPublicPosts = postService
			.getAllPosts(pageable, searchCriteria);

		return ResponseEntity
			.ok(pagedResourcesAssembler
				.toModel(allPublicPosts.map(postMapper::toPostWrapperDto)));
	}
}
