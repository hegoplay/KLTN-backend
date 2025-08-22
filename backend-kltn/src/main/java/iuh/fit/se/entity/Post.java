package iuh.fit.se.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import iuh.fit.se.entity.enumerator.FunctionStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Table(name = "posts")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@lombok.Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Post {
	@Id
	@GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
	String postId;
	String title;

	@ManyToOne
	@JoinColumn(name = "user_id")
	User writer;
	String content;

	@CreatedDate
	@Builder.Default
	LocalDateTime postTime = LocalDateTime.now();

	@ManyToOne
	@JoinColumn(name = "image_id")
	Attachment featureImage;

	@OneToMany(mappedBy = "post",cascade = CascadeType.REMOVE, orphanRemoval = true)
	List<Comment> comments;

	@Enumerated(EnumType.STRING)
	FunctionStatus status;
	
    @LastModifiedDate
    @OrderColumn
    LocalDateTime lastModifiedTime; // Sẽ tự động cập nhật khi entity thay đổi
}
