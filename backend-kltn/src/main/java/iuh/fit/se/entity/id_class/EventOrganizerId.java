package iuh.fit.se.entity.id_class;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@Embeddable
public class EventOrganizerId implements Serializable {
    private static final long serialVersionUID = 693269534776186984L;
    @Column(name = "organizer_id")
	private String organizerId;
    @Column(name = "event_id")
    private String eventId;
    
    // constructors, getters, equals, hashCode
}