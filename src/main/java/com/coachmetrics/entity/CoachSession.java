package com.coachmetrics.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Stores the coach's session form data:
 * mentor, batchOwnerId, batchOwnerName, date, fromTime, toTime, hours (auto-calc)
 */
@Entity
@Table(name = "coach_sessions")
public class CoachSession {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id", nullable = false)
    private User coach;

    private String batchOwnerId;
    private String batchOwnerName;

    @Column(nullable = false)
    private LocalDate sessionDate;

    private LocalTime fromTime;
    private LocalTime toTime;

    // Auto-calculated: (toTime - fromTime) in hours
    private Double hours;

    private String notes;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CoachSession() {}

    @PrePersist  void onCreate() { this.createdAt = LocalDateTime.now(); this.updatedAt = LocalDateTime.now(); }
    @PreUpdate   void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public Long         getId()            { return id; }
    public Mentor       getMentor()        { return mentor; }
    public User         getCoach()         { return coach; }
    public String       getBatchOwnerId()  { return batchOwnerId; }
    public String       getBatchOwnerName(){ return batchOwnerName; }
    public LocalDate    getSessionDate()   { return sessionDate; }
    public LocalTime    getFromTime()      { return fromTime; }
    public LocalTime    getToTime()        { return toTime; }
    public Double       getHours()         { return hours; }
    public String       getNotes()         { return notes; }
    public LocalDateTime getCreatedAt()    { return createdAt; }
    public LocalDateTime getUpdatedAt()    { return updatedAt; }

    public void setId(Long id)                    { this.id            = id; }
    public void setMentor(Mentor mentor)          { this.mentor        = mentor; }
    public void setCoach(User coach)              { this.coach         = coach; }
    public void setBatchOwnerId(String v)         { this.batchOwnerId  = v; }
    public void setBatchOwnerName(String v)       { this.batchOwnerName= v; }
    public void setSessionDate(LocalDate v)       { this.sessionDate   = v; }
    public void setFromTime(LocalTime v)          { this.fromTime      = v; }
    public void setToTime(LocalTime v)            { this.toTime        = v; }
    public void setHours(Double v)                { this.hours         = v; }
    public void setNotes(String v)                { this.notes         = v; }
    public void setCreatedAt(LocalDateTime v)     { this.createdAt     = v; }
    public void setUpdatedAt(LocalDateTime v)     { this.updatedAt     = v; }
}
