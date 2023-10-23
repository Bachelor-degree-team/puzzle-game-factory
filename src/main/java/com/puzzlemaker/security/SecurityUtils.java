package com.puzzlemaker.security;

import com.puzzlemaker.model.UserRole;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.stream.Stream;

@UtilityClass
public class SecurityUtils {

    public static boolean hasAccess(UserDetails loggedInUser, String userToBeAccessedLogin) {

        Stream<String> userRoles = loggedInUser.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority);

        if (userRoles.anyMatch(role -> UserRole.ADMIN.name().equals(role))) {
            return true;
        }

        return loggedInUser.getUsername().equals(userToBeAccessedLogin);
    }
}
