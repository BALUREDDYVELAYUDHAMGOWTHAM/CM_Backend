package com.coachmetrics.service;

import com.coachmetrics.dto.CoachDto;
import com.coachmetrics.entity.User;
import com.coachmetrics.enums.Department;
import com.coachmetrics.enums.UserRole;
import com.coachmetrics.exception.ResourceNotFoundException;
import com.coachmetrics.repository.MentorRepository;
import com.coachmetrics.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CoachService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private MentorRepository mentorRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private ActivityLogService logService;

    public CoachDto.Response createCoach(CoachDto.Request req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + req.getEmail());
        }
        User coach = new User();
        coach.setFullName(req.getFullName());
        coach.setEmail(req.getEmail());
        coach.setPassword(encoder.encode("password"));  // default password
        coach.setRole(UserRole.COACH);
        if (req.getDepartment() != null && !req.getDepartment().isEmpty()) {
            coach.setDepartment(Department.valueOf(req.getDepartment()));
        }
        coach.setContact(req.getContact());
        coach.setActive(req.isActive());
        coach = userRepo.save(coach);
        logService.log("COACH_ADDED", "Coach added: " + coach.getFullName(), "Admin", coach.getFullName());
        return toResponse(coach);
    }

    public CoachDto.Response updateCoach(Long id, CoachDto.Request req) {
        User coach = findById(id);
        if (!coach.getEmail().equals(req.getEmail()) && userRepo.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + req.getEmail());
        }
        coach.setFullName(req.getFullName());
        coach.setEmail(req.getEmail());
        if (req.getDepartment() != null && !req.getDepartment().isEmpty()) {
            coach.setDepartment(Department.valueOf(req.getDepartment()));
        }
        coach.setContact(req.getContact());
        coach.setActive(req.isActive());
        coach = userRepo.save(coach);
        logService.log("COACH_UPDATED", "Coach updated: " + coach.getFullName(), "Admin", coach.getFullName());
        return toResponse(coach);
    }

    public void deleteCoach(Long id) {
        User coach = findById(id);
        logService.log("COACH_DELETED", "Coach deleted: " + coach.getFullName(), "Admin", coach.getFullName());
        userRepo.delete(coach);
    }

    @Transactional(readOnly = true)
    public List<CoachDto.Response> getAllCoaches(String name, String dept, Boolean active) {
        List<User> coaches = userRepo.findByRole(UserRole.COACH);
        List<CoachDto.Response> result = new ArrayList<>();
        for (User c : coaches) {
            if (name != null && !name.isEmpty() && !c.getFullName().toLowerCase().contains(name.toLowerCase())) continue;
            if (dept != null && !dept.isEmpty() && (c.getDepartment() == null || !c.getDepartment().name().equals(dept))) continue;
            if (active != null && c.isActive() != active) continue;
            result.add(toResponse(c));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public CoachDto.Response getCoachById(Long id) { return toResponse(findById(id)); }

    public User findById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coach not found: " + id));
    }

    public CoachDto.Response toResponse(User c) {
        CoachDto.Response dto = new CoachDto.Response();
        dto.setId(c.getId());
        dto.setFullName(c.getFullName());
        dto.setEmail(c.getEmail());
        dto.setDepartment(c.getDepartment() != null ? c.getDepartment().name() : null);
        dto.setContact(c.getContact());
        dto.setActive(c.isActive());
        dto.setMentorCount((int) mentorRepo.countByCoach(c));
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }
}
