package iuh.fit.se.entity.enumerator;

public enum FunctionStatus {
	PENDING,
	ARCHIVED,
	ACCEPTED,
	REJECTED,
	DISABLED;
	
	public static boolean ableToCreate(FunctionStatus status) {
		return status == PENDING || status == ARCHIVED;
	}
	
	public static boolean ableToUpdate(FunctionStatus status) {
		return status == PENDING || status == ARCHIVED || status == DISABLED;
	}
}
