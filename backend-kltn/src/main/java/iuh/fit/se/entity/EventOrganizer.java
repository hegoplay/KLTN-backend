package iuh.fit.se.entity;

import java.util.HashSet;
import java.util.Set;

import iuh.fit.se.entity.enumerator.OrganizerRole;
import iuh.fit.se.entity.id_class.EventOrganizerId;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Table(name = "event_organizers")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventOrganizer {

    @EmbeddedId
    private EventOrganizerId id;

    @MapsId("organizerId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @MapsId("eventId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "role_content", length = 1000)
    private String roleContent;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(
        name = "organizer_roles",
        joinColumns = {
            @JoinColumn(name = "organizer_id", referencedColumnName = "organizer_id"),
            @JoinColumn(name = "event_id", referencedColumnName = "event_id")
        })
    @Builder.Default
    private Set<OrganizerRole> roles = new HashSet<>();

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