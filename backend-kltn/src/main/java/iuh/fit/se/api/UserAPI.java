package iuh.fit.se.api;

public class UserAPI {
	public static final String BASE_URL = "/api/users";
	public static final String UPDATE_MY_INFO = "/update-my-info";
	public static final String UPDATE_MY_PASSWORD = "/update-my-password";
	public static final String USER_ID = "/{userId}";
	public static final String USER_ID_RESET_PASSWORD = USER_ID + "/reset-password";
	public static final String USER_ID_CHANGE_ROLE = USER_ID + "/change-role";
}
