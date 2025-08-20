package iuh.fit.se.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Embeddable
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@lombok.Builder
public class Location {
	
	String destination;
	
	LocalDateTime startDate;
	LocalDateTime endDate;
}
