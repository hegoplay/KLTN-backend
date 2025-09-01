package iuh.fit.se.util;

import org.springframework.stereotype.Component;

import iuh.fit.se.entity.enumerator.UserRole;

@Component
public class TokenContextUtil {
    private static final ThreadLocal<String> userIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> usernameHolder = new ThreadLocal<>();
    private static final ThreadLocal<UserRole> roleHolder = new ThreadLocal<>();

    public void setUserId(String userId) {
        userIdHolder.set(userId);
    }

    public String getUserId() {
        return userIdHolder.get();
    }

    public void setUsername(String username) {
        usernameHolder.set(username);
    }

    public String getUsername() {
        return usernameHolder.get();
    }

    public void setRole(UserRole role) {
        roleHolder.set(role);
    }

    public UserRole getRole() {
        return roleHolder.get();
    }

    public void clear() {
        userIdHolder.remove();
        usernameHolder.remove();
        roleHolder.remove();
    }

    public boolean hasUser() {
        return getUserId() != null && getUsername() != null && getRole() != null;
    }
}