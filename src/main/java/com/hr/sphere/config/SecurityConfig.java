package com.hr.sphere.config;

import com.hr.sphere.security.Roles;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * ============================================================================
 * Spring Security Configuration
 * ============================================================================
 * 
 * Configures role-based access control (RBAC), URL authorization rules,
 * form login behavior, session lifecycle, and H2 database console frame access.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * BCrypt password encoder for secure one-way password hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines the HTTP security filter chain and authorization rules.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF to allow straightforward form submissions & H2 console usage
            .csrf(csrf -> csrf.disable())

            // Disable X-Frame-Options so H2 Database Web Console can render in iframe
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))

            // URL Route Authorization Rules
            .authorizeHttpRequests(auth -> auth
                // Publicly accessible assets and pages
                .requestMatchers(
                    "/login",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/public/**",
                    "/error",
                    "/h2-console/**"
                ).permitAll()

                // Public POST endpoints (e.g. auth actions)
                .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()

                // Role-restricted portal paths
                .requestMatchers("/superadmin/**").hasRole(Roles.SUPERADMIN)
                .requestMatchers("/companyadmin/**").hasRole(Roles.COMPANY_ADMIN)
                .requestMatchers("/hr/**").hasRole(Roles.HR)
                .requestMatchers("/employee/**").hasRole(Roles.EMPLOYEE)

                // All other endpoints require authentication
                .anyRequest().authenticated()
            )

            // Form Login Configuration
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/app", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )

            // Logout Configuration
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            // Exception Handling
            .exceptionHandling(ex -> ex
                .accessDeniedHandler((request, response, accessDeniedException) -> 
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN)
                )
            );

        return http.build();
    }
}
