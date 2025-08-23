package iuh.fit.se.entity.enumerator;

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
