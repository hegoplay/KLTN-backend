package iuh.fit.se.entity;

import iuh.fit.se.entity.enumerator.AttendeeStatus;
import iuh.fit.se.entity.id_class.AttendeeId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Table(name = "attendees")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@lombok.Builder
@IdClass(AttendeeId.class)
@NoArgsConstructor
@AllArgsConstructor
public class Attendee {
	@Id
    @ManyToOne
    @JoinColumn(name = "event_id")
	Event event;
	
	@Id
    @ManyToOne
    @JoinColumn(name = "user_id")
	User user;
	
	@Enumerated(EnumType.STRING)
	AttendeeStatus status;
}
