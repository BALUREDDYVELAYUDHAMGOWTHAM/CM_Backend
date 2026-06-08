package com.coachmetrics.controller;

import com.coachmetrics.dto.CoachDto;
import com.coachmetrics.service.CoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/coaches")
public class CoachController {

    @Autowired
    private CoachService coachService;

    @GetMapping
    public ResponseEntity<List<CoachDto.Response>> getCoaches(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String dept,
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(coachService.getAllCoaches(name, dept, active));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoachDto.Response> getCoach(@PathVariable Long id) {
        return ResponseEntity.ok(coachService.getCoachById(id));
    }

    @PostMapping
    public ResponseEntity<CoachDto.Response> createCoach(@RequestBody CoachDto.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(coachService.createCoach(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CoachDto.Response> updateCoach(@PathVariable Long id, @RequestBody CoachDto.Request req) {
        return ResponseEntity.ok(coachService.updateCoach(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoach(@PathVariable Long id) {
        coachService.deleteCoach(id);
        return ResponseEntity.noContent().build();
    }
}
