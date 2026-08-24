package com.hr.sphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ============================================================================
 * HR Sphere SaaS Application Entry Point
 * ============================================================================
 * 
 * A Multi-Tenant Human Resource & Payroll Management SaaS Platform.
 * 
 * Key Platform Highlights:
 *  - Multi-Tenant architecture isolated by company boundaries
 *  - 4-Tier Role-Based Access Control (Super Admin, Company Admin, HR, Employee)
 *  - Cascade Access Lock: Instant organization-level suspension enforcement
 *  - Embedded H2 file-backed persistence with zero mandatory external DB setup
 *  - Responsive Thymeleaf dashboards tailored for each role
 * 
 * @author HR Sphere Team
 * @version 1.0.0
 */
@SpringBootApplication
public class HrSphereApplication {

    /**
     * Bootstraps the Spring Boot application container and web server.
     *
     * @param args runtime command-line arguments passed to the JVM
     */
    public static void main(String[] args) {
        SpringApplication.run(HrSphereApplication.class, args);
    }
}
