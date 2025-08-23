package iuh.fit.se.entity;

import java.util.ArrayList;
import java.util.List;

import iuh.fit.se.entity.enumerator.FunctionStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Table(name = "trainings")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@lombok.Builder
public class Training {
	@jakarta.persistence.Id
	@JoinColumn(name = "training_id")
	@GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
	String id;

	@OneToMany(mappedBy = "training")
	List<TrainingEvent> trainingEvents;
	String description;
	Location location;

	@ManyToMany
	@JoinTable(
		name = "mentor_training_list",
		joinColumns = @JoinColumn(name = "training_id"),
		inverseJoinColumns = @JoinColumn(name = "mentor_id")
	)
	@Builder.Default
	List<User> mentors = new ArrayList<>();	

	@Enumerated(jakarta.persistence.EnumType.STRING)
	FunctionStatus status;

	@ManyToOne
	@JoinColumn(name = "creator_id")
	User creator;

	@ManyToMany
	@JoinTable(
		name = "registered_training_participants",
		joinColumns = @JoinColumn(name = "training_id"),
		inverseJoinColumns = @JoinColumn(name = "participant_id")
	)
	@Builder.Default
	List<User> participants = new ArrayList<>();
	
	public void addParticipant(User user) {
		if (participants == null) {
			participants = new ArrayList<>();
		}
		participants.add(user);
	}
	
	public void removeParticipant(User user) {
		if (participants != null) {
			participants.remove(user);
		}
	}
	
	public void addMentor(User user) {
		if (mentors == null) {
			mentors = new ArrayList<>();
		}
		mentors.add(user);
	}
	
	public void removeMentor(User user) {
		if (mentors != null) {
			mentors.remove(user);
		}
	}
	
}
