package com.hr.sphere.web.companyadmin;

import com.hr.sphere.model.AppUser;
import com.hr.sphere.model.Company;
import com.hr.sphere.model.Task;
import com.hr.sphere.repo.AppUserRepository;
import com.hr.sphere.repo.TaskRepository;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ============================================================================
 * CompanyAdminController (Tenant Organization Administration)
 * ============================================================================
 * 
 * Provides tenant-level administration capabilities:
 *  - Company metrics (active HR managers, employee headcount, task completion rate)
 *  - Onboarding and managing HR Managers within assigned quota limits
 *  - Activating/Deactivating individual HR personnel accounts
 *  - Removing HR personnel
 */
@Controller
@RequestMapping("/companyadmin")
@Validated
public class CompanyAdminController {

    private static final Logger log = LoggerFactory.getLogger(CompanyAdminController.class);

    private final AppUserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    public CompanyAdminController(AppUserRepository userRepository,
                                  TaskRepository taskRepository,
                                  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Resolves the currently authenticated Company Admin user principal.
     */
    private AppUser getCurrentAdmin(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated admin user not found: " + auth.getName()));
    }

    /**
     * Renders the Company Admin dashboard with company metrics and HR manager roster.
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        AppUser admin = getCurrentAdmin(auth);
        Company company = admin.getCompany();

        List<AppUser> hrList = userRepository.findByCompanyIdAndRole(company.getId(), AppUser.UserRole.HR);
        List<AppUser> employeeList = userRepository.findByCompanyIdAndRole(company.getId(), AppUser.UserRole.EMPLOYEE);

        long totalTasks = taskRepository.countByCompanyId(company.getId());
        long completedTasks = taskRepository.countByCompanyIdAndStatus(company.getId(), Task.TaskStatus.COMPLETED);
        long completionRate = totalTasks > 0 ? (completedTasks * 100 / totalTasks) : 0;

        model.addAttribute("company", company);
        model.addAttribute("hrs", hrList);
        model.addAttribute("employees", employeeList);
        model.addAttribute("totalTasks", totalTasks);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("completionRate", completionRate);

        log.debug("Loaded Company Admin dashboard for '{}': {} HRs, {} Employees, {} Tasks.",
                company.getName(), hrList.size(), employeeList.size(), totalTasks);
        return "companyadmin/dashboard";
    }

    /**
     * Creates and registers a new HR Manager under this tenant company,
     * subject to company HR capacity limits.
     */
    @PostMapping("/hr/create")
    public String createHR(
            Authentication auth,
            @RequestParam("username") @NotBlank String username,
            @RequestParam("password") @NotBlank String password,
            @RequestParam("fullName") @NotBlank String fullName,
            @RequestParam("email") @NotBlank String email
    ) {
        AppUser admin = getCurrentAdmin(auth);
        Company company = admin.getCompany();

        // 1. Validate HR Quota Limit
        long currentHRCount = userRepository.countByCompanyIdAndRole(company.getId(), AppUser.UserRole.HR);
        if (currentHRCount >= company.getMaxHRs()) {
            log.warn("Cannot add HR: Company '{}' reached max limit of {} HRs.", company.getName(), company.getMaxHRs());
            return "redirect:/companyadmin/dashboard?error=hr_limit_reached";
        }

        // 2. Validate Username Uniqueness
        if (userRepository.findByUsername(username.trim()).isPresent()) {
            log.warn("Cannot add HR: Username '{}' is already taken.", username);
            return "redirect:/companyadmin/dashboard?error=username_exists";
        }

        // 3. Create and persist HR Manager
        AppUser hr = new AppUser();
        hr.setUsername(username.trim());
        hr.setPassword(passwordEncoder.encode(password));
        hr.setFullName(fullName.trim());
        hr.setEmail(email.trim());
        hr.setRole(AppUser.UserRole.HR);
        hr.setCompany(company);
        hr.setStatus(AppUser.UserStatus.ACTIVE);
        userRepository.save(hr);

        log.info("Registered new HR Manager '{}' for company '{}'", username, company.getName());
        return "redirect:/companyadmin/dashboard?success=hr_created";
    }

    /**
     * Toggles an HR Manager's account status between ACTIVE and INACTIVE.
     */
    @PostMapping("/hr/toggle-status")
    public String toggleHRStatus(@RequestParam("hrId") Long hrId) {
        AppUser hr = userRepository.findById(hrId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid HR user ID: " + hrId));

        AppUser.UserStatus newStatus = hr.isActive() ? AppUser.UserStatus.INACTIVE : AppUser.UserStatus.ACTIVE;
        hr.setStatus(newStatus);
        userRepository.save(hr);

        log.info("Toggled HR Manager '{}' status to: {}", hr.getUsername(), newStatus);
        return "redirect:/companyadmin/dashboard?success=hr_status_updated";
    }

    /**
     * Removes an HR Manager from the system.
     */
    @PostMapping("/hr/delete")
    public String deleteHR(@RequestParam("hrId") Long hrId) {
        userRepository.findById(hrId).ifPresent(hr -> {
            userRepository.delete(hr);
            log.warn("Deleted HR Manager '{}' (ID: {})", hr.getUsername(), hrId);
        });
        return "redirect:/companyadmin/dashboard?success=hr_deleted";
    }
}
