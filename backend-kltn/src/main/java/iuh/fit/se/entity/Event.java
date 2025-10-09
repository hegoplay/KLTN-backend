package iuh.fit.se.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import iuh.fit.se.entity.enumerator.AttendeeStatus;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.entity.id_class.EventOrganizerId;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Entity
@Table(name = "events")
@DiscriminatorColumn(
	name = "event_type",
	discriminatorType = jakarta.persistence.DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.JOINED)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public abstract class Event {

	@Id
	@GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
	@jakarta.persistence.Column(name = "event_id")
	@EqualsAndHashCode.Include
	String id;

	@jakarta.persistence.JoinColumn(name = "host_id")
	@ManyToOne(cascade = CascadeType.PERSIST)
	User host;

	@Column(name = "host_id", insertable = false, updatable = false)
	String hostId;

	@Embedded
	Location location;
	@Column(nullable = false)
	String title;
	@Column(columnDefinition = "TEXT")
	String description;

	@OneToMany(
		mappedBy = "event",
		cascade = jakarta.persistence.CascadeType.ALL,
		orphanRemoval = true,
		fetch = FetchType.LAZY)
	@Builder.Default
	@MapKey(name = "userId") // Sử dụng user ID làm key
	Map<String, Attendee> attendeesMap = new HashMap<>();

	@OneToMany(
		mappedBy = "event",
		cascade = jakarta.persistence.CascadeType.ALL,
		orphanRemoval = true,
		fetch = FetchType.LAZY)
	@Builder.Default
	@ToString.Exclude
	List<EventOrganizer> organizers = new ArrayList<>();
	Integer multiple;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	@Builder.Default
	FunctionStatus status = FunctionStatus.ARCHIVED;

	boolean done = false;

	LocalDateTime doneTime;

	Boolean single;

	protected abstract boolean isSingleTable();

	@PrePersist
	@PreUpdate
	private void initializedIsSingle() {
		if (single == null) {
			this.single = Boolean.valueOf(isSingleTable());
		}
	}

	public boolean checkAttendeeExists(String userId) {
		return this.attendeesMap != null
			&& this.attendeesMap.containsKey(userId);
	}

	public void addAttendee(Attendee attendee) {
		if (this.attendeesMap == null) {
			this.attendeesMap = new HashMap<>();
		}
		attendee.setEvent(this);
		this.attendeesMap.put(attendee.getUser().getId(), attendee);
	}

	public void removeAttendee(Attendee attendee) {
		if (this.attendeesMap != null) {
			this.attendeesMap.remove(attendee.getUserId());
			attendee.setEvent(null);
			attendee.setEventId(null);
		}
	}

	public void addOrganizer(EventOrganizer organizer) {
		if (this.organizers == null) {
			this.organizers = new ArrayList<>();
		}
		organizer.setEvent(this);
	    organizer.setOrganizer(organizer.getOrganizer());
	    if (organizer.getId() == null) {
	        EventOrganizerId id = new EventOrganizerId(
	            organizer.getOrganizer().getId(), 
	            this.getId()
	        );
	        organizer.setId(id);
	    }
		this.organizers.add(organizer);
	}

	public void removeOrganizer(EventOrganizer organizer) {
		if (this.organizers != null) {
			this.organizers.remove(organizer);
			organizer.setEvent(null);
		}
	}

	public void checkIn(String userId) {
		if (this.attendeesMap != null
			&& this.attendeesMap.containsKey(userId)) {
			Attendee attendee = this.attendeesMap.get(userId);
			if (attendee
				.getStatus() == iuh.fit.se.entity.enumerator.AttendeeStatus.CHECKED) {
				throw new IllegalStateException(
					"User with ID " + userId + " has already checked in.");
			}
			if (attendee
				.getStatus() == iuh.fit.se.entity.enumerator.AttendeeStatus.BANNED) {
				throw new IllegalStateException(
					"User with ID " + userId + " is banned from this event.");
			}
			attendee
				.setStatus(iuh.fit.se.entity.enumerator.AttendeeStatus.CHECKED);
		} else {
			throw new IllegalArgumentException("User with ID " + userId
				+ " is not an attendee of this event.");
		}

	}

	public void cancelRegistration(String userId) {
		if (this.attendeesMap != null
			&& this.attendeesMap.containsKey(userId)) {
			Attendee attendee = this.attendeesMap.get(userId);
			if (attendee.getStatus() == AttendeeStatus.CHECKED) {
				throw new IllegalStateException("User with ID " + userId
					+ " has already checked in and cannot cancel registration.");
			}
			if (attendee.getStatus() == AttendeeStatus.BANNED) {
				throw new IllegalStateException("User with ID " + userId
					+ " is banned from this event and cannot cancel registration.");
			}
			removeAttendee(attendee);
		} else {
			throw new IllegalArgumentException("User with ID " + userId
				+ " is not an attendee of this event.");
		}
	}

	public EventOrganizer getOrganizerByUserId(String userId) {
		if (this.organizers != null) {
			for (EventOrganizer organizer : this.organizers) {
				if (organizer.getOrganizer().getId().equals(userId)) {
					return organizer;
				}
			}
		}
		return null;
	}

	public Attendee getAttendeeByUserId(String userId) {
		if (this.attendeesMap != null
			&& this.attendeesMap.containsKey(userId)) {
			return this.attendeesMap.get(userId);
		}
		return null;
	}

}
