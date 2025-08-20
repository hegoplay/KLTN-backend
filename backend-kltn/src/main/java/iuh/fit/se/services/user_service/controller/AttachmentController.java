package iuh.fit.se.services.user_service.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import iuh.fit.se.entity.Attachment;
import iuh.fit.se.services.user_service.aop.MediaFile;
import iuh.fit.se.services.user_service.dto.AttachmentDto;
import iuh.fit.se.services.user_service.dto.ListAttachmentDtoResponse;
import iuh.fit.se.services.user_service.mapper.AttachmentMapper;
import iuh.fit.se.services.user_service.service.AttachmentService;
import iuh.fit.se.util.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/attachments")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Tag(name = "Auth Management", description = """
		API này hỗ trợ các chức năng tải lên và lấy thông tin tệp đính kèm của người dùng.
	""")
@SecurityRequirement(name = "bearerAuth")
public class AttachmentController {

	AttachmentService attachmentService;
	AttachmentMapper attachmentMapper;
	JwtTokenUtil jwtTokenUtil;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(
	    summary = "Tải lên tệp đính kèm",
	    description = "Tải lên tệp đính kèm cho người dùng hiện tại. Chỉ chấp nhận file media (hình ảnh, video, audio)."
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		description = "Tệp đính kèm cần tải lên",
		required = true
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "Tệp đính kèm đã được tải lên thành công"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "Yêu cầu không hợp lệ, có thể do tệp quá lớn hoặc định dạng không hỗ trợ"
		)
	})
	public ResponseEntity<AttachmentDto> uploadAttachment(
		MultipartFile file,
		HttpServletRequest request
	) {
		String userId = jwtTokenUtil
			.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest(request));
		Attachment attachment = attachmentService
			.uploadAttachment(userId, file);

		return ResponseEntity.ok(attachmentMapper.toAttachmentDto(attachment));
	}
	@GetMapping("/me")
	@io.swagger.v3.oas.annotations.Operation(
		summary = "Lấy danh sách tệp đính kèm của người dùng hiện tại",
		description = "Lấy danh sách tất cả các tệp đính kèm đã tải lên bởi người dùng hiện tại."
	)
	public ResponseEntity<ListAttachmentDtoResponse> getCurrentUserAttachment(
		@Parameter(
	        description = "Tệp media cần tải lên (hình ảnh, video, audio)",
	        required = true
	    )

		@MediaFile(maxSize = 20 * 1024 * 1024)
		HttpServletRequest request
	) {
		String userId = jwtTokenUtil
			.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest(request));
		List<AttachmentDto> attachments = attachmentService
			.getAttachmentsByUserId(userId)
			.stream()
			.map(attachmentMapper::toAttachmentDto)
			.toList();

		return ResponseEntity.ok(new ListAttachmentDtoResponse(attachments));
	}
}
