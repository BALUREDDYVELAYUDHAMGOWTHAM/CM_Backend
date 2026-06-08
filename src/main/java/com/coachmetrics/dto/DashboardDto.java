package com.coachmetrics.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DashboardDto {
    // Admin stats
    private long totalMentors;
    private long activeCoaches;
    private long sdetCount;
    private long dotnetCount;
    private long totalSessions;
    private double totalHours;
    // Coach stats
    private long myMentors;
    private long sessionsThisMonth;
    private long upcomingSessions;
    private long cohortsAssigned;
    // Charts
    private Map<String, Long> deptDistribution;
    private List<ActivityLogDto> recentActivity;
    public DashboardDto() {}
    public long   getTotalMentors()      { return totalMentors; }
    public long   getActiveCoaches()     { return activeCoaches; }
    public long   getSdetCount()         { return sdetCount; }
    public long   getDotnetCount()       { return dotnetCount; }
    public long   getTotalSessions()     { return totalSessions; }
    public double getTotalHours()        { return totalHours; }
    public long   getMyMentors()         { return myMentors; }
    public long   getSessionsThisMonth() { return sessionsThisMonth; }
    public long   getUpcomingSessions()  { return upcomingSessions; }
    public long   getCohortsAssigned()   { return cohortsAssigned; }
    public Map<String, Long> getDeptDistribution() { return deptDistribution; }
    public List<ActivityLogDto> getRecentActivity() { return recentActivity; }
    public void setTotalMentors(long totalMentors)           { this.totalMentors      = totalMentors; }
    public void setActiveCoaches(long activeCoaches)         { this.activeCoaches     = activeCoaches; }
    public void setSdetCount(long sdetCount)                 { this.sdetCount         = sdetCount; }
    public void setDotnetCount(long dotnetCount)             { this.dotnetCount       = dotnetCount; }
    public void setTotalSessions(long totalSessions)         { this.totalSessions     = totalSessions; }
    public void setTotalHours(double totalHours)             { this.totalHours        = totalHours; }
    public void setMyMentors(long myMentors)                 { this.myMentors         = myMentors; }
    public void setSessionsThisMonth(long sessionsThisMonth) { this.sessionsThisMonth = sessionsThisMonth; }
    public void setUpcomingSessions(long upcomingSessions)   { this.upcomingSessions  = upcomingSessions; }
    public void setCohortsAssigned(long cohortsAssigned)     { this.cohortsAssigned   = cohortsAssigned; }
    public void setDeptDistribution(Map<String, Long> deptDistribution) { this.deptDistribution = deptDistribution; }
    public void setRecentActivity(List<ActivityLogDto> recentActivity)  { this.recentActivity   = recentActivity; }
}
