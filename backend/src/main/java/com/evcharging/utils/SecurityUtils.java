package com.evcharging.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    public Long getCurrentStaffAccountId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();

        // Adjust this based on your actual JWT implementation
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            // If using custom UserDetails with getId() method
            try {
                return (Long) principal.getClass().getMethod("getId").invoke(principal);
            } catch (Exception e) {
                throw new SecurityException("Unable to extract staff account ID from authentication");
            }
        }

        // Alternative: if principal is the username/email and you need to look it up
        throw new SecurityException("Unable to extract staff account ID from authentication");
    }

    //Get the current username
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }
}
