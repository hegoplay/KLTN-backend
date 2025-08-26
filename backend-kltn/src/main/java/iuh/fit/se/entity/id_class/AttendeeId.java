package iuh.fit.se.entity.id_class;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendeeId implements java.io.Serializable {
	private static final long serialVersionUID = -1869379599143120689L;
	private String userId;
	private String eventId;
}
