package com.hr.sphere.model;

import com.hr.sphere.security.Roles;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * ============================================================================
 * AppUser Entity (System User & Security Principal)
 * ============================================================================
 * 
 * Represents all platform actors (Super Admin, Company Admin, HR Manager, Employee).
 * Implements Spring Security's {@link UserDetails} to enforce access policies,
 * credential verification, and organization-level cascading suspension locks.
 */
@Entity
@Table(name = "app_users", indexes = {
    @Index(name = "idx_appuser_username", columnList = "username"),
    @Index(name = "idx_appuser_company", columnList = "company_id")
})
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(length = 150)
    private String email;

    @Column(nullable = false, length = 150)
    private String fullName;

    /**
     * Tenant organization the user belongs to.
     * Null for platform-level SUPERADMIN.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    private Company company;

    /**
     * Supervising HR Manager who manages this employee.
     * Null for administrators and standalone managers.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "managed_by_hr_id")
    private AppUser managedByHR;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * User Roles in the platform hierarchy.
     */
    public enum UserRole {
        SUPERADMIN,
        COMPANY_ADMIN,
        HR,
        EMPLOYEE;

        public String toRoleString() {
            return switch (this) {
                case SUPERADMIN -> Roles.SUPERADMIN;
                case COMPANY_ADMIN -> Roles.COMPANY_ADMIN;
                case HR -> Roles.HR;
                case EMPLOYEE -> Roles.EMPLOYEE;
            };
        }
    }

    /**
     * Account operational status.
     */
    public enum UserStatus {
        ACTIVE,
        INACTIVE
    }

    // ========================================================================
    // Domain & Business Helper Methods
    // ========================================================================

    public boolean isSuperAdmin() {
        return this.role == UserRole.SUPERADMIN;
    }

    public boolean isCompanyAdmin() {
        return this.role == UserRole.COMPANY_ADMIN;
    }

    public boolean isHR() {
        return this.role == UserRole.HR;
    }

    public boolean isEmployee() {
        return this.role == UserRole.EMPLOYEE;
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    // ========================================================================
    // Spring Security UserDetails Implementation
    // ========================================================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.toRoleString()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Cascade Access Lock:
     * - Returns false if the user account status itself is not ACTIVE.
     * - Returns false if the user's parent company is SUSPENDED.
     */
    @Override
    public boolean isEnabled() {
        if (this.status != UserStatus.ACTIVE) {
            return false;
        }
        if (this.company != null && this.company.getStatus() != Company.CompanyStatus.ACTIVE) {
            return false;
        }
        return true;
    }

    // ========================================================================
    // Getters and Setters
    // ========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public AppUser getManagedByHR() {
        return managedByHR;
    }

    public void setManagedByHR(AppUser managedByHR) {
        this.managedByHR = managedByHR;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role=" + role +
                ", status=" + status +
                '}';
    }
}
