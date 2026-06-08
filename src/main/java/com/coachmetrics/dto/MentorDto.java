package com.coachmetrics.dto;
import java.time.LocalDateTime;
import java.util.List;

public class MentorDto {

    public static class Request {
        private String fullName, email, associateId, department, cohortCode, contact, verticalMapping, notes, trainingStatus;
        public Request() {}
        public String getFullName()        { return fullName; }
        public String getEmail()           { return email; }
        public String getAssociateId()     { return associateId; }
        public String getDepartment()      { return department; }
        public String getCohortCode()      { return cohortCode; }
        public String getContact()         { return contact; }
        public String getVerticalMapping() { return verticalMapping; }
        public String getNotes()           { return notes; }
        public String getTrainingStatus()  { return trainingStatus; }
        public void setFullName(String v)       { this.fullName       = v; }
        public void setEmail(String v)          { this.email          = v; }
        public void setAssociateId(String v)    { this.associateId    = v; }
        public void setDepartment(String v)     { this.department     = v; }
        public void setCohortCode(String v)     { this.cohortCode     = v; }
        public void setContact(String v)        { this.contact        = v; }
        public void setVerticalMapping(String v){ this.verticalMapping= v; }
        public void setNotes(String v)          { this.notes          = v; }
        public void setTrainingStatus(String v) { this.trainingStatus = v; }
    }

    public static class Response {
        private Long id;
        private String fullName, email, associateId, department, cohortCode, contact, verticalMapping, notes, trainingStatus, coachName, coachEmail;
        private Long coachId;
        private int totalSessions;
        private double totalHours;
        private List<MentorConnectDto.Response> connects;
        private LocalDateTime createdAt;
        public Response() {}
        public Long   getId()             { return id; }
        public String getFullName()       { return fullName; }
        public String getEmail()          { return email; }
        public String getAssociateId()    { return associateId; }
        public String getDepartment()     { return department; }
        public String getCohortCode()     { return cohortCode; }
        public String getContact()        { return contact; }
        public String getVerticalMapping(){ return verticalMapping; }
        public String getNotes()          { return notes; }
        public String getTrainingStatus() { return trainingStatus; }
        public String getCoachName()      { return coachName; }
        public String getCoachEmail()     { return coachEmail; }
        public Long   getCoachId()        { return coachId; }
        public int    getTotalSessions()  { return totalSessions; }
        public double getTotalHours()     { return totalHours; }
        public List<MentorConnectDto.Response> getConnects() { return connects; }
        public LocalDateTime getCreatedAt(){ return createdAt; }
        public void setId(Long v)                           { this.id             = v; }
        public void setFullName(String v)                   { this.fullName       = v; }
        public void setEmail(String v)                      { this.email          = v; }
        public void setAssociateId(String v)                { this.associateId    = v; }
        public void setDepartment(String v)                 { this.department     = v; }
        public void setCohortCode(String v)                 { this.cohortCode     = v; }
        public void setContact(String v)                    { this.contact        = v; }
        public void setVerticalMapping(String v)            { this.verticalMapping= v; }
        public void setNotes(String v)                      { this.notes          = v; }
        public void setTrainingStatus(String v)             { this.trainingStatus = v; }
        public void setCoachName(String v)                  { this.coachName      = v; }
        public void setCoachEmail(String v)                 { this.coachEmail     = v; }
        public void setCoachId(Long v)                      { this.coachId        = v; }
        public void setTotalSessions(int v)                 { this.totalSessions  = v; }
        public void setTotalHours(double v)                 { this.totalHours     = v; }
        public void setConnects(List<MentorConnectDto.Response> v){ this.connects = v; }
        public void setCreatedAt(LocalDateTime v)           { this.createdAt      = v; }
    }
}
