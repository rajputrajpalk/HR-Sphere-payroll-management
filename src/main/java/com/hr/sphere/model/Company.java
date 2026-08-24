package com.hr.sphere.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * Company Entity (Tenant Organization)
 * ============================================================================
 * 
 * Represents an independent tenant organization in HR Sphere.
 * Enforces multi-tenancy boundaries, custom quotas for staff capacity,
 * and organization-level activation/suspension states.
 */
@Entity
@Table(name = "companies", uniqueConstraints = {
        @UniqueConstraint(name = "uk_company_slug", columnNames = {"slug"})
})
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    /**
     * Unique URL identifier / short code for the company (e.g. "acme").
     */
    @Column(nullable = false, length = 100)
    private String slug;

    @Column(length = 200)
    private String domain;

    @Column(length = 150)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CompanyStatus status = CompanyStatus.ACTIVE;

    @Column(nullable = false)
    private Integer maxHRs = 5;

    @Column(nullable = false)
    private Integer maxEmployees = 100;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Operational status of the company.
     */
    public enum CompanyStatus {
        ACTIVE,
        SUSPENDED
    }

    // ========================================================================
    // Domain & Business Helper Methods
    // ========================================================================

    public boolean isActive() {
        return this.status == CompanyStatus.ACTIVE;
    }

    public boolean isSuspended() {
        return this.status == CompanyStatus.SUSPENDED;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CompanyStatus getStatus() {
        return status;
    }

    public void setStatus(CompanyStatus status) {
        this.status = status;
    }

    public Integer getMaxHRs() {
        return maxHRs;
    }

    public void setMaxHRs(Integer maxHRs) {
        this.maxHRs = maxHRs;
    }

    public Integer getMaxEmployees() {
        return maxEmployees;
    }

    public void setMaxEmployees(Integer maxEmployees) {
        this.maxEmployees = maxEmployees;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", status=" + status +
                '}';
    }
}
