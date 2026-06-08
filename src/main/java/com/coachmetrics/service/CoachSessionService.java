package com.coachmetrics.service;

import com.coachmetrics.dto.CoachSessionDto;
import com.coachmetrics.entity.CoachSession;
import com.coachmetrics.entity.Mentor;
import com.coachmetrics.entity.User;
import com.coachmetrics.exception.ResourceNotFoundException;
import com.coachmetrics.repository.CoachSessionRepository;
import com.coachmetrics.repository.MentorRepository;
import com.coachmetrics.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CoachSessionService {

    @Autowired
    private CoachSessionRepository sessionRepo;

    @Autowired
    private MentorRepository mentorRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ActivityLogService logService;

    public CoachSessionDto.Response createSession(CoachSessionDto.Request req, String coachEmail) {
        Mentor mentor = mentorRepo.findById(req.getMentorId())
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found"));
        User coach = userRepo.findByEmail(coachEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Coach not found"));

        CoachSession session = new CoachSession();
        session.setMentor(mentor);
        session.setCoach(coach);
        session.setBatchOwnerId(req.getBatchOwnerId());
        session.setBatchOwnerName(req.getBatchOwnerName());
        session.setSessionDate(req.getSessionDate());
        session.setNotes(req.getNotes());

        // Parse times and auto-calculate hours
        if (req.getFromTime() != null && req.getToTime() != null) {
            LocalTime from = LocalTime.parse(req.getFromTime());
            LocalTime to   = LocalTime.parse(req.getToTime());
            session.setFromTime(from);
            session.setToTime(to);
            long minutes = ChronoUnit.MINUTES.between(from, to);
            session.setHours(Math.round(minutes / 60.0 * 2) / 2.0); // round to 0.5
        }

        session = sessionRepo.save(session);
        logService.log("SESSION_ADDED",
                "Session added for mentor: " + mentor.getFullName() + " on " + req.getSessionDate(),
                coachEmail, mentor.getFullName());
        return toResponse(session);
    }

    @Transactional(readOnly = true)
    public List<CoachSessionDto.Response> getUpcomingSessions(String coachEmail) {
        User coach = userRepo.findByEmail(coachEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Coach not found"));
        List<CoachSession> sessions = sessionRepo.findUpcoming(coach.getId(), LocalDate.now());
        List<CoachSessionDto.Response> result = new ArrayList<>();
        // top 5 only
        int limit = Math.min(5, sessions.size());
        for (int i = 0; i < limit; i++) result.add(toResponse(sessions.get(i)));
        return result;
    }

    @Transactional(readOnly = true)
    public List<CoachSessionDto.Response> getSessionsByCoach(String coachEmail) {
        User coach = userRepo.findByEmail(coachEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Coach not found"));
        List<CoachSession> sessions = sessionRepo.findByCoachIdOrderBySessionDateDesc(coach.getId());
        List<CoachSessionDto.Response> result = new ArrayList<>();
        for (CoachSession s : sessions) result.add(toResponse(s));
        return result;
    }

    @Transactional(readOnly = true)
    public Double getMonthlyHoursForMentor(Long mentorId, int year, int month) {
        Double h = sessionRepo.sumHoursByMentorMonthYear(mentorId, year, month);
        return h != null ? h : 0.0;
    }

    // For reports: filter by date range
    @Transactional(readOnly = true)
    public List<CoachSessionDto.Response> getSessionsByDateRange(LocalDate from, LocalDate to) {
        List<CoachSession> sessions = sessionRepo.findByDateRange(from, to);
        List<CoachSessionDto.Response> result = new ArrayList<>();
        for (CoachSession s : sessions) result.add(toResponse(s));
        return result;
    }

    public CoachSessionDto.Response toResponse(CoachSession s) {
        CoachSessionDto.Response dto = new CoachSessionDto.Response();
        dto.setId(s.getId());
        dto.setMentorId(s.getMentor().getId());
        dto.setMentorName(s.getMentor().getFullName());
        dto.setAssociateId(s.getMentor().getAssociateId());
        dto.setMentorEmail(s.getMentor().getEmail());
        dto.setDepartment(s.getMentor().getDepartment());
        dto.setCohortCode(s.getMentor().getCohortCode());
        dto.setContact(s.getMentor().getContact());
        dto.setVerticalMapping(s.getMentor().getVerticalMapping());
        dto.setTrainingStatus(s.getMentor().getTrainingStatus().name());
        dto.setCoachName(s.getCoach().getFullName());
        dto.setBatchOwnerId(s.getBatchOwnerId());
        dto.setBatchOwnerName(s.getBatchOwnerName());
        dto.setSessionDate(s.getSessionDate());
        dto.setFromTime(s.getFromTime() != null ? s.getFromTime().toString() : null);
        dto.setToTime(s.getToTime()   != null ? s.getToTime().toString()   : null);
        dto.setHours(s.getHours());
        dto.setNotes(s.getNotes());
        dto.setCreatedAt(s.getCreatedAt());
        return dto;
    }
}
