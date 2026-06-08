package com.coachmetrics.repository;
import com.coachmetrics.entity.Mentor;
import com.coachmetrics.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface MentorRepository extends JpaRepository<Mentor, Long> {
    boolean existsByEmail(String email);
    List<Mentor> findByCoach(User coach);
    Optional<Mentor> findByAssociateId(String associateId);
    long countByCoach(User coach);

    @Query("SELECT DISTINCT m.cohortCode FROM Mentor m WHERE m.coach = :coach AND m.cohortCode IS NOT NULL")
    List<String> findDistinctCohortsByCoach(@Param("coach") User coach);
}
