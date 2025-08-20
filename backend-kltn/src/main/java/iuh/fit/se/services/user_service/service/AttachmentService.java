package iuh.fit.se.services.user_service.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import iuh.fit.se.entity.Attachment;

public interface AttachmentService {
	Attachment uploadAttachment(String userId, MultipartFile file);
	
	List<Attachment> getAttachmentsByUserId(String userId);
	
	
}
