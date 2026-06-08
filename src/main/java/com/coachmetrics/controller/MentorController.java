package com.coachmetrics.controller;

import com.coachmetrics.dto.MentorConnectDto;
import com.coachmetrics.dto.MentorDto;
import com.coachmetrics.service.MentorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class MentorController {

    @Autowired private MentorService mentorService;

    @GetMapping("/coach/mentors")
    public ResponseEntity<List<MentorDto.Response>> getMyMentors(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam(required=false) String name,
            @RequestParam(required=false) String dept,
            @RequestParam(required=false) String cohort) {
        return ResponseEntity.ok(mentorService.getMentorsByCoach(ud.getUsername(), name, dept, cohort));
    }

    @GetMapping("/coach/mentors/{id}")
    public ResponseEntity<MentorDto.Response> getMentor(@PathVariable Long id) {
        return ResponseEntity.ok(mentorService.getMentorById(id));
    }

    // Autofill by associateId
    @GetMapping("/coach/mentors/associate/{associateId}")
    public ResponseEntity<MentorDto.Response> getByAssociateId(@PathVariable String associateId) {
        return ResponseEntity.ok(mentorService.findByAssociateId(associateId));
    }

    @PostMapping("/coach/mentors")
    public ResponseEntity<MentorDto.Response> createMentor(
            @RequestBody MentorDto.Request req,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mentorService.createMentor(req, ud.getUsername()));
    }

    @PutMapping("/coach/mentors/{id}")
    public ResponseEntity<MentorDto.Response> updateMentor(
            @PathVariable Long id,
            @RequestBody MentorDto.Request req,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(mentorService.updateMentor(id, req, ud.getUsername()));
    }

    @DeleteMapping("/coach/mentors/{id}")
    public ResponseEntity<Void> deleteMentor(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails ud) {
        mentorService.deleteMentor(id, ud.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/coach/connects/{connectId}")
    public ResponseEntity<MentorConnectDto.Response> updateConnect(
            @PathVariable Long connectId,
            @RequestBody MentorConnectDto.Request req) {
        return ResponseEntity.ok(mentorService.updateConnect(connectId, req));
    }

    @GetMapping("/admin/mentors")
    public ResponseEntity<List<MentorDto.Response>> getAllMentors(
            @RequestParam(required=false) String name,
            @RequestParam(required=false) String dept) {
        return ResponseEntity.ok(mentorService.getAllMentors(name, dept));
    }
}
