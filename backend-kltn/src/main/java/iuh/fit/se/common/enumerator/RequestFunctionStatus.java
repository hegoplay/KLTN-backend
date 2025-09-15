package iuh.fit.se.common.enumerator;

import iuh.fit.se.entity.enumerator.FunctionStatus;

public enum RequestFunctionStatus {
	PENDING, ARCHIVED;
	
	public static FunctionStatus convertToFunctionStatus(RequestFunctionStatus status) {
		if (status == null) {
			return FunctionStatus.PENDING;
		}
		switch (status) {
			case PENDING:
				return FunctionStatus.PENDING;
			case ARCHIVED:
				return FunctionStatus.ARCHIVED;
			default:
				throw new IllegalArgumentException("Unknown status: " + status);
		}
	}
}
