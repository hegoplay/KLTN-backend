package iuh.fit.se.entity;

import java.time.LocalDateTime;

import org.springframework.core.annotation.Order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Table(name = "room_check_in")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@lombok.Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "user")
public class RoomCheckIn {
	@Id
	@GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
	String checkInId;

	@ManyToOne
	@JoinColumn(name = "user_id")
	User user;
	
	@OrderColumn
	LocalDateTime startTime;
	@Builder.Default
	LocalDateTime endTime = LocalDateTime
		.now()
		.withHour(22)
		.withMinute(0)
		.withSecond(0)
		.withNano(0); // Default to 1 hour later
}
