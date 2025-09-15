package iuh.fit.se.services.event_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Schema(description = "DTO dùng để tạo mới sự kiện")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TrainingEventCreateRequestDto extends BaseEventCreateRequestDto {
	@Schema(
		description = "ID của khóa đào tạo liên kết với sự kiện này",
		example = "uuid training",
		requiredMode = Schema.RequiredMode.REQUIRED)
	private String trainingId;
	
	public static TrainingEventCreateRequestDto fromBaseEventDto(BaseEventCreateRequestDto baseEventCreateRequestDto, String trainingId) {
		return TrainingEventCreateRequestDto.builder()
			.title(baseEventCreateRequestDto.getTitle())
			.description(baseEventCreateRequestDto.getDescription())
			.location(baseEventCreateRequestDto.getLocation())
			.multiple(baseEventCreateRequestDto.getMultiple())
			.organizers(baseEventCreateRequestDto.getOrganizers())
			.trainingId(trainingId)
			.build();
	}
}
