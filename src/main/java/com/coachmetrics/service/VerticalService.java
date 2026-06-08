package com.coachmetrics.service;

import com.coachmetrics.entity.VerticalEntity;
import com.coachmetrics.repository.VerticalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VerticalService {
    @Autowired private VerticalRepository repo;
    @Autowired private ActivityLogService logService;

    public List<VerticalEntity> getAll() { return repo.findByActiveTrue(); }

    public VerticalEntity create(String name) {
        if (repo.existsByName(name)) throw new IllegalArgumentException("Vertical already exists: " + name);
        VerticalEntity v = new VerticalEntity(name);
        v = repo.save(v);
        logService.log("VERTICAL_ADDED", "Vertical added: " + name, "Admin", name);
        return v;
    }

    public void delete(Long id) {
        VerticalEntity v = repo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        v.setActive(false);
        repo.save(v);
    }
}
