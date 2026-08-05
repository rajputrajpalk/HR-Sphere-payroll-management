package com.hr.sphere.web.superadmin;

import com.hr.sphere.model.AppUser;
import com.hr.sphere.model.Company;
import com.hr.sphere.repo.AppUserRepository;
import com.hr.sphere.repo.CompanyRepository;
import com.hr.sphere.repo.TaskRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/superadmin")
@Validated
public class SuperAdminController {

    private final CompanyRepository companyRepository;
    private final AppUserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    public SuperAdminController(CompanyRepository companyRepository, AppUserRepository userRepository, TaskRepository taskRepository, PasswordEncoder passwordEncoder) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long totalCompanies = companyRepository.count();
        long activeCompanies = companyRepository.findAll().stream().filter(c -> c.getStatus() == Company.CompanyStatus.ACTIVE).count();
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByStatus(AppUser.UserStatus.ACTIVE);

        model.addAttribute("totalCompanies", totalCompanies);
        model.addAttribute("activeCompanies", activeCompanies);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("companies", companyRepository.findAll());
        return "superadmin/dashboard";
    }

    @GetMapping("/companies")
    public String companies(Model model) {
        model.addAttribute("companies", companyRepository.findAll());
        return "superadmin/companies";
    }

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
        if (companyRepository.findBySlug(slug).isPresent()) {
            return "redirect:/superadmin/dashboard?error=slug_exists";
        }
        Company company = new Company();
        company.setName(name);
        company.setSlug(slug);
        company.setDomain(domain);
        company.setEmail(email);
        company.setMaxHRs(maxHRs);
        company.setMaxEmployees(maxEmployees);
        company.setStatus(Company.CompanyStatus.ACTIVE);
        company = companyRepository.save(company);

        // Create Company Admin User
        AppUser admin = new AppUser();
        admin.setUsername(adminUsername);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(AppUser.UserRole.COMPANY_ADMIN);
        admin.setCompany(company);
        admin.setFullName(name + " Admin");
        admin.setEmail(email);
        admin.setStatus(AppUser.UserStatus.ACTIVE);
        userRepository.save(admin);

        return "redirect:/superadmin/dashboard?success=created";
    }

    @PostMapping("/companies/toggle-status")
    public String toggleCompanyStatus(@RequestParam("companyId") Long companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow();
        if (company.getStatus() == Company.CompanyStatus.ACTIVE) {
            company.setStatus(Company.CompanyStatus.SUSPENDED);
        } else {
            company.setStatus(Company.CompanyStatus.ACTIVE);
        }
        companyRepository.save(company);
        return "redirect:/superadmin/dashboard?success=status_updated";
    }

    @PostMapping("/companies/delete")
    public String deleteCompany(@RequestParam("companyId") Long companyId) {
        companyRepository.deleteById(companyId);
        return "redirect:/superadmin/dashboard?success=deleted";
    }
}
