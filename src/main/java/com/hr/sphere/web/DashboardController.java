package com.hr.sphere.web;

import com.hr.sphere.security.Roles;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ============================================================================
 * Central Dashboard Dispatcher
 * ============================================================================
 * 
 * Inspects the authenticated user's assigned security authority and redirects
 * them to their dedicated role-specific workspace dashboard.
 */
@Controller
public class DashboardController {

    /**
     * Inspects authorities on the current Authentication object and redirects:
     *  - ROLE_SUPERADMIN    -> /superadmin/dashboard
     *  - ROLE_COMPANY_ADMIN -> /companyadmin/dashboard
     *  - ROLE_HR            -> /hr/dashboard
     *  - ROLE_EMPLOYEE      -> /employee/dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        if (hasRole(authentication, Roles.SUPERADMIN)) {
            return "redirect:/superadmin/dashboard";
        }
        if (hasRole(authentication, Roles.COMPANY_ADMIN)) {
            return "redirect:/companyadmin/dashboard";
        }
        if (hasRole(authentication, Roles.HR)) {
            return "redirect:/hr/dashboard";
        }

        return "redirect:/employee/dashboard";
    }

    /**
     * Helper to verify if the authenticated user possesses a given role authority.
     */
    private boolean hasRole(Authentication auth, String roleName) {
        String authorityName = "ROLE_" + roleName;
        for (GrantedAuthority authority : auth.getAuthorities()) {
            if (authorityName.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
