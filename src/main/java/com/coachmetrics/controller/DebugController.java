package com.coachmetrics.controller;

import com.coachmetrics.entity.MentorConnect;
import com.coachmetrics.repository.MentorConnectRepository;
import com.coachmetrics.repository.MentorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/coach/debug")
public class DebugController {

    @Autowired private MentorRepository mentorRepo;
    @Autowired private MentorConnectRepository connectRepo;

    /**
     * GET /api/coach/debug/connects/{mentorName}
     * Shows all 14 week connect rows for a mentor
     * Use this to verify DB state after agent update
     */
    @GetMapping("/connects/{mentorName}")
    public ResponseEntity<?> getConnects(@PathVariable String mentorName) {
        var mentors = mentorRepo.findAll().stream()
            .filter(m -> m.getFullName().toLowerCase().contains(mentorName.toLowerCase()))
            .collect(Collectors.toList());

        if (mentors.isEmpty()) {
            return ResponseEntity.ok(Map.of("error", "No mentor found: " + mentorName));
        }

        var result = new LinkedHashMap<String, Object>();
        for (var mentor : mentors) {
            List<MentorConnect> connects = connectRepo
                .findByMentorIdOrderByWeekNumberAsc(mentor.getId());

            var rows = connects.stream().map(c -> {
                var row = new LinkedHashMap<String, Object>();
                row.put("id",          c.getId());
                row.put("weekNumber",  c.getWeekNumber());
                row.put("weekRange",   c.getWeekRange());
                row.put("happened",    c.isHappened());
                row.put("mode",        c.getMode());
                row.put("hours",       c.getHours());
                row.put("connectDate", c.getConnectDate());
                return row;
            }).collect(Collectors.toList());

            result.put(mentor.getFullName() + " (id=" + mentor.getId() + ")", rows);
        }
        return ResponseEntity.ok(result);
    }
}
