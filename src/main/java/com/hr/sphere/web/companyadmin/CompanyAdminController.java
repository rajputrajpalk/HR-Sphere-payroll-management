package com.hr.sphere.web.companyadmin;

import com.hr.sphere.model.AppUser;
import com.hr.sphere.model.Company;
import com.hr.sphere.repo.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/companyadmin")
public class CompanyAdminController {

    private final AppUserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final TaskRepository taskRepository;
    private final LeaveApplicationRepository leaveRepository;
    private final PayrollRepository payrollRepository;
    private final PasswordEncoder passwordEncoder;

    public CompanyAdminController(AppUserRepository userRepository, CompanyRepository companyRepository, TaskRepository taskRepository, LeaveApplicationRepository leaveRepository, PayrollRepository payrollRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.taskRepository = taskRepository;
        this.leaveRepository = leaveRepository;
        this.payrollRepository = payrollRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private AppUser getCurrentUser(Authentication auth) {
        return userRepository.findByUsername(auth.getName()).orElseThrow();
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        AppUser admin = getCurrentUser(auth);
        Company company = admin.getCompany();

        List<AppUser> hrList = userRepository.findByCompanyIdAndRole(company.getId(), AppUser.UserRole.HR);
        List<AppUser> employeeList = userRepository.findByCompanyIdAndRole(company.getId(), AppUser.UserRole.EMPLOYEE);

        long totalTasks = taskRepository.countByCompanyId(company.getId());
        long completedTasks = taskRepository.countByCompanyIdAndStatus(company.getId(), com.hr.sphere.model.Task.TaskStatus.COMPLETED);

        model.addAttribute("company", company);
        model.addAttribute("hrs", hrList);
        model.addAttribute("employees", employeeList);
        model.addAttribute("totalTasks", totalTasks);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("completionRate", totalTasks > 0 ? (completedTasks * 100 / totalTasks) : 0);

        return "companyadmin/dashboard";
    }

    @PostMapping("/hr/create")
    public String createHR(Authentication auth,
                           @RequestParam("username") @NotBlank String username,
                           @RequestParam("password") @NotBlank String password,
                           @RequestParam("fullName") @NotBlank String fullName,
                           @RequestParam("email") @NotBlank String email) {
        AppUser admin = getCurrentUser(auth);
        Company company = admin.getCompany();

        long currentHRCount = userRepository.countByCompanyIdAndRole(company.getId(), AppUser.UserRole.HR);
        if (currentHRCount >= company.getMaxHRs()) {
            return "redirect:/companyadmin/dashboard?error=hr_limit_reached";
        }

        AppUser hr = new AppUser();
        hr.setUsername(username);
        hr.setPassword(passwordEncoder.encode(password));
        hr.setFullName(fullName);
        hr.setEmail(email);
        hr.setRole(AppUser.UserRole.HR);
        hr.setCompany(company);
        hr.setStatus(AppUser.UserStatus.ACTIVE);
        userRepository.save(hr);

        return "redirect:/companyadmin/dashboard?success=hr_created";
    }

    @PostMapping("/hr/toggle-status")
    public String toggleHRStatus(@RequestParam("hrId") Long hrId) {
        AppUser hr = userRepository.findById(hrId).orElseThrow();
        if (hr.getStatus() == AppUser.UserStatus.ACTIVE) {
            hr.setStatus(AppUser.UserStatus.INACTIVE);
        } else {
            hr.setStatus(AppUser.UserStatus.ACTIVE);
        }
        userRepository.save(hr);
        return "redirect:/companyadmin/dashboard?success=hr_status_updated";
    }

    @PostMapping("/hr/delete")
    public String deleteHR(@RequestParam("hrId") Long hrId) {
        userRepository.deleteById(hrId);
        return "redirect:/companyadmin/dashboard?success=hr_deleted";
    }
}
