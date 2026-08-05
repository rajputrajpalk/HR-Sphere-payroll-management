package com.hr.sphere.repo;

import com.hr.sphere.model.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {
    List<LeaveApplication> findByCompanyId(Long companyId);

    List<LeaveApplication> findByEmployeeId(Long employeeId);

    List<LeaveApplication> findByCompanyIdAndStatus(Long companyId, LeaveApplication.LeaveStatus status);
}
