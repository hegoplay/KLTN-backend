package iuh.fit.se.entity.id_class;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class AttendeeId implements java.io.Serializable {
	private static final long serialVersionUID = -1869379599143120689L;
	private String user;
	private String event;
}
