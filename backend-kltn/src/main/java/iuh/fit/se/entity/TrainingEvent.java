package iuh.fit.se.entity;

import jakarta.persistence.Entity;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Entity(name = "training_events")
public class TrainingEvent extends Event {

}
