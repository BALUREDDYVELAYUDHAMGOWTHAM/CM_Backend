package com.coachmetrics.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "verticals")
public class VerticalEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private boolean active = true;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    public VerticalEntity() {}
    public VerticalEntity(String name) { this.name = name; }

    @PrePersist void onCreate() { this.createdAt = LocalDateTime.now(); }

    public Long getId()               { return id; }
    public String getName()           { return name; }
    public boolean isActive()         { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setId(Long id)          { this.id = id; }
    public void setName(String name)    { this.name = name; }
    public void setActive(boolean a)    { this.active = a; }
    public void setCreatedAt(LocalDateTime c){ this.createdAt = c; }
}
