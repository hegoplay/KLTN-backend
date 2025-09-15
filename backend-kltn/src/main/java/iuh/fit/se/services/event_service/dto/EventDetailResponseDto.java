package iuh.fit.se.services.event_service.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import iuh.fit.se.entity.enumerator.AttendeeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "DTO dùng để trả về chi tiết thông tin sự kiện")
@ToString(callSuper = true)
public class EventDetailResponseDto extends EventWrapperDto {
	List<EventOrganizerDto> organizers;
	Boolean isHost;
	EventOrganizerDto userAsOrganizer;
	@Schema(
		description = "Trạng thái tham gia của người dùng hiện tại. Nếu là null thì người dùng chưa đăng ký tham gia sự kiện.",
		example = "PENDING"
	)
	AttendeeStatus userAttendeeStatus;

	// contest serving fields
	@Schema(
		description = "Danh sách kết quả thi của người dùng hiện tại. Nếu là null thì người dùng chưa tham gia thi.",
		requiredMode = Schema.RequiredMode.NOT_REQUIRED
	)
	List<ExamResultDto> examResults;
	Boolean ableToRegister;

	// training event serving fields
	String trainingId;

	// seminar serving fields
	List<String> reviews;
}
