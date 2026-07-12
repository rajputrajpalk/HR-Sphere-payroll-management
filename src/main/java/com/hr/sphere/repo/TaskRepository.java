package com.hr.sphere.repo;

import com.hr.sphere.model.AppUser;
import com.hr.sphere.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCompanyIdAndAssignedToId(Long companyId, Long assignedToId);
    List<Task> findByCompanyId(Long companyId);
}

