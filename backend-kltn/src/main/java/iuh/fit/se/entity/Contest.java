package iuh.fit.se.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Entity(name = "contests")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class Contest extends Event {

	@OneToMany(
		mappedBy = "contest",
		cascade = jakarta.persistence.CascadeType.ALL,
		orphanRemoval = true,
		fetch = jakarta.persistence.FetchType.LAZY
	)
	@Builder.Default	
	List<ExamResult> examResults = new java.util.ArrayList<>();

	@Builder.Default
	boolean ableToRegister = true;

	@Override
	public boolean isSingleTable() {
		return true;
	}
}
