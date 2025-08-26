package iuh.fit.se.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Entity(name = "training_events")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class TrainingEvent extends Event {
	@ManyToOne
	@JoinColumn(name = "training_id")
	@ToString.Exclude
	Training training;

	@Override
	public boolean isSingleTable() {
		return false;
	}
}
