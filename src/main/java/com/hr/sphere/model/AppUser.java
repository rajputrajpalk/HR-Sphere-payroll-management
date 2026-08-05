package com.hr.sphere.model;

import com.hr.sphere.security.Roles;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "app_users")
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

    // SUPERADMIN has no company.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    private Company company;

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

    public enum UserStatus {
        ACTIVE,
        INACTIVE
    }

    // Explicit Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @Override
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @Override
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public AppUser getManagedByHR() { return managedByHR; }
    public void setManagedByHR(AppUser managedByHR) { this.managedByHR = managedByHR; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + role.toRoleString());
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        // Cascade Access Lock: Block login if user is INACTIVE or if parent Company is SUSPENDED
        if (status != UserStatus.ACTIVE) {
            return false;
        }
        if (company != null && company.getStatus() != Company.CompanyStatus.ACTIVE) {
            return false;
        }
        return true;
    }
}
