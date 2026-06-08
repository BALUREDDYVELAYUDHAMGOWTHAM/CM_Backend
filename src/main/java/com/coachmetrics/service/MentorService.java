package com.coachmetrics.service;

import com.coachmetrics.dto.MentorConnectDto;
import com.coachmetrics.dto.MentorDto;
import com.coachmetrics.entity.Mentor;
import com.coachmetrics.entity.MentorConnect;
import com.coachmetrics.entity.User;
import com.coachmetrics.enums.MentorConnectMode;
import com.coachmetrics.enums.TrainingStatus;
import com.coachmetrics.exception.ResourceNotFoundException;
import com.coachmetrics.repository.MentorConnectRepository;
import com.coachmetrics.repository.MentorRepository;
import com.coachmetrics.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class MentorService {

    @Autowired private MentorRepository mentorRepo;
    @Autowired private MentorConnectRepository connectRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private ActivityLogService logService;

    private static final String[] WEEK_RANGES = {
        "19 Jan - 23 Jan","27 Jan - 30 Jan","2 Feb - 6 Feb",
        "9 Feb - 13 Feb","16 Feb - 20 Feb","23 Feb - 27 Feb",
        "2 Mar - 6 Mar","9 Mar - 13 Mar","16 Mar - 19 Mar",
        "23 Mar - 27 Mar","30 Mar - 03 Apr","06 Apr - 10 Apr",
        "13 Apr - 17 Apr","20 Apr - 24 Apr"
    };

    public MentorDto.Response createMentor(MentorDto.Request req, String coachEmail) {
        if (mentorRepo.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email already registered: " + req.getEmail());
        User coach = userRepo.findByEmail(coachEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Coach not found"));

        Mentor mentor = new Mentor();
        mentor.setFullName(req.getFullName());
        mentor.setEmail(req.getEmail());
        mentor.setAssociateId(req.getAssociateId());
        mentor.setDepartment(req.getDepartment());
        mentor.setCohortCode(req.getCohortCode());
        mentor.setContact(req.getContact());
        mentor.setVerticalMapping(req.getVerticalMapping());
        mentor.setNotes(req.getNotes());
        mentor.setTrainingStatus(req.getTrainingStatus() != null
                ? TrainingStatus.valueOf(req.getTrainingStatus()) : TrainingStatus.ACTIVE);
        mentor.setCoach(coach);
        mentor = mentorRepo.save(mentor);

        // Auto-create 14 weekly connect slots
        for (int i = 0; i < WEEK_RANGES.length; i++) {
            MentorConnect mc = new MentorConnect();
            mc.setMentor(mentor); mc.setWeekRange(WEEK_RANGES[i]); mc.setWeekNumber(i + 1); mc.setHappened(false);
            connectRepo.save(mc);
        }
        logService.log("MENTOR_ADDED", "Mentor added: " + mentor.getFullName(), coachEmail, mentor.getFullName());
        return toResponse(mentor);
    }

    public MentorDto.Response updateMentor(Long id, MentorDto.Request req, String coachEmail) {
        Mentor mentor = findById(id);
        mentor.setFullName(req.getFullName());
        mentor.setEmail(req.getEmail());
        mentor.setAssociateId(req.getAssociateId());
        mentor.setDepartment(req.getDepartment());
        mentor.setCohortCode(req.getCohortCode());
        mentor.setContact(req.getContact());
        mentor.setVerticalMapping(req.getVerticalMapping());
        mentor.setNotes(req.getNotes());
        if (req.getTrainingStatus() != null)
            mentor.setTrainingStatus(TrainingStatus.valueOf(req.getTrainingStatus()));
        mentor = mentorRepo.save(mentor);
        logService.log("MENTOR_UPDATED", "Mentor updated: " + mentor.getFullName(), coachEmail, mentor.getFullName());
        return toResponse(mentor);
    }

    public void deleteMentor(Long id, String coachEmail) {
        Mentor mentor = findById(id);
        logService.log("MENTOR_DELETED", "Mentor deleted: " + mentor.getFullName(), coachEmail, mentor.getFullName());
        mentorRepo.delete(mentor);
    }

    @Transactional(readOnly = true)
    public List<MentorDto.Response> getMentorsByCoach(String coachEmail, String name, String dept, String cohort) {
        User coach = userRepo.findByEmail(coachEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Coach not found"));
        List<Mentor> mentors = mentorRepo.findByCoach(coach);
        List<MentorDto.Response> result = new ArrayList<>();
        for (Mentor m : mentors) {
            if (name != null && !name.isEmpty() && !m.getFullName().toLowerCase().contains(name.toLowerCase())) continue;
            if (dept != null && !dept.isEmpty() && !dept.equals(m.getDepartment())) continue;
            if (cohort != null && !cohort.isEmpty() && !cohort.equals(m.getCohortCode())) continue;
            result.add(toResponse(m));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<MentorDto.Response> getAllMentors(String name, String dept) {
        List<Mentor> mentors = mentorRepo.findAll();
        List<MentorDto.Response> result = new ArrayList<>();
        for (Mentor m : mentors) {
            if (name != null && !name.isEmpty() && !m.getFullName().toLowerCase().contains(name.toLowerCase())) continue;
            if (dept != null && !dept.isEmpty() && !dept.equals(m.getDepartment())) continue;
            result.add(toResponse(m));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public MentorDto.Response getMentorById(Long id) { return toResponse(findById(id)); }

    // Find by associateId for autofill
    @Transactional(readOnly = true)
    public MentorDto.Response findByAssociateId(String associateId) {
        Mentor m = mentorRepo.findByAssociateId(associateId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found: " + associateId));
        return toResponse(m);
    }

    public MentorConnectDto.Response updateConnect(Long connectId, MentorConnectDto.Request req) {
        MentorConnect mc = connectRepo.findById(connectId)
                .orElseThrow(() -> new ResourceNotFoundException("Connect entry not found"));
        mc.setHappened(req.isHappened());
        if (req.getMode() != null) mc.setMode(MentorConnectMode.valueOf(req.getMode()));
        mc.setConnectDate(req.getConnectDate());
        mc.setHours(req.getHours());
        mc.setReason(req.getReason());
        mc = connectRepo.save(mc);
        logService.log("CONNECT_UPDATED",
                "Week " + mc.getWeekNumber() + " updated for " + mc.getMentor().getFullName(),
                mc.getMentor().getCoach().getEmail(), mc.getMentor().getFullName());
        return toConnectResponse(mc);
    }

    public Mentor findById(Long id) {
        return mentorRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found: " + id));
    }

    public MentorDto.Response toResponse(Mentor m) {
        List<MentorConnect> connects = connectRepo.findByMentorIdOrderByWeekNumberAsc(m.getId());
        int sessions = 0; double hours = 0;
        List<MentorConnectDto.Response> connectDtos = new ArrayList<>();
        for (MentorConnect mc : connects) {
            if (mc.isHappened()) { sessions++; hours += mc.getHours() != null ? mc.getHours() : 0; }
            connectDtos.add(toConnectResponse(mc));
        }
        MentorDto.Response dto = new MentorDto.Response();
        dto.setId(m.getId());
        dto.setFullName(m.getFullName());
        dto.setEmail(m.getEmail());
        dto.setAssociateId(m.getAssociateId());
        dto.setDepartment(m.getDepartment());
        dto.setCohortCode(m.getCohortCode());
        dto.setContact(m.getContact());
        dto.setVerticalMapping(m.getVerticalMapping());
        dto.setNotes(m.getNotes());
        dto.setTrainingStatus(m.getTrainingStatus().name());
        dto.setCoachName(m.getCoach().getFullName());
        dto.setCoachEmail(m.getCoach().getEmail());
        dto.setCoachId(m.getCoach().getId());
        dto.setTotalSessions(sessions);
        dto.setTotalHours(hours);
        dto.setConnects(connectDtos);
        dto.setCreatedAt(m.getCreatedAt());
        return dto;
    }

    private MentorConnectDto.Response toConnectResponse(MentorConnect mc) {
        MentorConnectDto.Response dto = new MentorConnectDto.Response();
        dto.setId(mc.getId()); dto.setMentorId(mc.getMentor().getId());
        dto.setMentorName(mc.getMentor().getFullName()); dto.setWeekRange(mc.getWeekRange());
        dto.setWeekNumber(mc.getWeekNumber()); dto.setHappened(mc.isHappened());
        dto.setMode(mc.getMode() != null ? mc.getMode().name() : null);
        dto.setConnectDate(mc.getConnectDate()); dto.setHours(mc.getHours()); dto.setReason(mc.getReason());
        return dto;
    }
}
