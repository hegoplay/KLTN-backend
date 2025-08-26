package iuh.fit.se.entity.id_class;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class EventOrganizerId implements Serializable {
    private static final long serialVersionUID = 693269534776186984L;
	private String organizerId;
    private String eventId;
    
    // constructors, getters, equals, hashCode
}