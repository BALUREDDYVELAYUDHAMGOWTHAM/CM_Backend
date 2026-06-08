package com.coachmetrics.entity;

import com.coachmetrics.enums.TrainingStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mentors")
public class Mentor {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    // NEW: Associate ID
    @Column(unique = true)
    private String associateId;

    private String department;
    private String cohortCode;
    private String contact;
    private String verticalMapping;
    private String notes;

    @Enumerated(EnumType.STRING)
    private TrainingStatus trainingStatus = TrainingStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id", nullable = false)
    private User coach;

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MentorConnect> mentorConnects = new ArrayList<>();

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CoachSession> coachSessions = new ArrayList<>();

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Mentor() {}

    @PrePersist void onCreate() { this.createdAt = LocalDateTime.now(); this.updatedAt = LocalDateTime.now(); }
    @PreUpdate  void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public Long           getId()             { return id; }
    public String         getFullName()       { return fullName; }
    public String         getEmail()          { return email; }
    public String         getAssociateId()    { return associateId; }
    public String         getDepartment()     { return department; }
    public String         getCohortCode()     { return cohortCode; }
    public String         getContact()        { return contact; }
    public String         getVerticalMapping(){ return verticalMapping; }
    public String         getNotes()          { return notes; }
    public TrainingStatus getTrainingStatus() { return trainingStatus; }
    public User           getCoach()          { return coach; }
    public List<MentorConnect>  getMentorConnects() { return mentorConnects; }
    public List<CoachSession>   getCoachSessions()  { return coachSessions; }
    public LocalDateTime  getCreatedAt()      { return createdAt; }
    public LocalDateTime  getUpdatedAt()      { return updatedAt; }

    public void setId(Long id)                            { this.id              = id; }
    public void setFullName(String v)                     { this.fullName        = v; }
    public void setEmail(String v)                        { this.email           = v; }
    public void setAssociateId(String v)                  { this.associateId     = v; }
    public void setDepartment(String v)                   { this.department      = v; }
    public void setCohortCode(String v)                   { this.cohortCode      = v; }
    public void setContact(String v)                      { this.contact         = v; }
    public void setVerticalMapping(String v)              { this.verticalMapping = v; }
    public void setNotes(String v)                        { this.notes           = v; }
    public void setTrainingStatus(TrainingStatus v)       { this.trainingStatus  = v; }
    public void setCoach(User v)                          { this.coach           = v; }
    public void setMentorConnects(List<MentorConnect> v)  { this.mentorConnects  = v; }
    public void setCoachSessions(List<CoachSession> v)    { this.coachSessions   = v; }
    public void setCreatedAt(LocalDateTime v)             { this.createdAt       = v; }
    public void setUpdatedAt(LocalDateTime v)             { this.updatedAt       = v; }
}
