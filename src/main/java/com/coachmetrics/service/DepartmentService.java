package com.coachmetrics.service;

import com.coachmetrics.entity.DepartmentEntity;
import com.coachmetrics.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DepartmentService {
    @Autowired private DepartmentRepository repo;
    @Autowired private ActivityLogService logService;

    public List<DepartmentEntity> getAll()    { return repo.findByActiveTrue(); }

    public DepartmentEntity create(String name, String code) {
        if (repo.existsByCode(code)) throw new IllegalArgumentException("Department code already exists: " + code);
        DepartmentEntity d = new DepartmentEntity(name, code);
        d = repo.save(d);
        logService.log("DEPT_ADDED", "Department added: " + name, "Admin", name);
        return d;
    }

    public void delete(Long id) {
        DepartmentEntity d = repo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        d.setActive(false);
        repo.save(d);
    }
}
