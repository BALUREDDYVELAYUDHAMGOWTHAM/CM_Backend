package com.coachmetrics.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class CoachSessionDto {

    public static class Request {
        private Long mentorId;
        private String batchOwnerId;
        private String batchOwnerName;
        private LocalDate sessionDate;
        private String fromTime; // "HH:mm"
        private String toTime;   // "HH:mm"
        private String notes;
        public Request() {}
        public Long      getMentorId()      { return mentorId; }
        public String    getBatchOwnerId()  { return batchOwnerId; }
        public String    getBatchOwnerName(){ return batchOwnerName; }
        public LocalDate getSessionDate()   { return sessionDate; }
        public String    getFromTime()      { return fromTime; }
        public String    getToTime()        { return toTime; }
        public String    getNotes()         { return notes; }
        public void setMentorId(Long v)         { this.mentorId      = v; }
        public void setBatchOwnerId(String v)   { this.batchOwnerId  = v; }
        public void setBatchOwnerName(String v) { this.batchOwnerName= v; }
        public void setSessionDate(LocalDate v) { this.sessionDate   = v; }
        public void setFromTime(String v)       { this.fromTime      = v; }
        public void setToTime(String v)         { this.toTime        = v; }
        public void setNotes(String v)          { this.notes         = v; }
    }

    public static class Response {
        private Long      id;
        private Long      mentorId;
        private String    mentorName;
        private String    associateId;
        private String    mentorEmail;
        private String    department;
        private String    cohortCode;
        private String    contact;
        private String    verticalMapping;
        private String    trainingStatus;
        private String    coachName;
        private String    batchOwnerId;
        private String    batchOwnerName;
        private LocalDate sessionDate;
        private String    fromTime;
        private String    toTime;
        private Double    hours;
        private String    notes;
        private LocalDateTime createdAt;
        public Response() {}
        public Long      getId()             { return id; }
        public Long      getMentorId()       { return mentorId; }
        public String    getMentorName()     { return mentorName; }
        public String    getAssociateId()    { return associateId; }
        public String    getMentorEmail()    { return mentorEmail; }
        public String    getDepartment()     { return department; }
        public String    getCohortCode()     { return cohortCode; }
        public String    getContact()        { return contact; }
        public String    getVerticalMapping(){ return verticalMapping; }
        public String    getTrainingStatus() { return trainingStatus; }
        public String    getCoachName()      { return coachName; }
        public String    getBatchOwnerId()   { return batchOwnerId; }
        public String    getBatchOwnerName() { return batchOwnerName; }
        public LocalDate getSessionDate()    { return sessionDate; }
        public String    getFromTime()       { return fromTime; }
        public String    getToTime()         { return toTime; }
        public Double    getHours()          { return hours; }
        public String    getNotes()          { return notes; }
        public LocalDateTime getCreatedAt()  { return createdAt; }
        public void setId(Long v)                    { this.id             = v; }
        public void setMentorId(Long v)              { this.mentorId       = v; }
        public void setMentorName(String v)          { this.mentorName     = v; }
        public void setAssociateId(String v)         { this.associateId    = v; }
        public void setMentorEmail(String v)         { this.mentorEmail    = v; }
        public void setDepartment(String v)          { this.department     = v; }
        public void setCohortCode(String v)          { this.cohortCode     = v; }
        public void setContact(String v)             { this.contact        = v; }
        public void setVerticalMapping(String v)     { this.verticalMapping= v; }
        public void setTrainingStatus(String v)      { this.trainingStatus = v; }
        public void setCoachName(String v)           { this.coachName      = v; }
        public void setBatchOwnerId(String v)        { this.batchOwnerId   = v; }
        public void setBatchOwnerName(String v)      { this.batchOwnerName = v; }
        public void setSessionDate(LocalDate v)      { this.sessionDate    = v; }
        public void setFromTime(String v)            { this.fromTime       = v; }
        public void setToTime(String v)              { this.toTime         = v; }
        public void setHours(Double v)               { this.hours          = v; }
        public void setNotes(String v)               { this.notes          = v; }
        public void setCreatedAt(LocalDateTime v)    { this.createdAt      = v; }
    }
}
