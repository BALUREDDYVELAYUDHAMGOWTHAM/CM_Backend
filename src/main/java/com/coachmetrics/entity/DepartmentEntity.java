package com.coachmetrics.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "departments")
public class DepartmentEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    private boolean active = true;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    public DepartmentEntity() {}
    public DepartmentEntity(String name, String code) { this.name = name; this.code = code; }

    @PrePersist void onCreate() { this.createdAt = LocalDateTime.now(); }

    public Long getId()           { return id; }
    public String getName()       { return name; }
    public String getCode()       { return code; }
    public boolean isActive()     { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setId(Long id)          { this.id = id; }
    public void setName(String name)    { this.name = name; }
    public void setCode(String code)    { this.code = code; }
    public void setActive(boolean active){ this.active = active; }
    public void setCreatedAt(LocalDateTime c){ this.createdAt = c; }
}
