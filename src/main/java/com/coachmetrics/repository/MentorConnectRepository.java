package com.coachmetrics.repository;

import com.coachmetrics.entity.MentorConnect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MentorConnectRepository extends JpaRepository<MentorConnect, Long> {
    List<MentorConnect> findByMentorIdOrderByWeekNumberAsc(Long mentorId);

    @Query("SELECT SUM(mc.hours) FROM MentorConnect mc WHERE mc.mentor.coach.id = :coachId AND mc.happened = true")
    Double sumHoursByCoachId(@Param("coachId") Long coachId);

    @Query("SELECT COUNT(mc) FROM MentorConnect mc WHERE mc.mentor.coach.id = :coachId AND mc.happened = true")
    long countSessionsByCoachId(@Param("coachId") Long coachId);

    @Query("SELECT COUNT(mc) FROM MentorConnect mc WHERE mc.happened = true")
    long countAllSessions();

    @Query("SELECT COALESCE(SUM(mc.hours), 0) FROM MentorConnect mc WHERE mc.happened = true")
    Double sumAllHours();
}
