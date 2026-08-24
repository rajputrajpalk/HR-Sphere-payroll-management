package com.hr.sphere.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * ============================================================================
 * LeaveApplication Entity (Employee Leave Requests & Approvals)
 * ============================================================================
 * 
 * Manages employee time-off requests, reasons, date ranges, and HR review workflows.
 * Scoped directly to a tenant company to ensure strict data segregation.
 */
@Entity
@Table(name = "leave_applications", indexes = {
    @Index(name = "idx_leave_company", columnList = "company_id"),
    @Index(name = "idx_leave_employee", columnList = "employee_id"),
    @Index(name = "idx_leave_status", columnList = "status")
})
public class LeaveApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private AppUser employee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hr_id")
    private AppUser hr;

    @Column(nullable = false, length = 1000)
    private String reason;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LeaveStatus status = LeaveStatus.PENDING;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Leave request status lifecycle.
     */
    public enum LeaveStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    // ========================================================================
    // Domain & Business Helper Methods
    // ========================================================================

    public boolean isPending() {
        return this.status == LeaveStatus.PENDING;
    }

    public boolean isApproved() {
        return this.status == LeaveStatus.APPROVED;
    }

    public boolean isRejected() {
        return this.status == LeaveStatus.REJECTED;
    }

    /**
     * Calculates the total number of calendar days for this leave period (inclusive).
     */
    public long getDurationInDays() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    // ========================================================================
    // Getters and Setters
    // ========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AppUser getEmployee() {
        return employee;
    }

    public void setEmployee(AppUser employee) {
        this.employee = employee;
    }

    public AppUser getHr() {
        return hr;
    }

    public void setHr(AppUser hr) {
        this.hr = hr;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "LeaveApplication{" +
                "id=" + id +
                ", employee=" + (employee != null ? employee.getUsername() : null) +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                '}';
    }
}
