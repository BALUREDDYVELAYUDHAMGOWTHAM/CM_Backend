package com.coachmetrics.entity;

import com.coachmetrics.enums.MentorConnectMode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents one weekly mentor-connect entry per the Excel tracker columns:
 * Cohort code | Batch owner | Mentor | Week range | Happened? | Mode | Date | Hours | Reason
 */
@Entity
@Table(name = "mentor_connects")
public class MentorConnect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    // Week identifier e.g. "19 Jan - 23 Jan", "27 Jan - 30 Jan"
    @Column(nullable = false)
    private String weekRange;

    // Week sequence number (1-14)
    private Integer weekNumber;

    // Did the connect happen?
    private boolean happened = false;

    @Enumerated(EnumType.STRING)
    private MentorConnectMode mode;

    private LocalDate connectDate;

    private Double hours;

    // Reason if virtual / not happened
    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    public MentorConnect() {}

    @PrePersist
    void onCreate() { this.createdAt = LocalDateTime.now(); }

    public Long              getId()          { return id; }
    public Mentor            getMentor()      { return mentor; }
    public String            getWeekRange()   { return weekRange; }
    public Integer           getWeekNumber()  { return weekNumber; }
    public boolean           isHappened()     { return happened; }
    public MentorConnectMode getMode()        { return mode; }
    public LocalDate         getConnectDate() { return connectDate; }
    public Double            getHours()       { return hours; }
    public String            getReason()      { return reason; }
    public LocalDateTime     getCreatedAt()   { return createdAt; }

    public void setId(Long id)                          { this.id          = id; }
    public void setMentor(Mentor mentor)                { this.mentor      = mentor; }
    public void setWeekRange(String weekRange)          { this.weekRange   = weekRange; }
    public void setWeekNumber(Integer weekNumber)       { this.weekNumber  = weekNumber; }
    public void setHappened(boolean happened)           { this.happened    = happened; }
    public void setMode(MentorConnectMode mode)         { this.mode        = mode; }
    public void setConnectDate(LocalDate connectDate)   { this.connectDate = connectDate; }
    public void setHours(Double hours)                  { this.hours       = hours; }
    public void setReason(String reason)                { this.reason      = reason; }
    public void setCreatedAt(LocalDateTime createdAt)   { this.createdAt   = createdAt; }
}
