package com.coachmetrics.service;

import com.coachmetrics.dto.DashboardDto;
import com.coachmetrics.dto.ReportDto;
import com.coachmetrics.entity.Mentor;
import com.coachmetrics.entity.User;
import com.coachmetrics.enums.UserRole;
import com.coachmetrics.exception.ResourceNotFoundException;
import com.coachmetrics.repository.CoachSessionRepository;
import com.coachmetrics.repository.MentorConnectRepository;
import com.coachmetrics.repository.MentorRepository;
import com.coachmetrics.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private MentorRepository mentorRepo;

    @Autowired
    private MentorConnectRepository connectRepo;

    @Autowired
    private CoachSessionRepository sessionRepo;

    @Autowired
    private ActivityLogService logService;

    public DashboardDto getAdminDashboard() {
        DashboardDto dto = new DashboardDto();
        dto.setTotalMentors(mentorRepo.count());
        dto.setActiveCoaches(userRepo.countByRoleAndActive(UserRole.COACH, true));

        // Count by String department
        long sdetCount   = mentorRepo.findAll().stream().filter(m -> "SDET".equals(m.getDepartment())).count();
        long dotnetCount = mentorRepo.findAll().stream().filter(m -> "DOTNET".equals(m.getDepartment())).count();
        dto.setSdetCount(sdetCount);
        dto.setDotnetCount(dotnetCount);

        dto.setTotalSessions(connectRepo.countAllSessions());
        Double hours = connectRepo.sumAllHours();
        dto.setTotalHours(hours != null ? hours : 0);

        Map<String, Long> dist = new LinkedHashMap<>();
        dist.put("SDET",    sdetCount);
        dist.put(".NET/C#", dotnetCount);
        dto.setDeptDistribution(dist);
        dto.setRecentActivity(logService.getRecent(10));
        return dto;
    }

    public DashboardDto getCoachDashboard(String coachEmail) {
        User coach = userRepo.findByEmail(coachEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Coach not found"));

        DashboardDto dto = new DashboardDto();
        dto.setMyMentors(mentorRepo.countByCoach(coach));

        // Sessions this month from CoachSession table
        long sessions = connectRepo.countSessionsByCoachId(coach.getId());
        dto.setSessionsThisMonth(sessions);

        // Upcoming sessions count
        long upcoming = sessionRepo.findUpcoming(coach.getId(), LocalDate.now()).size();
        dto.setUpcomingSessions(upcoming);

        List<String> cohorts = mentorRepo.findDistinctCohortsByCoach(coach);
        dto.setCohortsAssigned((long) cohorts.size());
        dto.setRecentActivity(logService.getRecent(8));
        return dto;
    }

    public List<ReportDto> getReport(String dept, String from, String to) {
        List<Mentor> mentors;

        // Filter by department string (not enum)
        if (dept != null && !dept.isEmpty()) {
            mentors = mentorRepo.findAll().stream()
                    .filter(m -> dept.equals(m.getDepartment()))
                    .collect(java.util.stream.Collectors.toList());
        } else {
            mentors = mentorRepo.findAll();
        }

        // Date range filtering using CoachSession data
        LocalDate fromDate = (from != null && !from.isEmpty()) ? LocalDate.parse(from) : LocalDate.now().minusMonths(1);
        LocalDate toDate   = (to   != null && !to.isEmpty())   ? LocalDate.parse(to)   : LocalDate.now();

        List<ReportDto> result = new ArrayList<>();
        for (Mentor m : mentors) {
            // Get sessions for this mentor in the date range
            List<com.coachmetrics.entity.CoachSession> mSessions =
                    sessionRepo.findByMentorIdOrderBySessionDateAsc(m.getId())
                            .stream()
                            .filter(s -> !s.getSessionDate().isBefore(fromDate) && !s.getSessionDate().isAfter(toDate))
                            .collect(java.util.stream.Collectors.toList());

            int totalSessions = mSessions.size();
            double totalHrs   = mSessions.stream().mapToDouble(s -> s.getHours() != null ? s.getHours() : 0).sum();

            ReportDto r = new ReportDto();
            r.setMentorName(m.getFullName());
            r.setDepartment(m.getDepartment() != null ? m.getDepartment() : "");
            r.setCohortCode(m.getCohortCode());
            r.setCoachName(m.getCoach().getFullName());
            r.setTotalSessions(totalSessions);
            r.setTotalHours(totalHrs);
            r.setAvgDuration(totalSessions > 0 ? totalHrs / totalSessions : 0);
            result.add(r);
        }
        return result;
    }
}
