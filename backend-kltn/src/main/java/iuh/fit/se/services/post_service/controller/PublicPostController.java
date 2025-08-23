package iuh.fit.se.services.post_service.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import iuh.fit.se.entity.Post;
import iuh.fit.se.services.post_service.dto.PostDetailDto;
import iuh.fit.se.services.post_service.dto.PostWrapperDto;
import iuh.fit.se.services.post_service.dto.SearchPostRequestDto;
import iuh.fit.se.services.post_service.service.PostService;
import iuh.fit.se.util.PageableUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/public/posts")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Tag(name = "Public Post Function management", description = """
	API ày hỗ trợ các chức năng tìm kiếm bài viết công khai và lấy thông tin chi tiết của bài viết.
	""")
public class PublicPostController {

	PagedResourcesAssembler<PostWrapperDto> pagedResourcesAssembler;
	PostService postService;
	iuh.fit.se.services.post_service.mapper.PostMapping postMapper;

	@GetMapping("/{id}")
	@Operation(summary = "Lấy thông tin chi tiết của bài viết", description = """
		Lấy thông tin chi tiết của một bài viết công khai theo ID. Nếu bài viết không tồn tại, trả về mã lỗi 404.
		""")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy thông tin bài viết thành công"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Bài viết không tồn tại")
	})
	public ResponseEntity<PostDetailDto> getPostById(@PathVariable String id) {
		if (!postService.isPostExist(id)) {
			return ResponseEntity.notFound().build();
		}
		Post post = postService.getAcceptedPostById(id);
		PostDetailDto postDetailDto = postMapper.toPostDetailDto(post);
		return ResponseEntity.ok(postDetailDto);
	}

	@GetMapping("/search")
	@Operation(summary = "Tìm kiếm bài viết công khai", description = """
		Tìm kiếm các bài viết công khai dựa trên tiêu chí tìm kiếm. 
		Trả về danh sách các bài viết phù hợp với tiêu chí tìm kiếm.
		""")
	
	public ResponseEntity<PagedModel<EntityModel<PostWrapperDto>>> searchPosts(
		@RequestParam(required = false) String keyword,
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
			.getAllPublicPosts(pageable, searchCriteria);

		return ResponseEntity
			.ok(pagedResourcesAssembler
				.toModel(allPublicPosts.map(postMapper::toPostWrapperDto)));
	}

}
