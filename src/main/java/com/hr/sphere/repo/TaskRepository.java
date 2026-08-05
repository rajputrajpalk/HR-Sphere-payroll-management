package com.hr.sphere.repo;

import com.hr.sphere.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCompanyId(Long companyId);

    List<Task> findByCompanyIdAndAssignedToId(Long companyId, Long assignedToId);

    List<Task> findByCompanyIdAndCreatedById(Long companyId, Long hrId);

    long countByCompanyId(Long companyId);

    long countByCompanyIdAndStatus(Long companyId, Task.TaskStatus status);
}
