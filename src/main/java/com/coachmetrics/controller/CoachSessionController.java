package com.coachmetrics.controller;

import com.coachmetrics.dto.CoachSessionDto;
import com.coachmetrics.service.CoachSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/coach/sessions")
public class CoachSessionController {

    @Autowired
    private CoachSessionService sessionService;

    @PostMapping
    public ResponseEntity<CoachSessionDto.Response> create(
            @RequestBody CoachSessionDto.Request req,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sessionService.createSession(req, ud.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<CoachSessionDto.Response>> getMySessions(
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(sessionService.getSessionsByCoach(ud.getUsername()));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<CoachSessionDto.Response>> getUpcoming(
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(sessionService.getUpcomingSessions(ud.getUsername()));
    }
}
