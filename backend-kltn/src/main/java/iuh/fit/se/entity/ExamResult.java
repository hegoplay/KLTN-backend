package iuh.fit.se.entity;

import iuh.fit.se.entity.id_class.ExamResultId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Table(name = "exam_results")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@lombok.Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResult {

	@EmbeddedId
	ExamResultId examResultId;

	@ManyToOne
	@MapsId("studentId") // Maps the studentId attribute of embedded id
	@JoinColumn(name = "student_id", insertable = false, updatable = false)
	User student;

	@ManyToOne
	@JoinColumn(name = "contest_id", insertable = false, updatable = false)
	@MapsId("contestId") // Maps the contestId attribute of embedded id
	Contest contest;
	@Column(name = "ranking")
	Integer rank;
	Integer point;
}
