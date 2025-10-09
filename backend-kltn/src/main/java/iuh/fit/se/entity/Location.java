package iuh.fit.se.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OrderColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Embeddable
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@lombok.Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
	
	@Column(columnDefinition = "TEXT")
	String destination;
	@OrderColumn
	LocalDateTime startTime;
	LocalDateTime endTime;
}
