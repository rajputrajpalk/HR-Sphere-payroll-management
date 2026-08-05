package com.hr.sphere.web.hr;

import com.hr.sphere.model.*;
import com.hr.sphere.repo.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/hr")
public class HRController {

    private final AppUserRepository userRepository;
    private final TaskRepository taskRepository;
    private final LeaveApplicationRepository leaveRepository;
    private final PayrollRepository payrollRepository;
    private final PasswordEncoder passwordEncoder;

    public HRController(AppUserRepository userRepository, TaskRepository taskRepository, LeaveApplicationRepository leaveRepository, PayrollRepository payrollRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.leaveRepository = leaveRepository;
        this.payrollRepository = payrollRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private AppUser getCurrentHR(Authentication auth) {
        return userRepository.findByUsername(auth.getName()).orElseThrow();
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        AppUser hr = getCurrentHR(auth);
        Company company = hr.getCompany();

        List<AppUser> managedEmployees = userRepository.findByManagedByHRId(hr.getId());
        List<Task> tasks = taskRepository.findByCompanyIdAndCreatedById(company.getId(), hr.getId());
        List<LeaveApplication> leaves = leaveRepository.findByCompanyId(company.getId());
        List<Payroll> payrolls = payrollRepository.findByCompanyId(company.getId());

        model.addAttribute("hr", hr);
        model.addAttribute("company", company);
        model.addAttribute("employees", managedEmployees);
        model.addAttribute("tasks", tasks);
        model.addAttribute("leaves", leaves);
        model.addAttribute("payrolls", payrolls);

        return "hr/dashboard";
    }

    @PostMapping("/employee/create")
    public String createEmployee(Authentication auth,
                                 @RequestParam("username") @NotBlank String username,
                                 @RequestParam("password") @NotBlank String password,
                                 @RequestParam("fullName") @NotBlank String fullName,
                                 @RequestParam("email") @NotBlank String email) {
        AppUser hr = getCurrentHR(auth);
        Company company = hr.getCompany();

        AppUser employee = new AppUser();
        employee.setUsername(username);
        employee.setPassword(passwordEncoder.encode(password));
        employee.setFullName(fullName);
        employee.setEmail(email);
        employee.setRole(AppUser.UserRole.EMPLOYEE);
        employee.setCompany(company);
        employee.setManagedByHR(hr);
        employee.setStatus(AppUser.UserStatus.ACTIVE);
        userRepository.save(employee);

        return "redirect:/hr/dashboard?success=employee_created";
    }

    @PostMapping("/task/create")
    public String createTask(Authentication auth,
                             @RequestParam("assignedToUserId") Long assignedToUserId,
                             @RequestParam("title") @NotBlank String title,
                             @RequestParam("description") @NotBlank String description,
                             @RequestParam("priority") Task.TaskPriority priority,
                             @RequestParam(value = "dueDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        AppUser hr = getCurrentHR(auth);
        AppUser employee = userRepository.findById(assignedToUserId).orElseThrow();

        Task task = new Task();
        task.setCompany(hr.getCompany());
        task.setCreatedBy(hr);
        task.setAssignedTo(employee);
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setStatus(Task.TaskStatus.PENDING);
        task.setDueDate(dueDate);
        taskRepository.save(task);

        return "redirect:/hr/dashboard?success=task_created";
    }

    @PostMapping("/leave/approve")
    public String approveLeave(Authentication auth, @RequestParam("leaveId") Long leaveId) {
        AppUser hr = getCurrentHR(auth);
        LeaveApplication leave = leaveRepository.findById(leaveId).orElseThrow();
        leave.setStatus(LeaveApplication.LeaveStatus.APPROVED);
        leave.setHr(hr);
        leaveRepository.save(leave);
        return "redirect:/hr/dashboard?success=leave_approved";
    }

    @PostMapping("/leave/reject")
    public String rejectLeave(Authentication auth, @RequestParam("leaveId") Long leaveId) {
        AppUser hr = getCurrentHR(auth);
        LeaveApplication leave = leaveRepository.findById(leaveId).orElseThrow();
        leave.setStatus(LeaveApplication.LeaveStatus.REJECTED);
        leave.setHr(hr);
        leaveRepository.save(leave);
        return "redirect:/hr/dashboard?success=leave_rejected";
    }

    @PostMapping("/payroll/generate")
    public String generatePayroll(Authentication auth,
                                  @RequestParam("employeeId") Long employeeId,
                                  @RequestParam("baseSalary") BigDecimal baseSalary,
                                  @RequestParam(value = "bonuses", required = false) BigDecimal bonuses,
                                  @RequestParam(value = "deductions", required = false) BigDecimal deductions,
                                  @RequestParam("payPeriod") String payPeriod) {
        AppUser hr = getCurrentHR(auth);
        AppUser employee = userRepository.findById(employeeId).orElseThrow();

        Payroll p = new Payroll();
        p.setCompany(hr.getCompany());
        p.setEmployee(employee);
        p.setBaseSalary(baseSalary);
        p.setBonuses(bonuses != null ? bonuses : BigDecimal.ZERO);
        p.setDeductions(deductions != null ? deductions : BigDecimal.ZERO);
        p.setPayPeriod(payPeriod);
        p.setStatus(Payroll.PayrollStatus.PENDING);
        payrollRepository.save(p);

        return "redirect:/hr/dashboard?success=payroll_generated";
    }
}
