package iuh.fit.se.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Table(name = "attachments")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@lombok.Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {
	@Id
	String name;
	String url;
	String fileType;
	long size;
	long height;
	long width;
	@ManyToOne
	@JoinColumn(name = "user_id")
	User user;
}
