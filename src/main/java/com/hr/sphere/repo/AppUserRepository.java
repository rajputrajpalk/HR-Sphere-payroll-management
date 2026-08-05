package com.hr.sphere.repo;

import com.hr.sphere.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);

    List<AppUser> findByCompanyId(Long companyId);

    List<AppUser> findByCompanyIdAndRole(Long companyId, AppUser.UserRole role);

    List<AppUser> findByManagedByHRId(Long hrId);

    long countByCompanyIdAndRole(Long companyId, AppUser.UserRole role);

    long countByCompanyIdAndStatus(Long companyId, AppUser.UserStatus status);

    long countByStatus(AppUser.UserStatus status);
}
