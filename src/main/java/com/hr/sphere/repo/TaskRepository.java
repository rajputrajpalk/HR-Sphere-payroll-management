package com.hr.sphere.repo;

import com.hr.sphere.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ============================================================================
 * TaskRepository (Data Access for Task Delegation & Tracking)
 * ============================================================================
 * 
 * Provides database operations for task assignment, employee workspace queues,
 * HR manager delegation boards, and completion rate calculations.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Retrieves all tasks scoped to a specific tenant company.
     * SQL: SELECT * FROM tasks WHERE company_id = ?
     */
    List<Task> findByCompanyId(Long companyId);

    /**
     * Retrieves tasks assigned to a specific employee within their company.
     * SQL: SELECT * FROM tasks WHERE company_id = ? AND assigned_to_user_id = ?
     */
    List<Task> findByCompanyIdAndAssignedToId(Long companyId, Long assignedToId);

    /**
     * Retrieves tasks created by a specific HR manager within their company.
     * SQL: SELECT * FROM tasks WHERE company_id = ? AND created_by_hr_user_id = ?
     */
    List<Task> findByCompanyIdAndCreatedById(Long companyId, Long hrId);

    /**
     * Counts total tasks within a company.
     * SQL: SELECT COUNT(*) FROM tasks WHERE company_id = ?
     */
    long countByCompanyId(Long companyId);

    /**
     * Counts tasks within a company by status (PENDING, IN_PROGRESS, COMPLETED).
     * SQL: SELECT COUNT(*) FROM tasks WHERE company_id = ? AND status = ?
     */
    long countByCompanyIdAndStatus(Long companyId, Task.TaskStatus status);
}
