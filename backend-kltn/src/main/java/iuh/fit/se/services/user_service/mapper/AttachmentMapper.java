package iuh.fit.se.services.user_service.mapper;

import org.mapstruct.Mapper;

import iuh.fit.se.services.user_service.dto.AttachmentDto;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {
	AttachmentDto toAttachmentDto(iuh.fit.se.entity.Attachment attachment);
}
