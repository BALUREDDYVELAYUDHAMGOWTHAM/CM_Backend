package com.coachmetrics.repository;
import com.coachmetrics.entity.CoachSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
public interface CoachSessionRepository extends JpaRepository<CoachSession, Long> {
    List<CoachSession> findByMentorIdOrderBySessionDateAsc(Long mentorId);
    List<CoachSession> findByCoachIdOrderBySessionDateDesc(Long coachId);

    // Upcoming sessions: date >= today, ordered by date
    @Query("SELECT cs FROM CoachSession cs WHERE cs.coach.id = :coachId AND cs.sessionDate >= :today ORDER BY cs.sessionDate ASC")
    List<CoachSession> findUpcoming(@Param("coachId") Long coachId, @Param("today") LocalDate today);

    // Monthly hours for a mentor
    @Query("SELECT COALESCE(SUM(cs.hours),0) FROM CoachSession cs WHERE cs.mentor.id = :mentorId " +
           "AND YEAR(cs.sessionDate) = :year AND MONTH(cs.sessionDate) = :month")
    Double sumHoursByMentorMonthYear(@Param("mentorId") Long mentorId, @Param("year") int year, @Param("month") int month);

    // Filter by date range for reports
    @Query("SELECT cs FROM CoachSession cs WHERE cs.sessionDate BETWEEN :from AND :to ORDER BY cs.sessionDate DESC")
    List<CoachSession> findByDateRange(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT cs FROM CoachSession cs WHERE cs.coach.id = :coachId AND cs.sessionDate BETWEEN :from AND :to")
    List<CoachSession> findByCoachAndDateRange(@Param("coachId") Long coachId, @Param("from") LocalDate from, @Param("to") LocalDate to);
}
