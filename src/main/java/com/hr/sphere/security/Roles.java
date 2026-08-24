package com.hr.sphere.security;

/**
 * ============================================================================
 * Security Role Constants
 * ============================================================================
 * 
 * Central registry of authorization roles used across Spring Security filter
 * rules and controller mappings.
 * 
 * Hierarchy:
 *  1. SUPERADMIN    - Platform owner: Manages tenant companies & global settings.
 *  2. COMPANY_ADMIN - Tenant owner: Manages organization HR managers & company quotas.
 *  3. HR            - HR Manager: Manages employee onboarding, tasks, leaves, and payroll.
 *  4. EMPLOYEE      - Staff member: Updates task status, applies for leaves, views payslips.
 */
public final class Roles {

    private Roles() {
        // Utility class - prevent instantiation
    }

    public static final String SUPERADMIN = "SUPERADMIN";
    public static final String COMPANY_ADMIN = "COMPANY_ADMIN";
    public static final String HR = "HR";
    public static final String EMPLOYEE = "EMPLOYEE";
}
