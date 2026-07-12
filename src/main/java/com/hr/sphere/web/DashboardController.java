package com.hr.sphere.web;

import com.hr.sphere.security.Roles;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }
        boolean isSuper = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + Roles.SUPERADMIN));
        boolean isHr = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + Roles.HR));

        if (isSuper) return "superadmin/dashboard";
        if (isHr) return "hr/dashboard";
        return "employee/dashboard";
    }
}

