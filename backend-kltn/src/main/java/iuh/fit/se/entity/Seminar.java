package iuh.fit.se.entity;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Entity(name = "seminars")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class Seminar extends Event{
	@Builder.Default
	@ToString.Exclude
	@ElementCollection
	@CollectionTable(
        name = "seminar_reviews", // Tên bảng cho collection
        joinColumns = @JoinColumn(name = "seminar_id") // Khóa ngoại
        
    )
    @Column(name = "review", length = 1000) // Tên column và độ dài
	List<String> reviews = List.of();

	@Override
	public boolean isSingleTable() {
		return true;
	}
}
