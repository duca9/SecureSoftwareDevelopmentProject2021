package com.zuehlke.securesoftwaredevelopment.config;

import com.zuehlke.securesoftwaredevelopment.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

public class SecurityUtil {

    private SecurityUtil() {
    }

    private static final Logger LOG = LoggerFactory.getLogger(SecurityUtil.class);

    public static boolean hasPermission(String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean retVal = false;
        if (authentication != null && permission != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (authorities != null) {
                retVal = authorities.contains(new SimpleGrantedAuthority(permission));
            } else {
                LOG.error("Authorities is null");
            }
        } else {
            LOG.error("Authentication or permission is null");
        }
        return retVal;
    }

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object object = authentication.getPrincipal();
            if (object != null) {
                return (User) object;
            } else {
                LOG.warn("Principal is null");
            }
        } else {
            LOG.warn("Authentication is null");
        }
        return null;
    }

    public static Integer getCurrentUserId() {
        Integer userId = null;
        User user = getCurrentUser();
        if (user != null) {
            userId = user.getId();
        }
        return userId;
    }
}
