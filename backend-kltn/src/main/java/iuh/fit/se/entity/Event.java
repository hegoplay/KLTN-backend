package iuh.fit.se.entity;

import java.util.List;

import iuh.fit.se.entity.enumerator.FunctionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Entity
@Table(name = "events")
@DiscriminatorColumn(name = "event_type", discriminatorType = jakarta.persistence.DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class Event {
	
	@Id
	@GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
	@jakarta.persistence.Column(name = "event_id")
	String eventId;
	
	@jakarta.persistence.ManyToOne
	@jakarta.persistence.JoinColumn(name = "host_id")
	User host;
	
	@Embedded
	Location location;
	String title;
	String content;
//	@OneToMany(mappedBy = "event")
//	List<Attendee> attendees;
	
	@OneToMany
	@jakarta.persistence.JoinColumn(name = "event_id")
	List<EventOrganizer> organizers;
	Integer multiple;
	
	@Enumerated(EnumType.STRING)
	FunctionStatus functionStatus = FunctionStatus.ARCHIVED;
	
	boolean isDone = false;

}
