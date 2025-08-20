package iuh.fit.se.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class ContextUtil {
	
	public static String getCurrentUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}
