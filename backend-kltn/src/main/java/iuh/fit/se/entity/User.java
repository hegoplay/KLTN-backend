package iuh.fit.se.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import iuh.fit.se.entity.enumerator.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Table(name = "users")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@lombok.Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements java.io.Serializable, UserDetails {
	private static final long serialVersionUID = -6004794275702672464L;

	@Id
	@GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
	String id;

	@Column(unique = true, nullable = false)
	@Length(min = 4, max = 32)
	String username;
	@Column(nullable = false)
	@Length(min = 4, max = 64)
	String password;
	@Column(unique = true, nullable = false)
	@Length(min = 5, max = 64)
	String email;
	String nickname;

	@Builder.Default
	LocalDate dateOfBirth = LocalDate.now();

	@Column(nullable = false)
	String fullName;
	String studentId;

	// @ElementCollection(targetClass = UserRole.class)
	// @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name =
	// "user_id"))
	// @Enumerated(EnumType.STRING)
	// @Column(name = "role")
	// @Builder.Default
	// List<UserRole> roles = new ArrayList<>(); // Đổi tên thành roles cho rõ
	// nghĩa

	@Enumerated(EnumType.STRING)
	@Builder.Default
	UserRole role = UserRole.MEMBER; // Chỉ để một role duy nhất cho đơn giản

	@Column(nullable = false)
	@Builder.Default
	Integer attendancePoint = 0;
	@Builder.Default
	Integer contributionPoint = 0;

	// @Builder.Default
	// @OneToMany(mappedBy = "user")
	// List<Attachment> attachments = new java.util.ArrayList<>();

	@ManyToMany(mappedBy = "participants")
	@Builder.Default
	List<Training> registeredTrainings = new java.util.ArrayList<>();
	//
	@Builder.Default
	boolean disabled = false;


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		List<SimpleGrantedAuthority> auths = new java.util.ArrayList<>();
		int val = 0;
		if (role != null) {
			val = role.getValue();
		}

		for (UserRole r : UserRole.values()) {
			if (r.getValue() <= val) {
				auths.add(new SimpleGrantedAuthority("ROLE_" + r.name()));
			}
		}

		return auths;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return !disabled;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return !disabled;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return !disabled;
	}

	public boolean isMember() {
		return role == UserRole.MEMBER || role == UserRole.LEADER
			|| role == UserRole.ADMIN;
	}
	public boolean isLeader() {
		return role == UserRole.LEADER || role == UserRole.ADMIN;
	}
	public boolean isAdmin() {
		return role == UserRole.ADMIN;
	}
	
	public void plusAttendancePoint(int score) {
		if (score + attendancePoint.intValue() < 0) {
			throw new IllegalArgumentException("điểm tích cực có vấn đề ở user " + this.getUsername());
		}
		this.attendancePoint += score;
	}
	
	public void plusContributionPoint(int score) {
		if (score + contributionPoint.intValue() < 0) {
			throw new IllegalArgumentException("điểm đóng góp có vấn đề ở user " + this.getUsername());
		}
		this.contributionPoint += score;
	}

}
