package iuh.fit.se.entity.enumerator;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Trạng thái của người tham dự sự kiện")
public enum AttendeeStatus {
	REGISTERED,
	CHECKED,
	BANNED,
//	MISSED,
}
