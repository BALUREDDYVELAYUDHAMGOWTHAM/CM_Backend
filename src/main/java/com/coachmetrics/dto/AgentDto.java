package com.coachmetrics.dto;

import com.coachmetrics.service.AgentService.ParsedSession;
import java.time.LocalDate;
import java.util.List;

public class AgentDto {

    public static class Request {
        private String  text;
        private boolean autoSave = false;
        public Request() {}
        public String  getText()    { return text; }
        public boolean isAutoSave() { return autoSave; }
        public void setText(String v)      { this.text     = v; }
        public void setAutoSave(boolean v) { this.autoSave = v; }
    }

    public static class Response {
        private String       originalText;
        private String       intent;
        private ParsedSession parsed;
        private String       confirmationMessage;
        private boolean      complete;
        private boolean      saved;
        private boolean      trackerUpdate;
        private boolean      bulkUpdate;           // NEW
        private Long         savedSessionId;
        private List<String> missingFields;
        private String       error;

        // Single tracker update fields
        private String    mentorName;
        private String    weekRange;
        private Integer   weekNumber;
        private Double    hours;
        private String    mode;
        private LocalDate sessionDate;

        // Bulk update results
        private List<BulkResult> bulkResults;      // NEW

        public Response() {}

        public String        getOriginalText()        { return originalText; }
        public String        getIntent()              { return intent; }
        public ParsedSession getParsed()              { return parsed; }
        public String        getConfirmationMessage() { return confirmationMessage; }
        public boolean       isComplete()             { return complete; }
        public boolean       isSaved()                { return saved; }
        public boolean       isTrackerUpdate()        { return trackerUpdate; }
        public boolean       isBulkUpdate()           { return bulkUpdate; }
        public Long          getSavedSessionId()      { return savedSessionId; }
        public List<String>  getMissingFields()       { return missingFields; }
        public String        getError()               { return error; }
        public String        getMentorName()          { return mentorName; }
        public String        getWeekRange()           { return weekRange; }
        public Integer       getWeekNumber()          { return weekNumber; }
        public Double        getHours()               { return hours; }
        public String        getMode()                { return mode; }
        public LocalDate     getSessionDate()         { return sessionDate; }
        public List<BulkResult> getBulkResults()      { return bulkResults; }

        public void setOriginalText(String v)           { this.originalText        = v; }
        public void setIntent(String v)                 { this.intent              = v; }
        public void setParsed(ParsedSession v)          { this.parsed              = v; }
        public void setConfirmationMessage(String v)    { this.confirmationMessage = v; }
        public void setComplete(boolean v)              { this.complete            = v; }
        public void setSaved(boolean v)                 { this.saved               = v; }
        public void setTrackerUpdate(boolean v)         { this.trackerUpdate       = v; }
        public void setBulkUpdate(boolean v)            { this.bulkUpdate          = v; }
        public void setSavedSessionId(Long v)           { this.savedSessionId      = v; }
        public void setMissingFields(List<String> v)    { this.missingFields       = v; }
        public void setError(String v)                  { this.error               = v; }
        public void setMentorName(String v)             { this.mentorName          = v; }
        public void setWeekRange(String v)              { this.weekRange           = v; }
        public void setWeekNumber(Integer v)            { this.weekNumber          = v; }
        public void setHours(Double v)                  { this.hours               = v; }
        public void setMode(String v)                   { this.mode                = v; }
        public void setSessionDate(LocalDate v)         { this.sessionDate         = v; }
        public void setBulkResults(List<BulkResult> v)  { this.bulkResults         = v; }
    }

    // Result for each mentor in a bulk update
    public static class BulkResult {
        private String  mentorName;
        private String  associateId;
        private String  weekRange;
        private Integer weekNumber;
        private Double  hours;
        private String  mode;
        private boolean saved;
        private String  error;

        public BulkResult() {}
        public String  getMentorName()  { return mentorName; }
        public String  getAssociateId() { return associateId; }
        public String  getWeekRange()   { return weekRange; }
        public Integer getWeekNumber()  { return weekNumber; }
        public Double  getHours()       { return hours; }
        public String  getMode()        { return mode; }
        public boolean isSaved()        { return saved; }
        public String  getError()       { return error; }
        public void setMentorName(String v)  { this.mentorName  = v; }
        public void setAssociateId(String v) { this.associateId = v; }
        public void setWeekRange(String v)   { this.weekRange   = v; }
        public void setWeekNumber(Integer v) { this.weekNumber  = v; }
        public void setHours(Double v)       { this.hours       = v; }
        public void setMode(String v)        { this.mode        = v; }
        public void setSaved(boolean v)      { this.saved       = v; }
        public void setError(String v)       { this.error       = v; }
    }
}
