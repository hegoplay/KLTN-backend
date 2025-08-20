package iuh.fit.se.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Table(name = "exam_results", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "contest_id"}))
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@lombok.Builder
public class ExamResult {

	@Id
	@Column(name = "exam_result_id")
	@GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
	String id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	User student;

	@ManyToOne
	@JoinColumn(name = "contest_id")
	Contest contest;
	@Column(name = "ranking")
	Integer rank;
	Integer point;
}
