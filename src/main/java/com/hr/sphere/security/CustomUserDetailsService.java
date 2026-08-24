package com.hr.sphere.security;

import com.hr.sphere.model.AppUser;
import com.hr.sphere.repo.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * ============================================================================
 * Custom User Details Service
 * ============================================================================
 * 
 * Bridges Spring Security authentication with the application's database.
 * Loads user records by username for credential verification and role authority assignment.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final AppUserRepository userRepository;

    public CustomUserDetailsService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Finds and loads the user by their username.
     *
     * @param username the username identifying the user whose data is required.
     * @return the fully populated AppUser record implementing Spring's UserDetails.
     * @throws UsernameNotFoundException if the user cannot be found in the database.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Authenticating user with username: '{}'", username);

        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Authentication failed: Username '{}' not found.", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });

        log.debug("User '{}' loaded successfully with role: {}", username, user.getRole());
        return user;
    }
}
