package com.coachmetrics.repository;
import com.coachmetrics.entity.VerticalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface VerticalRepository extends JpaRepository<VerticalEntity, Long> {
    List<VerticalEntity> findByActiveTrue();
    boolean existsByName(String name);
}
