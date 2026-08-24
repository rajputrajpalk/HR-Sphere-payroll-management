package com.hr.sphere.repo;

import com.hr.sphere.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * AppUserRepository (Data Access for System Users)
 * ============================================================================
 * 
 * Provides database operations for user authentication, multi-tenant staff
 * management, HR delegation hierarchies, and quota enforcement.
 */
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    /**
     * Looks up a user by their unique login username.
     * SQL: SELECT * FROM app_users WHERE username = ?
     */
    Optional<AppUser> findByUsername(String username);

    /**
     * Retrieves all users belonging to a specific tenant company.
     * SQL: SELECT * FROM app_users WHERE company_id = ?
     */
    List<AppUser> findByCompanyId(Long companyId);

    /**
     * Retrieves all users belonging to a tenant company filtered by role (e.g. HR, EMPLOYEE).
     * SQL: SELECT * FROM app_users WHERE company_id = ? AND role = ?
     */
    List<AppUser> findByCompanyIdAndRole(Long companyId, AppUser.UserRole role);

    /**
     * Retrieves all employees supervised by a specific HR Manager.
     * SQL: SELECT * FROM app_users WHERE managed_by_hr_id = ?
     */
    List<AppUser> findByManagedByHRId(Long hrId);

    /**
     * Counts users within a company with a specific role (used for quota enforcement).
     * SQL: SELECT COUNT(*) FROM app_users WHERE company_id = ? AND role = ?
     */
    long countByCompanyIdAndRole(Long companyId, AppUser.UserRole role);

    /**
     * Counts users within a company with a specific status (ACTIVE / INACTIVE).
     * SQL: SELECT COUNT(*) FROM app_users WHERE company_id = ? AND status = ?
     */
    long countByCompanyIdAndStatus(Long companyId, AppUser.UserStatus status);

    /**
     * Platform-wide user count by status (used for Super Admin dashboard analytics).
     * SQL: SELECT COUNT(*) FROM app_users WHERE status = ?
     */
    long countByStatus(AppUser.UserStatus status);
}
