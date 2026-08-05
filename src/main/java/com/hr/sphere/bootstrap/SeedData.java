package com.hr.sphere.bootstrap;

import com.hr.sphere.model.AppUser;
import com.hr.sphere.repo.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SeedData implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SeedData(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Seed ONLY the initial Super Admin account
        userRepository.findByUsername("rajputrajpalk").ifPresentOrElse(
            u -> {
                u.setPassword(passwordEncoder.encode("Rajpal@18"));
                u.setStatus(AppUser.UserStatus.ACTIVE);
                userRepository.save(u);
            },
            () -> {
                AppUser superAdmin = new AppUser();
                superAdmin.setUsername("rajputrajpalk");
                superAdmin.setPassword(passwordEncoder.encode("Rajpal@18"));
                superAdmin.setRole(AppUser.UserRole.SUPERADMIN);
                superAdmin.setFullName("Super Admin (Rajpal)");
                superAdmin.setStatus(AppUser.UserStatus.ACTIVE);
                superAdmin.setEmail("admin@hrsphere.com");
                userRepository.save(superAdmin);
            }
        );
    }
}
