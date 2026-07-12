package com.hr.sphere.web.superadmin;

import com.hr.sphere.model.AppUser;
import com.hr.sphere.model.Company;
import com.hr.sphere.repo.AppUserRepository;
import com.hr.sphere.repo.CompanyRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/superadmin")
@Validated
public class SuperAdminController {

    private final CompanyRepository companyRepository;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SuperAdminController(CompanyRepository companyRepository, AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "superadmin/dashboard";
    }

    @GetMapping("/companies")
    public String companies(Model model) {
        model.addAttribute("companies", companyRepository.findAll());
        return "superadmin/companies";
    }

    @PostMapping("/companies/create")
    public String createCompany(
            @RequestParam @NotBlank String name,
            @RequestParam @NotBlank String slug
    ) {
        if (companyRepository.findBySlug(slug).isPresent()) {
            return "redirect:/superadmin/companies?error=slug_exists";
        }
        Company c = new Company();
        c.setName(name);
        c.setSlug(slug);
        c.setStatus(Company.CompanyStatus.APPROVED);
        companyRepository.save(c);
        return "redirect:/superadmin/companies";
    }

    @GetMapping("/hr-pending")
    public String hrPending(Model model) {
        // pending HRs are those role=HR and approvalStatus=PENDING
        // We don't have a repository method; implement via all users is simpler for now.
        List<AppUser> pending = userRepository.findAll().stream()
                .filter(u -> u.getRole() == AppUser.UserRole.HR)
                .filter(u -> u.getApprovalStatus() == AppUser.ApprovalStatus.PENDING)
                .toList();
        model.addAttribute("pendingHrUsers", pending);
        return "superadmin/hr-pending";
    }

    @PostMapping("/hr/approve")
    public String approveHr(@RequestParam Long userId) {
        AppUser user = userRepository.findById(userId).orElseThrow();
        user.setApprovalStatus(AppUser.ApprovalStatus.APPROVED);
        userRepository.save(user);
        return "redirect:/superadmin/hr-pending";
    }

    @PostMapping("/hr/reject")
    public String rejectHr(@RequestParam Long userId) {
        userRepository.deleteById(userId);
        return "redirect:/superadmin/hr-pending";
    }
}

