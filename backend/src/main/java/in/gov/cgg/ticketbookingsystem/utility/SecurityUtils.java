package in.gov.cgg.ticketbookingsystem.utility;

import in.gov.cgg.ticketbookingsystem.model.Role;
import in.gov.cgg.ticketbookingsystem.security.SecurityUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    private SecurityUtils() {
    }

    public static boolean hasRole(SecurityUser user, String role) {
        return user.getAuthorities().contains(new SimpleGrantedAuthority(role));
    }

    public static boolean isAdmin(SecurityUser user) {
        return hasRole(user, Role.ROLE_ADMIN.name());
    }

    public static boolean isUser(SecurityUser user) {
        return hasRole(user, Role.ROLE_USER.name());
    }

    public static SecurityUser getCurrentSecurityUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new IllegalStateException("User is not authenticated");
        // unreachable in practice, unauthenticated request would be blocked
        // before they reach any service method could call this
        // but since the compiler asks nicely it shall be null checked.
        return (SecurityUser) auth.getPrincipal();
    }

    public static String getUsername() {
        return getCurrentSecurityUser().getUsername();
    }
}