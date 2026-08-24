package com.hr.sphere.web.superadmin;

import com.hr.sphere.model.AppUser;
import com.hr.sphere.model.Company;
import com.hr.sphere.repo.AppUserRepository;
import com.hr.sphere.repo.CompanyRepository;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * ============================================================================
 * SuperAdminController (System Platform Administration)
 * ============================================================================
 * 
 * Provides platform-level administration capabilities:
 *  - Global system analytics (total tenants, active companies, total users)
 *  - Onboarding and provisioning new Tenant Companies & initial Company Admins
 *  - Activating/Suspending companies (Cascade Access Lock)
 *  - Deleting tenant organizations and their associated resources
 */
@Controller
@RequestMapping("/superadmin")
@Validated
public class SuperAdminController {

    private static final Logger log = LoggerFactory.getLogger(SuperAdminController.class);

    private final CompanyRepository companyRepository;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SuperAdminController(CompanyRepository companyRepository,
                                AppUserRepository userRepository,
                                PasswordEncoder passwordEncoder) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Renders the Super Admin main analytics dashboard and company list.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long totalCompanies = companyRepository.count();
        long activeCompanies = companyRepository.countByStatus(Company.CompanyStatus.ACTIVE);
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByStatus(AppUser.UserStatus.ACTIVE);

        model.addAttribute("totalCompanies", totalCompanies);
        model.addAttribute("activeCompanies", activeCompanies);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("companies", companyRepository.findAll());

        log.debug("Loaded Super Admin dashboard: {} total companies, {} total users.", totalCompanies, totalUsers);
        return "superadmin/dashboard";
    }

    /**
     * Redirects to the dashboard for backward compatibility.
     */
    @GetMapping("/companies")
    public String companies(Model model) {
        return "redirect:/superadmin/dashboard";
    }

    /**
     * Provisions a new Tenant Company and creates its root Company Administrator.
     */
    @PostMapping("/companies/create")
    public String createCompany(
            @RequestParam("name") @NotBlank String name,
            @RequestParam("slug") @NotBlank String slug,
            @RequestParam(value = "domain", required = false) String domain,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "maxHRs", defaultValue = "5") Integer maxHRs,
            @RequestParam(value = "maxEmployees", defaultValue = "100") Integer maxEmployees,
            @RequestParam("adminUsername") @NotBlank String adminUsername,
            @RequestParam("adminPassword") @NotBlank String adminPassword
    ) {
        // Sanitize and check slug uniqueness
        String cleanSlug = slug.trim().toLowerCase();
        if (companyRepository.findBySlug(cleanSlug).isPresent()) {
            log.warn("Failed to create company: Slug '{}' already exists.", cleanSlug);
            return "redirect:/superadmin/dashboard?error=slug_exists";
        }

        // Check if admin username is already taken
        if (userRepository.findByUsername(adminUsername.trim()).isPresent()) {
            log.warn("Failed to create company: Admin username '{}' already taken.", adminUsername);
            return "redirect:/superadmin/dashboard?error=username_exists";
        }

        // 1. Create and persist Company
        Company company = new Company();
        company.setName(name.trim());
        company.setSlug(cleanSlug);
        company.setDomain(domain != null ? domain.trim() : null);
        company.setEmail(email != null ? email.trim() : null);
        company.setMaxHRs(maxHRs != null ? maxHRs : 5);
        company.setMaxEmployees(maxEmployees != null ? maxEmployees : 100);
        company.setStatus(Company.CompanyStatus.ACTIVE);
        company = companyRepository.save(company);

        // 2. Create and persist initial Company Admin
        AppUser admin = new AppUser();
        admin.setUsername(adminUsername.trim());
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(AppUser.UserRole.COMPANY_ADMIN);
        admin.setCompany(company);
        admin.setFullName(name.trim() + " Administrator");
        admin.setEmail(email != null ? email.trim() : null);
        admin.setStatus(AppUser.UserStatus.ACTIVE);
        userRepository.save(admin);

        log.info("Successfully provisioned tenant company '{}' (slug: {}) with admin '{}'", name, cleanSlug, adminUsername);
        return "redirect:/superadmin/dashboard?success=created";
    }

    /**
     * Toggles the operational status of a company between ACTIVE and SUSPENDED.
     * When suspended, the Cascade Access Lock instantly blocks all users belonging to this company.
     */
    @PostMapping("/companies/toggle-status")
    public String toggleCompanyStatus(@RequestParam("companyId") Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid company ID: " + companyId));

        Company.CompanyStatus newStatus = company.isActive() ? Company.CompanyStatus.SUSPENDED : Company.CompanyStatus.ACTIVE;
        company.setStatus(newStatus);
        companyRepository.save(company);

        log.info("Toggled company '{}' status to: {}", company.getName(), newStatus);
        return "redirect:/superadmin/dashboard?success=status_updated";
    }

    /**
     * Deletes a tenant company and cascades deletion across its records.
     */
    @PostMapping("/companies/delete")
    public String deleteCompany(@RequestParam("companyId") Long companyId) {
        companyRepository.findById(companyId).ifPresent(company -> {
            companyRepository.delete(company);
            log.warn("Deleted company '{}' (ID: {}) by Super Admin.", company.getName(), companyId);
        });
        return "redirect:/superadmin/dashboard?success=deleted";
    }
}
