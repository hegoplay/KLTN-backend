package iuh.fit.se.entity;

import java.util.HashSet;
import java.util.Set;

import iuh.fit.se.entity.enumerator.OrganizerRole;
import iuh.fit.se.entity.id_class.EventOrganizerId;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Table(
	name = "event_organizers",
	uniqueConstraints = {@UniqueConstraint(
		name = "uk_event_organizer_user_event",
		columnNames = {"user_id", "event_id"})})
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@lombok.Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(EventOrganizerId.class) // Thêm IdClass
public class EventOrganizer {

	@Id
	@Column(name = "organizer_id")
	String organizerId;

	@Id
	@Column(name = "event_id")
	String eventId;

	@ManyToOne
	@JoinColumn(name = "organizer_id", insertable = false, updatable = false)
	User organizer;

	@ManyToOne
	@JoinColumn(name = "event_id", insertable = false, updatable = false)
	Event event;
	String roleContent;

	@ElementCollection
	@Enumerated(EnumType.STRING) // Lưu dưới dạng chuỗi
	@CollectionTable(
		name = "organizer_roles",
		joinColumns = {@JoinColumn(name = "organizer_id"),
				@JoinColumn(name = "event_id")})
	@Builder.Default
	Set<OrganizerRole> roles = new HashSet<>();

	public void addRole(OrganizerRole role) {
		if (this.roles == null) {
			this.roles = new HashSet<>();
		}
		this.roles.add(role);
	}

	public void removeRole(OrganizerRole role) {
		if (this.roles != null) {
			this.roles.remove(role);
		}
	}

}
