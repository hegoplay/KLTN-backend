package iuh.fit.se.entity;

import java.util.List;

import iuh.fit.se.entity.enumerator.OrganizerRole;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Table(
    name = "event_organizers",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_event_organizer_user_event",
            columnNames = {"user_id", "event_id"}
        )
    }
)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@lombok.Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventOrganizer {
	@Id
	@GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
	String organizerId;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	User organizer;
	
	@ManyToOne
	@JoinColumn(name = "event_id")
	Event event;
	String roleContent;
	
	@ElementCollection
    @Enumerated(EnumType.STRING) // Lưu dưới dạng chuỗi
    @CollectionTable(
        name = "organizer_roles", 
        joinColumns = @JoinColumn(name = "organizer_id")
    )
	List<OrganizerRole> roles;
	
	
}
