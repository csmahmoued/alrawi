package eg.alrawi.alrawi_award.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.stream.Collectors;

public final class SecurityUtil {

    private SecurityUtil() {}

    public static String getUsername() {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "ANONYMOUS";
    }



    public static String getRoles() {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) return null;
        return auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }
}

