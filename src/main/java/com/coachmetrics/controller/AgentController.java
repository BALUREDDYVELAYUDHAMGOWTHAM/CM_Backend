package com.coachmetrics.controller;

import com.coachmetrics.dto.AgentDto;
import com.coachmetrics.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coach/agent")
public class AgentController {

    @Autowired
    private AgentService agentService;

    /**
     * POST /api/coach/agent/parse
     * Body: { "text": "Had a virtual session with Arun Kumar today 10am to 11:30am" }
     * Returns: parsed fields + confirmation message
     */
    @PostMapping("/parse")
    public ResponseEntity<AgentDto.Response> parse(
            @RequestBody AgentDto.Request req,
            @AuthenticationPrincipal UserDetails ud) {
        req.setAutoSave(false); // parse only — user confirms before save
        return ResponseEntity.ok(agentService.process(req, ud.getUsername()));
    }

    /**
     * POST /api/coach/agent/save
     * Body: { "text": "...", "autoSave": true }
     * Parses AND saves the session
     */
    @PostMapping("/save")
    public ResponseEntity<AgentDto.Response> save(
            @RequestBody AgentDto.Request req,
            @AuthenticationPrincipal UserDetails ud) {
        req.setAutoSave(true);
        return ResponseEntity.ok(agentService.process(req, ud.getUsername()));
    }
}
