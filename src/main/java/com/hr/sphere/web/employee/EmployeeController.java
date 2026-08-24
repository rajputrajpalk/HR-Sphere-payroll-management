package com.hr.sphere.web.employee;

import com.hr.sphere.model.AppUser;
import com.hr.sphere.model.LeaveApplication;
import com.hr.sphere.model.Payroll;
import com.hr.sphere.model.Task;
import com.hr.sphere.repo.AppUserRepository;
import com.hr.sphere.repo.LeaveApplicationRepository;
import com.hr.sphere.repo.PayrollRepository;
import com.hr.sphere.repo.TaskRepository;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * ============================================================================
 * EmployeeController (Staff Self-Service Workspace)
 * ============================================================================
 * 
 * Provides self-service workspace capabilities for employees:
 *  - Viewing assigned tasks and updating work progress status
 *  - Submitting leave requests and monitoring approval outcomes
 *  - Viewing monthly payslips and compensation breakdowns
 */
@Controller
@RequestMapping("/employee")
@Validated
public class EmployeeController {

    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    private final AppUserRepository userRepository;
    private final TaskRepository taskRepository;
    private final LeaveApplicationRepository leaveRepository;
    private final PayrollRepository payrollRepository;

    public EmployeeController(AppUserRepository userRepository,
                              TaskRepository taskRepository,
                              LeaveApplicationRepository leaveRepository,
                              PayrollRepository payrollRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.leaveRepository = leaveRepository;
        this.payrollRepository = payrollRepository;
    }

    /**
     * Resolves the currently authenticated Employee user principal.
     */
    private AppUser getCurrentEmployee(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated employee not found: " + auth.getName()));
    }

    /**
     * Renders the Employee self-service workspace dashboard.
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        AppUser employee = getCurrentEmployee(auth);

        List<Task> myTasks = taskRepository.findByCompanyIdAndAssignedToId(employee.getCompany().getId(), employee.getId());
        List<LeaveApplication> myLeaves = leaveRepository.findByEmployeeId(employee.getId());
        List<Payroll> myPayrolls = payrollRepository.findByEmployeeId(employee.getId());

        model.addAttribute("employee", employee);
        model.addAttribute("tasks", myTasks);
        model.addAttribute("leaves", myLeaves);
        model.addAttribute("payrolls", myPayrolls);

        log.debug("Loaded Employee workspace for '{}': {} tasks, {} leaves, {} payslips.",
                employee.getUsername(), myTasks.size(), myLeaves.size(), myPayrolls.size());
        return "employee/dashboard";
    }

    /**
     * Updates the status of an assigned task (PENDING -> IN_PROGRESS -> COMPLETED).
     */
    @PostMapping("/task/update-status")
    public String updateTaskStatus(
            Authentication auth,
            @RequestParam("taskId") Long taskId,
            @RequestParam("status") Task.TaskStatus status
    ) {
        AppUser employee = getCurrentEmployee(auth);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        // Security check: Verify that this task is indeed assigned to this employee
        if (!task.getAssignedTo().getId().equals(employee.getId())) {
            log.warn("Unauthorized attempt by '{}' to update task ID: {}", employee.getUsername(), taskId);
            return "redirect:/employee/dashboard?error=unauthorized";
        }

        task.setStatus(status);
        taskRepository.save(task);

        log.info("Employee '{}' updated task ID: {} status to: {}", employee.getUsername(), taskId, status);
        return "redirect:/employee/dashboard?success=task_updated";
    }

    /**
     * Submits a new leave request for HR review.
     */
    @PostMapping("/leave/apply")
    public String applyLeave(
            Authentication auth,
            @RequestParam("reason") @NotBlank String reason,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        AppUser employee = getCurrentEmployee(auth);

        // Date sanity check
        if (endDate.isBefore(startDate)) {
            log.warn("Invalid leave dates submitted by '{}': end date {} is before start date {}",
                    employee.getUsername(), endDate, startDate);
            return "redirect:/employee/dashboard?error=invalid_dates";
        }

        LeaveApplication leave = new LeaveApplication();
        leave.setCompany(employee.getCompany());
        leave.setEmployee(employee);
        leave.setReason(reason.trim());
        leave.setStartDate(startDate);
        leave.setEndDate(endDate);
        leave.setStatus(LeaveApplication.LeaveStatus.PENDING);
        leaveRepository.save(leave);

        log.info("Employee '{}' applied for leave ({} to {}): {}",
                employee.getUsername(), startDate, endDate, reason);
        return "redirect:/employee/dashboard?success=leave_applied";
    }
}
