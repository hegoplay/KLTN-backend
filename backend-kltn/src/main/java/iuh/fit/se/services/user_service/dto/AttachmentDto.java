package iuh.fit.se.services.user_service.dto;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
@lombok.experimental.FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AttachmentDto {
	String name;
	String url;
	String fileType;
	long size;
	long height;
	long width;
}
