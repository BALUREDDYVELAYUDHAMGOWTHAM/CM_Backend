package com.coachmetrics.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ReportDto {
    private String mentorName;
    private String department;
    private String cohortCode;
    private String coachName;
    private int    totalSessions;
    private double totalHours;
    private double avgDuration;
    public ReportDto() {}
    public String getMentorName()    { return mentorName; }
    public String getDepartment()    { return department; }
    public String getCohortCode()    { return cohortCode; }
    public String getCoachName()     { return coachName; }
    public int    getTotalSessions() { return totalSessions; }
    public double getTotalHours()    { return totalHours; }
    public double getAvgDuration()   { return avgDuration; }
    public void setMentorName(String mentorName)       { this.mentorName    = mentorName; }
    public void setDepartment(String department)       { this.department    = department; }
    public void setCohortCode(String cohortCode)       { this.cohortCode    = cohortCode; }
    public void setCoachName(String coachName)         { this.coachName     = coachName; }
    public void setTotalSessions(int totalSessions)    { this.totalSessions = totalSessions; }
    public void setTotalHours(double totalHours)       { this.totalHours    = totalHours; }
    public void setAvgDuration(double avgDuration)     { this.avgDuration   = avgDuration; }
}
