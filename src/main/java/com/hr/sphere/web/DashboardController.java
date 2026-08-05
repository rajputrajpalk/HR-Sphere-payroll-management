package com.hr.sphere.web;

import com.hr.sphere.security.Roles;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        boolean isSuper = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + Roles.SUPERADMIN));
        boolean isCompanyAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + Roles.COMPANY_ADMIN));
        boolean isHr = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + Roles.HR));

        if (isSuper) return "redirect:/superadmin/dashboard";
        if (isCompanyAdmin) return "redirect:/companyadmin/dashboard";
        if (isHr) return "redirect:/hr/dashboard";
        return "redirect:/employee/dashboard";
    }
}
