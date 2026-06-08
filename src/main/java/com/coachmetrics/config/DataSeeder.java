package com.coachmetrics.config;

import com.coachmetrics.entity.*;
import com.coachmetrics.enums.*;
import com.coachmetrics.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired private UserRepository userRepo;
    @Autowired private MentorRepository mentorRepo;
    @Autowired private MentorConnectRepository connectRepo;
    @Autowired private CoachSessionRepository sessionRepo;
    @Autowired private DepartmentRepository deptRepo;
    @Autowired private VerticalRepository verticalRepo;
    @Autowired private PasswordEncoder encoder;

    private static final String[] WEEK_RANGES = {
        "19 Jan - 23 Jan","27 Jan - 30 Jan","2 Feb - 6 Feb",
        "9 Feb - 13 Feb","16 Feb - 20 Feb","23 Feb - 27 Feb",
        "2 Mar - 6 Mar","9 Mar - 13 Mar","16 Mar - 19 Mar",
        "23 Mar - 27 Mar","30 Mar - 03 Apr","06 Apr - 10 Apr",
        "13 Apr - 17 Apr","20 Apr - 24 Apr"
    };

    @Override
    public void run(String... args) {
        if (userRepo.count() > 0) return;
        System.out.println("Seeding Coach Metrics v2 demo data...");

        // Seed departments
        DepartmentEntity sdet   = deptRepo.save(new DepartmentEntity("SDET","SDET"));
        DepartmentEntity dotnet = deptRepo.save(new DepartmentEntity(".NET/C#","DOTNET"));

        // Seed verticals
        verticalRepo.save(new VerticalEntity("SDET Track 1"));
        verticalRepo.save(new VerticalEntity("SDET Track 2"));
        verticalRepo.save(new VerticalEntity("SDET Track 3"));
        verticalRepo.save(new VerticalEntity(".NET Track 1"));
        verticalRepo.save(new VerticalEntity(".NET Track 2"));

        // Admin
        User admin = newUser("Admin User","admin@cm.com",UserRole.ADMIN,null);

        // Coaches
        User sarah = newUser("Sarah Williams","sarah@cm.com",UserRole.COACH,"SDET");
        User john  = newUser("John Davis",    "john@cm.com", UserRole.COACH,"DOTNET");
        User priya = newUser("Priya Sharma",  "priya@cm.com",UserRole.COACH,"SDET");

        // Mentors
        Mentor arun   = newMentor("Arun Kumar",   "arun@m.com",  "ASC001","SDET",  "COH-2024-A","SDET Track 1",  sarah, TrainingStatus.ACTIVE,   true);
        Mentor divya  = newMentor("Divya Nair",   "divya@m.com", "ASC002","SDET",  "COH-2024-A","SDET Track 1",  sarah, TrainingStatus.ACTIVE,   true);
        Mentor ravi   = newMentor("Ravi Patel",   "ravi@m.com",  "ASC003","SDET",  "COH-2024-B","SDET Track 2",  sarah, TrainingStatus.ON_HOLD,  false);
        Mentor meena  = newMentor("Meena Thomas", "meena@m.com", "ASC004","DOTNET","COH-2024-C",".NET Track 1",   john,  TrainingStatus.ACTIVE,   true);
        Mentor suresh = newMentor("Suresh Babu",  "suresh@m.com","ASC005","DOTNET","COH-2024-C",".NET Track 1",   john,  TrainingStatus.COMPLETED,true);
        Mentor kavya  = newMentor("Kavya Menon",  "kavya@m.com", "ASC006","DOTNET","COH-2024-D",".NET Track 2",   john,  TrainingStatus.ACTIVE,   true);
        Mentor michael= newMentor("Michael Brown","mic@m.com",   "ASC007","SDET",  "COH-2024-E","SDET Track 3",  priya, TrainingStatus.ACTIVE,   true);
        Mentor anita  = newMentor("Anita Rao",    "anita@m.com", "ASC008","SDET",  "COH-2024-E","SDET Track 3",  priya, TrainingStatus.DROPPED,  false);

        // Seed CoachSessions (some past, some upcoming)
        seedSession(arun,  sarah, "BOWN01","Batch Owner 1", LocalDate.of(2026,5,10), "09:00","10:30");
        seedSession(divya, sarah, "BOWN01","Batch Owner 1", LocalDate.of(2026,5,12), "11:00","12:00");
        seedSession(arun,  sarah, "BOWN01","Batch Owner 1", LocalDate.of(2026,5,22), "09:00","10:00"); // upcoming
        seedSession(meena, john,  "BOWN02","Batch Owner 2", LocalDate.of(2026,5,15), "14:00","15:30");
        seedSession(kavya, john,  "BOWN02","Batch Owner 2", LocalDate.of(2026,5,25), "10:00","11:00"); // upcoming

        System.out.println("Seeded. Login: admin@cm.com / sarah@cm.com / john@cm.com / priya@cm.com (password: password)");
    }

    private User newUser(String name, String email, UserRole role, String dept) {
        User u = new User();
        u.setFullName(name); u.setEmail(email);
        u.setPassword(encoder.encode("password"));
        u.setRole(role); u.setActive(true);
        if (dept != null) u.setDepartment(com.coachmetrics.enums.Department.valueOf(dept.equals("DOTNET")?"DOTNET":"SDET"));
        return userRepo.save(u);
    }

    private Mentor newMentor(String name, String email, String associateId, String dept,
                              String cohort, String vertical, User coach,
                              TrainingStatus status, boolean hasConnects) {
        Mentor m = new Mentor();
        m.setFullName(name); m.setEmail(email); m.setAssociateId(associateId);
        m.setDepartment(dept); m.setCohortCode(cohort);
        m.setVerticalMapping(vertical); m.setTrainingStatus(status); m.setCoach(coach);
        m = mentorRepo.save(m);
        for (int i = 0; i < WEEK_RANGES.length; i++) {
            MentorConnect mc = new MentorConnect();
            mc.setMentor(m); mc.setWeekRange(WEEK_RANGES[i]); mc.setWeekNumber(i+1);
            if (hasConnects && i < 6) {
                mc.setHappened(true);
                mc.setMode(i%3==0 ? MentorConnectMode.IN_PERSON : MentorConnectMode.VIRTUAL);
                mc.setConnectDate(LocalDate.of(2026,1,19).plusWeeks(i));
                mc.setHours(i%2==0 ? 1.5 : 1.0);
            } else if (hasConnects && i==6) {
                mc.setHappened(false); mc.setMode(MentorConnectMode.NOT_HAPPENED); mc.setReason("On leave");
            }
            connectRepo.save(mc);
        }
        return m;
    }

    private void seedSession(Mentor mentor, User coach, String batchId, String batchName,
                              LocalDate date, String from, String to) {
        CoachSession cs = new CoachSession();
        cs.setMentor(mentor); cs.setCoach(coach);
        cs.setBatchOwnerId(batchId); cs.setBatchOwnerName(batchName);
        cs.setSessionDate(date);
        LocalTime ft = LocalTime.parse(from), tt = LocalTime.parse(to);
        cs.setFromTime(ft); cs.setToTime(tt);
        long mins = java.time.temporal.ChronoUnit.MINUTES.between(ft, tt);
        cs.setHours(Math.round(mins / 60.0 * 2) / 2.0);
        sessionRepo.save(cs);
    }
}
