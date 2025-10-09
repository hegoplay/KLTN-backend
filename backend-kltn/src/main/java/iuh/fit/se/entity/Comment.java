package iuh.fit.se.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Table(name = "comments")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@lombok.Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
	@Id
	@GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
	String commentId;
	@Column(columnDefinition = "TEXT")
	String content;
	
	@ManyToOne
	@JoinColumn(name = "commenter_id")
	User commenter;
	
	@ManyToOne	
	@JoinColumn(name = "post_id")
	Post post;
	
	@Builder.Default
	LocalDateTime commentTime = LocalDateTime.now();	
}
