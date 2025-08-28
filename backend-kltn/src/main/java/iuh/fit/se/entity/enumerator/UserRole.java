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
	
	
}
