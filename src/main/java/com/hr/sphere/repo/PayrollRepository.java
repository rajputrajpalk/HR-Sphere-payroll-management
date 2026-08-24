package com.hr.sphere.repo;

import com.hr.sphere.model.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ============================================================================
 * PayrollRepository (Data Access for Salary Records & Payslips)
 * ============================================================================
 * 
 * Provides database operations for salary generation, company-wide payroll review,
 * and individual employee payslip history queries.
 */
@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    /**
     * Retrieves all payroll records generated within a tenant company.
     * SQL: SELECT * FROM payrolls WHERE company_id = ?
     */
    List<Payroll> findByCompanyId(Long companyId);

    /**
     * Retrieves all payslips issued to a specific employee.
     * SQL: SELECT * FROM payrolls WHERE employee_id = ?
     */
    List<Payroll> findByEmployeeId(Long employeeId);

    /**
     * Retrieves all payslips issued to an employee for a specific billing period.
     * SQL: SELECT * FROM payrolls WHERE employee_id = ? AND pay_period = ?
     */
    List<Payroll> findByEmployeeIdAndPayPeriod(Long employeeId, String payPeriod);
}
