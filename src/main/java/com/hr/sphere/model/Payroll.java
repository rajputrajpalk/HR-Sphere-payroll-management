package com.hr.sphere.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * Payroll Entity (Salary Records & Monthly Payslips)
 * ============================================================================
 * 
 * Stores monthly salary disbursement statements including base salary, bonuses,
 * deductions, pay period descriptions, and payment statuses.
 * Scoped directly to a tenant company to ensure strict data segregation.
 */
@Entity
@Table(name = "payrolls", indexes = {
    @Index(name = "idx_payroll_company", columnList = "company_id"),
    @Index(name = "idx_payroll_employee", columnList = "employee_id"),
    @Index(name = "idx_payroll_period", columnList = "payPeriod")
})
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private AppUser employee;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal baseSalary;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal bonuses = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal deductions = BigDecimal.ZERO;

    @Column(nullable = false, length = 50)
    private String payPeriod; // e.g. "August 2026"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PayrollStatus status = PayrollStatus.PENDING;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Payroll disbursement status.
     */
    public enum PayrollStatus {
        PENDING,
        PAID
    }

    // ========================================================================
    // Domain & Business Helper Methods
    // ========================================================================

    /**
     * Computes the net payable salary: (baseSalary + bonuses - deductions).
     */
    public BigDecimal calculateNetSalary() {
        BigDecimal base = baseSalary != null ? baseSalary : BigDecimal.ZERO;
        BigDecimal bonus = bonuses != null ? bonuses : BigDecimal.ZERO;
        BigDecimal deduction = deductions != null ? deductions : BigDecimal.ZERO;
        return base.add(bonus).subtract(deduction);
    }

    public boolean isPaid() {
        return this.status == PayrollStatus.PAID;
    }

    public boolean isPending() {
        return this.status == PayrollStatus.PENDING;
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

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }

    public BigDecimal getBonuses() {
        return bonuses;
    }

    public void setBonuses(BigDecimal bonuses) {
        this.bonuses = bonuses;
    }

    public BigDecimal getDeductions() {
        return deductions;
    }

    public void setDeductions(BigDecimal deductions) {
        this.deductions = deductions;
    }

    public String getPayPeriod() {
        return payPeriod;
    }

    public void setPayPeriod(String payPeriod) {
        this.payPeriod = payPeriod;
    }

    public PayrollStatus getStatus() {
        return status;
    }

    public void setStatus(PayrollStatus status) {
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
        return "Payroll{" +
                "id=" + id +
                ", employee=" + (employee != null ? employee.getUsername() : null) +
                ", payPeriod='" + payPeriod + '\'' +
                ", netSalary=" + calculateNetSalary() +
                ", status=" + status +
                '}';
    }
}
