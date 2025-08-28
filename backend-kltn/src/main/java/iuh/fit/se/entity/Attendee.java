package iuh.fit.se.entity;

import iuh.fit.se.entity.enumerator.AttendeeStatus;
import iuh.fit.se.entity.id_class.AttendeeId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Table(name = "attendees")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@lombok.Builder
@IdClass(AttendeeId.class)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Attendee {

	@Id
	@Column(name = "event_id")
	@EqualsAndHashCode.Include
	String eventId;

	@Id
	@Column(name = "user_id")
	@EqualsAndHashCode.Include
	String userId;

	@ManyToOne
	@JoinColumn(name = "event_id", insertable = false, updatable = false)
	@MapsId("eventId")
	@ToString.Exclude
	Event event;

	@ManyToOne
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	@MapsId("userId")
	@ToString.Exclude
	User user;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	AttendeeStatus status = AttendeeStatus.REGISTERED;
	
	public void checkIn() {
		if (this.status == AttendeeStatus.REGISTERED) {
			this.status = AttendeeStatus.CHECKED;
		}
		else if (this.status == AttendeeStatus.CHECKED) {
			this.status = AttendeeStatus.CHECKED; // idempotent
		}
		else {
			throw new IllegalStateException("Cannot check-in. Current status: " + this.status);
		}
	}
	
	public void toggleCheckIn() {
		if (this.status == AttendeeStatus.REGISTERED) {
			this.status = AttendeeStatus.CHECKED;
		}
		else if (this.status == AttendeeStatus.CHECKED) {
			this.status = AttendeeStatus.REGISTERED;
		}
		else {
			throw new IllegalStateException("Cannot toggle check-in. Current status: " + this.status);
		}
	}
}
