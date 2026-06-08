package com.coachmetrics.dto;

import java.time.LocalDate;

public class MentorConnectDto {

    public static class Request {
        private Long    mentorId;
        private String  weekRange;
        private Integer weekNumber;
        private boolean happened;
        private String  mode;
        private LocalDate connectDate;
        private Double  hours;
        private String  reason;
        public Request() {}
        public Long      getMentorId()    { return mentorId; }
        public String    getWeekRange()   { return weekRange; }
        public Integer   getWeekNumber()  { return weekNumber; }
        public boolean   isHappened()     { return happened; }
        public String    getMode()        { return mode; }
        public LocalDate getConnectDate() { return connectDate; }
        public Double    getHours()       { return hours; }
        public String    getReason()      { return reason; }
        public void setMentorId(Long mentorId)          { this.mentorId    = mentorId; }
        public void setWeekRange(String weekRange)      { this.weekRange   = weekRange; }
        public void setWeekNumber(Integer weekNumber)   { this.weekNumber  = weekNumber; }
        public void setHappened(boolean happened)       { this.happened    = happened; }
        public void setMode(String mode)                { this.mode        = mode; }
        public void setConnectDate(LocalDate connectDate){ this.connectDate = connectDate; }
        public void setHours(Double hours)              { this.hours       = hours; }
        public void setReason(String reason)            { this.reason      = reason; }
    }

    public static class Response {
        private Long      id;
        private Long      mentorId;
        private String    mentorName;
        private String    weekRange;
        private Integer   weekNumber;
        private boolean   happened;
        private String    mode;
        private LocalDate connectDate;
        private Double    hours;
        private String    reason;
        public Response() {}
        public Long      getId()          { return id; }
        public Long      getMentorId()    { return mentorId; }
        public String    getMentorName()  { return mentorName; }
        public String    getWeekRange()   { return weekRange; }
        public Integer   getWeekNumber()  { return weekNumber; }
        public boolean   isHappened()     { return happened; }
        public String    getMode()        { return mode; }
        public LocalDate getConnectDate() { return connectDate; }
        public Double    getHours()       { return hours; }
        public String    getReason()      { return reason; }
        public void setId(Long id)                      { this.id          = id; }
        public void setMentorId(Long mentorId)          { this.mentorId    = mentorId; }
        public void setMentorName(String mentorName)    { this.mentorName  = mentorName; }
        public void setWeekRange(String weekRange)      { this.weekRange   = weekRange; }
        public void setWeekNumber(Integer weekNumber)   { this.weekNumber  = weekNumber; }
        public void setHappened(boolean happened)       { this.happened    = happened; }
        public void setMode(String mode)                { this.mode        = mode; }
        public void setConnectDate(LocalDate connectDate){ this.connectDate = connectDate; }
        public void setHours(Double hours)              { this.hours       = hours; }
        public void setReason(String reason)            { this.reason      = reason; }
    }
}
