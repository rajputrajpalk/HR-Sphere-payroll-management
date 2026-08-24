package com.hr.sphere.bootstrap;

import com.hr.sphere.model.AppUser;
import com.hr.sphere.repo.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * ============================================================================
 * Application Startup Data Seeder
 * ============================================================================
 * 
 * Ensures the platform always has an active, accessible Super Admin account
 * upon initial boot. If the user already exists, it verifies and refreshes
 * the password hash and active status.
 */
@Component
public class SeedData implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedData.class);

    private static final String DEFAULT_SUPER_ADMIN_USERNAME = "rajputrajpalk";
    private static final String DEFAULT_SUPER_ADMIN_PASSWORD = "Rajpal@18";
    private static final String DEFAULT_SUPER_ADMIN_EMAIL = "admin@hrsphere.com";

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SeedData(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedSuperAdministrator();
    }

    /**
     * Seeds or updates the root Super Administrator account.
     */
    private void seedSuperAdministrator() {
        userRepository.findByUsername(DEFAULT_SUPER_ADMIN_USERNAME).ifPresentOrElse(
            existingAdmin -> {
                existingAdmin.setPassword(passwordEncoder.encode(DEFAULT_SUPER_ADMIN_PASSWORD));
                existingAdmin.setStatus(AppUser.UserStatus.ACTIVE);
                userRepository.save(existingAdmin);
                log.info("Verified Super Admin account credentials for: '{}'", DEFAULT_SUPER_ADMIN_USERNAME);
            },
            () -> {
                AppUser superAdmin = new AppUser();
                superAdmin.setUsername(DEFAULT_SUPER_ADMIN_USERNAME);
                superAdmin.setPassword(passwordEncoder.encode(DEFAULT_SUPER_ADMIN_PASSWORD));
                superAdmin.setRole(AppUser.UserRole.SUPERADMIN);
                superAdmin.setFullName("Super Admin (Rajpal)");
                superAdmin.setEmail(DEFAULT_SUPER_ADMIN_EMAIL);
                superAdmin.setStatus(AppUser.UserStatus.ACTIVE);
                userRepository.save(superAdmin);
                log.info("Initialized default Super Admin account: '{}' ({})", DEFAULT_SUPER_ADMIN_USERNAME, DEFAULT_SUPER_ADMIN_EMAIL);
            }
        );
    }
}
