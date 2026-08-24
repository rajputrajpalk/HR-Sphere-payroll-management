package com.hr.sphere.repo;

import com.hr.sphere.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * CompanyRepository (Data Access for Tenant Organizations)
 * ============================================================================
 * 
 * Provides database operations for provisioning, looking up, and managing
 * tenant companies and their subscription statuses.
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    /**
     * Looks up a tenant organization by its unique URL slug/code.
     * SQL: SELECT * FROM companies WHERE slug = ?
     */
    Optional<Company> findBySlug(String slug);

    /**
     * Retrieves all companies with a specific operational status (ACTIVE / SUSPENDED).
     * SQL: SELECT * FROM companies WHERE status = ?
     */
    List<Company> findByStatus(Company.CompanyStatus status);

    /**
     * Counts companies by operational status.
     * SQL: SELECT COUNT(*) FROM companies WHERE status = ?
     */
    long countByStatus(Company.CompanyStatus status);
}
