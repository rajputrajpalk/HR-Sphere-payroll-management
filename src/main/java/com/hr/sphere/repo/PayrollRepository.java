package com.hr.sphere.repo;

import com.hr.sphere.model.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    List<Payroll> findByCompanyId(Long companyId);

    List<Payroll> findByEmployeeId(Long employeeId);
}
