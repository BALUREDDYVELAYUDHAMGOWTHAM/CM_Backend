package com.coachmetrics.repository;

import com.coachmetrics.entity.User;
import com.coachmetrics.enums.Department;
import com.coachmetrics.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(UserRole role);
    List<User> findByRoleAndActive(UserRole role, boolean active);
    long countByRole(UserRole role);
    long countByRoleAndActive(UserRole role, boolean active);
}
