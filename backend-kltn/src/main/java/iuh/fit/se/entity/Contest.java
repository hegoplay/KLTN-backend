package iuh.fit.se.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Entity(name = "contests")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Contest extends Event {
	
	@OneToMany(mappedBy = "contest")
	List<ExamResult> examResults;
	
	@Builder.Default
	boolean ableToRegister = true;
}
