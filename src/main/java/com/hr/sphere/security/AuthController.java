package com.hr.sphere.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/app")
    public String appHome() {
        return "redirect:/dashboard";
    }


}

