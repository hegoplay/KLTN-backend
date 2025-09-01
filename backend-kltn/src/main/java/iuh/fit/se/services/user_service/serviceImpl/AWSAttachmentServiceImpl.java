package iuh.fit.se.services.user_service.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import iuh.fit.se.entity.Attachment;
import iuh.fit.se.services.user_service.repository.AttachmentRepository;
import iuh.fit.se.services.user_service.repository.UserRepository;
import iuh.fit.se.services.user_service.service.AttachmentService;
import iuh.fit.se.services.user_service.service.UserService;
import iuh.fit.se.util.S3Resource;
import iuh.fit.se.util.TokenContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Profile("prod")
@RequiredArgsConstructor
@Slf4j
public class AWSAttachmentServiceImpl implements AttachmentService {

	@Value("${aws.region}")
	private String region;

	@Value("${aws.s3.bucket}")
	private String s3BucketUrl;

	@Value("${aws.s3.bucket}")
	private String bucketName;

	private final AttachmentRepository attachmentRepository;
	private final UserRepository userRepository;
	private final UserService userService;
	private final S3Service s3Service;
	private final TokenContextUtil tokenContextUtil;

	@Override
	@Transactional
	public Attachment uploadAttachment(String userId, MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("File cannot be null or empty");
		}
		String keyName = System.currentTimeMillis() + "_"
			+ file.getOriginalFilename();
		try (S3Resource s3Resource = s3Service.uploadToS3(file, keyName)) {
			// Thực hiện các operations khác
			// Nếu mọi thứ thành công, commit resource
			Attachment attachment = Attachment
				.builder()
				.user(userRepository.getReferenceById(userId))
				.name(keyName)
				.fileType(file.getContentType())
				.height(200)
				.width(200)
				.url(s3Resource.getFileUrl())
				.size(file.getSize())
				.build();
			attachment = attachmentRepository.save(attachment);
			s3Resource.commit();
			return attachment;

		} catch (Exception e) {
			throw new RuntimeException("Failed to upload file to S3", e);
		}

	}

	@Override
	public List<Attachment> getAttachmentsByUserId(String userId) {
		userService.getUserById(userId);
		return attachmentRepository.findByUser_Id(userId);
	}

	@Override
	@Transactional
	public void deleteAttachment(String name) {
		attachmentRepository.deleteById(name);
		s3Service.deleteFile(name);

	}

	@Override
	public Attachment getAttachmentByName(String name) {
		// TODO Auto-generated method stub
		return attachmentRepository
			.findById(name)
			.orElseThrow(
				() -> new IllegalArgumentException("Attachment not found"));
	}

	@Override
	public void deleteMyAttachment(String name) {
		String userId = tokenContextUtil.getUserId();
		Attachment attachment = getAttachmentByName(name);
		if (!attachment.getUser().getId().equals(userId)) {
			throw new IllegalArgumentException(
				"Bạn không có quyền xóa tệp đính kèm này");
		}
		deleteAttachment(name);
	}
}
