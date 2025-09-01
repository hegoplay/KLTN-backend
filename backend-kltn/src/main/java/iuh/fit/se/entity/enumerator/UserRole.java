package iuh.fit.se.entity.enumerator;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cấp bậc của người dùng trong nhóm")
public enum UserRole {
	ADMIN(3),MEMBER(1),LEADER(2),NONE(0);
	
	private int value;

	private UserRole(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public boolean isMemberOrHigher() {
		return this.value >= MEMBER.value;
	}
	
	public boolean isLeaderOrHigher() {
		return this.value >= LEADER.value;
	}
	
	public boolean isAdmin() {
		return this == ADMIN;
	}
}
