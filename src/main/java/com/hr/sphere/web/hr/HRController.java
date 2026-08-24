package com.hr.sphere.web.hr;

import com.hr.sphere.model.*;
import com.hr.sphere.repo.*;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * ============================================================================
 * HRController (Human Resource Operational Management)
 * ============================================================================
 * 
 * Provides daily operational workflows for HR Managers:
 *  - Employee registration and onboarding under direct supervision
 *  - Task creation, prioritization, deadline setting, and employee delegation
 *  - Reviewing employee leave applications (Approve / Reject)
 *  - Monthly payroll statement generation and payslip distribution
 */
@Controller
@RequestMapping("/hr")
@Validated
public class HRController {

    private static final Logger log = LoggerFactory.getLogger(HRController.class);

    private final AppUserRepository userRepository;
    private final TaskRepository taskRepository;
    private final LeaveApplicationRepository leaveRepository;
    private final PayrollRepository payrollRepository;
    private final PasswordEncoder passwordEncoder;

    public HRController(AppUserRepository userRepository,
                        TaskRepository taskRepository,
                        LeaveApplicationRepository leaveRepository,
                        PayrollRepository payrollRepository,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.leaveRepository = leaveRepository;
        this.payrollRepository = payrollRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Resolves the currently authenticated HR Manager user principal.
     */
    private AppUser getCurrentHR(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated HR user not found: " + auth.getName()));
    }

    /**
     * Renders the comprehensive HR Operations Dashboard.
     */
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

        log.debug("Loaded HR dashboard for '{}': {} staff, {} tasks, {} leaves, {} payrolls.",
                hr.getUsername(), managedEmployees.size(), tasks.size(), leaves.size(), payrolls.size());
        return "hr/dashboard";
    }

    /**
     * Registers and onboards a new Employee assigned to this HR Manager.
     */
    @PostMapping("/employee/create")
    public String createEmployee(
            Authentication auth,
            @RequestParam("username") @NotBlank String username,
            @RequestParam("password") @NotBlank String password,
            @RequestParam("fullName") @NotBlank String fullName,
            @RequestParam("email") @NotBlank String email
    ) {
        AppUser hr = getCurrentHR(auth);
        Company company = hr.getCompany();

        // Check Employee Capacity Limit
        long currentEmployees = userRepository.countByCompanyIdAndRole(company.getId(), AppUser.UserRole.EMPLOYEE);
        if (currentEmployees >= company.getMaxEmployees()) {
            log.warn("Cannot register employee: Company '{}' reached max limit of {} employees.",
                    company.getName(), company.getMaxEmployees());
            return "redirect:/hr/dashboard?error=employee_limit_reached";
        }

        // Check Username Uniqueness
        if (userRepository.findByUsername(username.trim()).isPresent()) {
            log.warn("Cannot register employee: Username '{}' already taken.", username);
            return "redirect:/hr/dashboard?error=username_exists";
        }

        AppUser employee = new AppUser();
        employee.setUsername(username.trim());
        employee.setPassword(passwordEncoder.encode(password));
        employee.setFullName(fullName.trim());
        employee.setEmail(email.trim());
        employee.setRole(AppUser.UserRole.EMPLOYEE);
        employee.setCompany(company);
        employee.setManagedByHR(hr);
        employee.setStatus(AppUser.UserStatus.ACTIVE);
        userRepository.save(employee);

        log.info("HR '{}' registered new Employee '{}' for company '{}'", hr.getUsername(), username, company.getName());
        return "redirect:/hr/dashboard?success=employee_created";
    }

    /**
     * Creates and delegates a new work task to an assigned employee.
     */
    @PostMapping("/task/create")
    public String createTask(
            Authentication auth,
            @RequestParam("assignedToUserId") Long assignedToUserId,
            @RequestParam("title") @NotBlank String title,
            @RequestParam("description") @NotBlank String description,
            @RequestParam(value = "priority", defaultValue = "MEDIUM") Task.TaskPriority priority,
            @RequestParam(value = "dueDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate
    ) {
        AppUser hr = getCurrentHR(auth);
        AppUser employee = userRepository.findById(assignedToUserId)
                .orElseThrow(() -> new IllegalArgumentException("Target employee not found: " + assignedToUserId));

        Task task = new Task();
        task.setCompany(hr.getCompany());
        task.setCreatedBy(hr);
        task.setAssignedTo(employee);
        task.setTitle(title.trim());
        task.setDescription(description.trim());
        task.setPriority(priority != null ? priority : Task.TaskPriority.MEDIUM);
        task.setStatus(Task.TaskStatus.PENDING);
        task.setDueDate(dueDate);
        taskRepository.save(task);

        log.info("HR '{}' delegated task '{}' to employee '{}'", hr.getUsername(), title, employee.getUsername());
        return "redirect:/hr/dashboard?success=task_created";
    }

    /**
     * Approves an employee leave application.
     */
    @PostMapping("/leave/approve")
    public String approveLeave(Authentication auth, @RequestParam("leaveId") Long leaveId) {
        AppUser hr = getCurrentHR(auth);
        LeaveApplication leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("Leave application not found: " + leaveId));

        leave.setStatus(LeaveApplication.LeaveStatus.APPROVED);
        leave.setHr(hr);
        leaveRepository.save(leave);

        log.info("HR '{}' APPROVED leave application ID: {} for employee '{}'",
                hr.getUsername(), leaveId, leave.getEmployee().getUsername());
        return "redirect:/hr/dashboard?success=leave_approved";
    }

    /**
     * Rejects an employee leave application.
     */
    @PostMapping("/leave/reject")
    public String rejectLeave(Authentication auth, @RequestParam("leaveId") Long leaveId) {
        AppUser hr = getCurrentHR(auth);
        LeaveApplication leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("Leave application not found: " + leaveId));

        leave.setStatus(LeaveApplication.LeaveStatus.REJECTED);
        leave.setHr(hr);
        leaveRepository.save(leave);

        log.info("HR '{}' REJECTED leave application ID: {} for employee '{}'",
                hr.getUsername(), leaveId, leave.getEmployee().getUsername());
        return "redirect:/hr/dashboard?success=leave_rejected";
    }

    /**
     * Generates a monthly payroll payslip statement for an employee.
     */
    @PostMapping("/payroll/generate")
    public String generatePayroll(
            Authentication auth,
            @RequestParam("employeeId") Long employeeId,
            @RequestParam("baseSalary") BigDecimal baseSalary,
            @RequestParam(value = "bonuses", required = false) BigDecimal bonuses,
            @RequestParam(value = "deductions", required = false) BigDecimal deductions,
            @RequestParam("payPeriod") @NotBlank String payPeriod
    ) {
        AppUser hr = getCurrentHR(auth);
        AppUser employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Target employee not found: " + employeeId));

        Payroll payroll = new Payroll();
        payroll.setCompany(hr.getCompany());
        payroll.setEmployee(employee);
        payroll.setBaseSalary(baseSalary);
        payroll.setBonuses(bonuses != null ? bonuses : BigDecimal.ZERO);
        payroll.setDeductions(deductions != null ? deductions : BigDecimal.ZERO);
        payroll.setPayPeriod(payPeriod.trim());
        payroll.setStatus(Payroll.PayrollStatus.PENDING);
        payrollRepository.save(payroll);

        log.info("HR '{}' generated payroll for '{}' (Period: {}, Net: Rs. {})",
                hr.getUsername(), employee.getUsername(), payPeriod, payroll.calculateNetSalary());
        return "redirect:/hr/dashboard?success=payroll_generated";
    }
}
