package com.hr.sphere.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ============================================================================
 * Authentication & Navigation Controller
 * ============================================================================
 * 
 * Handles login view rendering and post-authentication redirection to the
 * central role-dispatching dashboard endpoint.
 */
@Controller
public class AuthController {

    /**
     * Renders the unified login portal for all tenant roles.
     *
     * @return the name of the Thymeleaf template ("login")
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Landing redirect after successful login to route the user
     * to their respective role-based dashboard.
     *
     * @return redirect to /dashboard
     */
    @GetMapping("/app")
    public String appHome() {
        return "redirect:/dashboard";
    }
}
