package iuh.fit.se.services.event_service.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import iuh.fit.se.entity.enumerator.AttendeeStatus;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.event_service.dto.enumerator.EventCategory;
import iuh.fit.se.services.user_service.dto.UserShortInfoResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class EventDetailResponseDto {
	String id;
	UserShortInfoResponseDto host;
	LocationDto location;
	String title;
	String content;
	List<EventOrganizerDto> organizers;
	Integer multiple;
	FunctionStatus status;
	Boolean done;
	EventCategory category;
	Boolean isHost;
	EventOrganizerDto userAsOrganizer;
	@Schema(
		description = "Trạng thái tham gia của người dùng hiện tại. Nếu là null thì người dùng chưa đăng ký tham gia sự kiện.",
		example = "PENDING"
	)
	AttendeeStatus userAttendeeStatus;

	// contest serving fields
	List<ExamResultDto> examResults;
	Boolean ableToRegister;

	// training event serving fields
	String trainingId;

	// seminar serving fields
	List<String> reviews;
}
