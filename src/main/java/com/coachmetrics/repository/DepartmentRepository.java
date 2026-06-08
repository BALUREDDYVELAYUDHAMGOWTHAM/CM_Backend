package com.coachmetrics.repository;
import com.coachmetrics.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long> {
    List<DepartmentEntity> findByActiveTrue();
    boolean existsByCode(String code);
}
