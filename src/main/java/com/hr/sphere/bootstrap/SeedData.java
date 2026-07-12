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
        userRepository.findByUsername("rajputrajpalk").ifPresentOrElse(u -> {
        }, () -> {
            AppUser superAdmin = new AppUser();
            superAdmin.setUsername("rajputrajpalk");
            superAdmin.setPassword(passwordEncoder.encode("Rajpal@18"));
            superAdmin.setRole(AppUser.UserRole.SUPERADMIN);
            superAdmin.setFullName("Rajput RajpalK");
            superAdmin.setApprovalStatus(AppUser.ApprovalStatus.APPROVED);


            userRepository.save(superAdmin);
        });
    }
}

