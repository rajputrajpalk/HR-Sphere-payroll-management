package com.hr.sphere.model;

import com.hr.sphere.security.Roles;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "app_users")
@Getter
@Setter
public class AppUser implements UserDetails {




    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String username;

    @Column(nullable = false)
    private String password;

    // SUPERADMIN has no company.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ApprovalStatus approvalStatus = ApprovalStatus.APPROVED;

    @Column(nullable = false, length = 150)
    private String fullName;

    public enum UserRole {
        SUPERADMIN,
        HR,
        EMPLOYEE;

        public String toRoleString() {
            return switch (this) {
                case SUPERADMIN -> Roles.SUPERADMIN;
                case HR -> Roles.HR;
                case EMPLOYEE -> Roles.EMPLOYEE;
            };
        }
    }

    public enum ApprovalStatus {
        APPROVED,
        PENDING
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + role.toRoleString());
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

    @Override
    public boolean isEnabled() {
        // If pending approval, block login.
        return approvalStatus == ApprovalStatus.APPROVED;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}


