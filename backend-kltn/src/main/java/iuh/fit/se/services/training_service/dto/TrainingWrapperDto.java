package iuh.fit.se.services.training_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import iuh.fit.se.common.dto.LocationDto;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.user_service.dto.UserShortInfoResponseDto;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.Getter
@lombok.Setter
@SuperBuilder
@lombok.experimental.FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@EqualsAndHashCode
@lombok.ToString
public class TrainingWrapperDto {
	@EqualsAndHashCode.Include
	String id;
	String title;
	LocationDto location;
	FunctionStatus status;
	UserShortInfoResponseDto creator;
	Integer limitRegister;
	@Schema(description = "Số lượng người dùng đã đăng ký tham gia training")
	Integer currentRegistered;
}
