package com.hr.sphere.web.employee;

import com.hr.sphere.model.*;
import com.hr.sphere.repo.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    private final AppUserRepository userRepository;
    private final TaskRepository taskRepository;
    private final LeaveApplicationRepository leaveRepository;
    private final PayrollRepository payrollRepository;

    public EmployeeController(AppUserRepository userRepository, TaskRepository taskRepository, LeaveApplicationRepository leaveRepository, PayrollRepository payrollRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.leaveRepository = leaveRepository;
        this.payrollRepository = payrollRepository;
    }

    private AppUser getCurrentEmployee(Authentication auth) {
        return userRepository.findByUsername(auth.getName()).orElseThrow();
    }

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

        return "employee/dashboard";
    }

    @PostMapping("/task/update-status")
    public String updateTaskStatus(@RequestParam("taskId") Long taskId, @RequestParam("status") Task.TaskStatus status) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        task.setStatus(status);
        taskRepository.save(task);
        return "redirect:/employee/dashboard?success=task_updated";
    }

    @PostMapping("/leave/apply")
    public String applyLeave(Authentication auth,
                             @RequestParam("reason") @NotBlank String reason,
                             @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                             @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        AppUser employee = getCurrentEmployee(auth);

        LeaveApplication leave = new LeaveApplication();
        leave.setCompany(employee.getCompany());
        leave.setEmployee(employee);
        leave.setReason(reason);
        leave.setStartDate(startDate);
        leave.setEndDate(endDate);
        leave.setStatus(LeaveApplication.LeaveStatus.PENDING);
        leaveRepository.save(leave);

        return "redirect:/employee/dashboard?success=leave_applied";
    }
}
