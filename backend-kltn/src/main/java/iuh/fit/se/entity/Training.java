package iuh.fit.se.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;

import iuh.fit.se.entity.enumerator.FunctionStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Table(name = "trainings")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@lombok.Builder
@NoArgsConstructor
@AllArgsConstructor
public class Training {
	@jakarta.persistence.Id
	@JoinColumn(name = "training_id")
	@GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
	String id;

	String title;

	@OneToMany(
		mappedBy = "training",
		fetch = FetchType.LAZY,
		cascade = {CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE,
				CascadeType.DETACH},
		orphanRemoval = true)
	List<TrainingEvent> trainingEvents;
	String description;
	Location location;

	@ManyToMany
	@JoinTable(
		name = "mentor_training_list",
		joinColumns = @JoinColumn(name = "training_id"),
		inverseJoinColumns = @JoinColumn(name = "mentor_id"))
	@Builder.Default
	Set<User> mentors = new HashSet<>();

	@Enumerated(jakarta.persistence.EnumType.STRING)
	FunctionStatus status;

	@ManyToOne
	@JoinColumn(name = "creator_id")
	User creator;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		name = "registered_training_participants",
		joinColumns = @JoinColumn(name = "training_id"),
		inverseJoinColumns = @JoinColumn(name = "participant_id"))
	@Builder.Default
	List<User> participants = new ArrayList<>();

	@CreatedDate
	java.time.LocalDateTime createdAt;

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
			mentors = new HashSet<>();
		}
		mentors.add(user);
	}

	public void removeMentor(User user) {
		if (mentors != null) {
			mentors.remove(user);
		}
	}

	public void setAllEventHost(User newHost) {
		if (trainingEvents != null) {
			for (TrainingEvent event : trainingEvents) {
				event.setHost(newHost);
			}
		}
	}

	public void setAllEventStatus(FunctionStatus newStatus) {
		if (trainingEvents != null) {
			for (TrainingEvent event : trainingEvents) {
				event.setStatus(newStatus);
			}
		}
	}

	public void addTrainingEvent(TrainingEvent event) {

		if (event
			.getLocation()
			.getStartTime()
			.isBefore(this.location.getStartTime())
			|| event
				.getLocation()
				.getEndTime()
				.isAfter(this.location.getEndTime())) {
			throw new IllegalArgumentException(
				"Thời gian sự kiện phải nằm trong khoảng thời gian của khóa đào tạo.");
		}

		if (trainingEvents == null) {
			trainingEvents = new ArrayList<>();
		}
		trainingEvents.add(event);
		event.setTraining(this);
	}

}
