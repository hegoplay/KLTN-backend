package iuh.fit.se.services.user_service.serviceImpl;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import iuh.fit.se.entity.Attachment;
import iuh.fit.se.entity.User;
import iuh.fit.se.services.user_service.repository.AttachmentRepository;
import iuh.fit.se.services.user_service.service.AttachmentService;
import iuh.fit.se.services.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@Profile("!prod")
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class LocalAttachmentServiceImpl implements AttachmentService {

	AttachmentRepository attachmentRepository;
	UserService userService;

	@Override
	public Attachment uploadAttachment(String userId, MultipartFile file) {
		User user = userService.getUserById(userId);
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("File cannot be null or empty");
		}
		Attachment attachment = Attachment
			.builder()
			.user(user)
			.name(file.getOriginalFilename() + "_" + System.currentTimeMillis())
			.fileType(file.getContentType())
			.height(200)
			.width(200)
			.url(
				"https://images.steamusercontent.com/ugc/2020477638665804166/24A9F61D7CCB7CBB1115DE608CF491A59848BBE5/?imw=512&imh=515&ima=fit&impolicy=Letterbox&imcolor=%23000000&letterbox=true")
			.size(file.getSize())
			.build();
		attachment = attachmentRepository.save(attachment);
		return attachment;
	}

	@Override
	public List<Attachment> getAttachmentsByUserId(String userId) {
		userService.getUserById(userId);
		return attachmentRepository.findByUser_Id(userId);
	}

	@Override
	public void deleteAttachment(String name) {
		// TODO Auto-generated method stub
		attachmentRepository.deleteById(name);
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
		Attachment attachment = getAttachmentByName(name);
		if (!attachment.getUser().getId()
			.equals(userService.getCurrentUser().getId())) {
			throw new IllegalArgumentException(
				"You are not allowed to delete this attachment");
		}
		attachmentRepository.deleteById(name);		
	}

}
