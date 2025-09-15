package iuh.fit.se.entity.enumerator;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Trạng thái của chức năng như event hoặc training")
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
	public boolean isAbleToCreate() {
		return ableToCreate(this);
	}
	
	public boolean isAbleToUpdate() {
		return ableToUpdate(this);
	}
}
