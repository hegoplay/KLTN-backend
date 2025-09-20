package iuh.fit.se.entity.id_class;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class ExamResultId {
	private String contestId;
	private String studentId;
	
	// constructors, getters, equals, hashCode
}
