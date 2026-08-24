package com.hr.sphere.repo;

import com.hr.sphere.model.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ============================================================================
 * LeaveApplicationRepository (Data Access for Employee Leave Requests)
 * ============================================================================
 * 
 * Provides database operations for leave request submissions, HR approval/rejection
 * workflows, and employee leave history tracking.
 */
@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {

    /**
     * Retrieves all leave applications submitted within a tenant company.
     * SQL: SELECT * FROM leave_applications WHERE company_id = ?
     */
    List<LeaveApplication> findByCompanyId(Long companyId);

    /**
     * Retrieves all leave applications submitted by a specific employee.
     * SQL: SELECT * FROM leave_applications WHERE employee_id = ?
     */
    List<LeaveApplication> findByEmployeeId(Long employeeId);

    /**
     * Retrieves leave applications within a company filtered by status (PENDING, APPROVED, REJECTED).
     * SQL: SELECT * FROM leave_applications WHERE company_id = ? AND status = ?
     */
    List<LeaveApplication> findByCompanyIdAndStatus(Long companyId, LeaveApplication.LeaveStatus status);

    /**
     * Counts leave applications within a company by status.
     * SQL: SELECT COUNT(*) FROM leave_applications WHERE company_id = ? AND status = ?
     */
    long countByCompanyIdAndStatus(Long companyId, LeaveApplication.LeaveStatus status);
}
