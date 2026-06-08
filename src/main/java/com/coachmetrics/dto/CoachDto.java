package com.coachmetrics.dto;

import java.time.LocalDateTime;

public class CoachDto {

    public static class Request {
        private String  fullName;
        private String  email;
        private String  department;
        private String  contact;
        private boolean active = true;
        private String  notes;
        public Request() {}
        public String  getFullName()   { return fullName; }
        public String  getEmail()      { return email; }
        public String  getDepartment() { return department; }
        public String  getContact()    { return contact; }
        public boolean isActive()      { return active; }
        public String  getNotes()      { return notes; }
        public void setFullName(String fullName)     { this.fullName   = fullName; }
        public void setEmail(String email)           { this.email      = email; }
        public void setDepartment(String department) { this.department = department; }
        public void setContact(String contact)       { this.contact    = contact; }
        public void setActive(boolean active)        { this.active     = active; }
        public void setNotes(String notes)           { this.notes      = notes; }
    }

    public static class Response {
        private Long          id;
        private String        fullName;
        private String        email;
        private String        department;
        private String        contact;
        private boolean       active;
        private int           mentorCount;
        private LocalDateTime createdAt;
        public Response() {}
        public Long          getId()          { return id; }
        public String        getFullName()    { return fullName; }
        public String        getEmail()       { return email; }
        public String        getDepartment()  { return department; }
        public String        getContact()     { return contact; }
        public boolean       isActive()       { return active; }
        public int           getMentorCount() { return mentorCount; }
        public LocalDateTime getCreatedAt()   { return createdAt; }
        public void setId(Long id)                         { this.id          = id; }
        public void setFullName(String fullName)           { this.fullName    = fullName; }
        public void setEmail(String email)                 { this.email       = email; }
        public void setDepartment(String department)       { this.department  = department; }
        public void setContact(String contact)             { this.contact     = contact; }
        public void setActive(boolean active)              { this.active      = active; }
        public void setMentorCount(int mentorCount)        { this.mentorCount = mentorCount; }
        public void setCreatedAt(LocalDateTime createdAt)  { this.createdAt   = createdAt; }
    }
}
