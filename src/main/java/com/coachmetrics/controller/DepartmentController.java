package com.coachmetrics.controller;

import com.coachmetrics.entity.DepartmentEntity;
import com.coachmetrics.entity.VerticalEntity;
import com.coachmetrics.service.DepartmentService;
import com.coachmetrics.service.VerticalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
public class DepartmentController {

    @Autowired private DepartmentService deptService;
    @Autowired private VerticalService verticalService;

    // Public endpoints for dropdowns (any authenticated user)
    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentEntity>> getDepartments() {
        return ResponseEntity.ok(deptService.getAll());
    }

    @GetMapping("/verticals")
    public ResponseEntity<List<VerticalEntity>> getVerticals() {
        return ResponseEntity.ok(verticalService.getAll());
    }

    // Admin only
    @PostMapping("/admin/departments")
    public ResponseEntity<DepartmentEntity> createDept(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(deptService.create(body.get("name"), body.get("code")));
    }

    @DeleteMapping("/admin/departments/{id}")
    public ResponseEntity<Void> deleteDept(@PathVariable Long id) {
        deptService.delete(id); return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/verticals")
    public ResponseEntity<VerticalEntity> createVertical(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(verticalService.create(body.get("name")));
    }

    @DeleteMapping("/admin/verticals/{id}")
    public ResponseEntity<Void> deleteVertical(@PathVariable Long id) {
        verticalService.delete(id); return ResponseEntity.noContent().build();
    }
}
