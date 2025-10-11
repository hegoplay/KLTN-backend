package iuh.fit.se.api;

public class EventAPI {
	public static final String BASE_URL = "/api/events";
	public static final String ME_SEARCH = "/me/search";
	public static final String SEARCH_REGISTERED_EVENTS = "/search/registered-events";
	public static final String EVENT_ID = "/{eventId}";
	public static final String EVENT_ID_MODIFY_ORGANIZERS = EVENT_ID + "/modify-organizers";
	
	
	public static final String SEMINAR_ID_ADD_REVIEW = "/seminar/{eventId}/add-review";
	public static final String SEMINAR_ID_GET_REVIEWS = "/seminar/{eventId}/get-review";
	public static final String ID_MANUAL_TRIGGER_REGISTER = "/{eventId}/manual-trigger-register";
	public static final String CONTEST_ID_UPDATE_STANDING = "/contest/{eventId}/update-standing";
	
	public static final String BASE_LEADER_URL = "/api/leader/events";
}
