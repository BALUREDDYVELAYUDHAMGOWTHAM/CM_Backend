package com.coachmetrics.service;

import com.coachmetrics.dto.AgentDto;
import com.coachmetrics.dto.CoachSessionDto;
import com.coachmetrics.dto.MentorConnectDto;
import com.coachmetrics.entity.Mentor;
import com.coachmetrics.entity.MentorConnect;
import com.coachmetrics.entity.User;
import com.coachmetrics.enums.MentorConnectMode;
import com.coachmetrics.exception.ResourceNotFoundException;
import com.coachmetrics.repository.MentorConnectRepository;
import com.coachmetrics.repository.MentorRepository;
import com.coachmetrics.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

@Service
public class AgentService {

    @Autowired private MentorRepository        mentorRepo;
    @Autowired private UserRepository          userRepo;
    @Autowired private CoachSessionService     sessionService;
    @Autowired private MentorService           mentorService;
    @Autowired private MentorConnectRepository connectRepo;

    private static final String[] WEEK_RANGES = {
        "19 Jan - 23 Jan","27 Jan - 30 Jan","2 Feb - 6 Feb",
        "9 Feb - 13 Feb","16 Feb - 20 Feb","23 Feb - 27 Feb",
        "2 Mar - 6 Mar","9 Mar - 13 Mar","16 Mar - 19 Mar",
        "23 Mar - 27 Mar","30 Mar - 03 Apr","06 Apr - 10 Apr",
        "13 Apr - 17 Apr","20 Apr - 24 Apr"
    };
    private static final LocalDate[] WEEK_FROM = {
        LocalDate.of(2026,1,19),LocalDate.of(2026,1,27),LocalDate.of(2026,2,2),
        LocalDate.of(2026,2,9), LocalDate.of(2026,2,16),LocalDate.of(2026,2,23),
        LocalDate.of(2026,3,2), LocalDate.of(2026,3,9), LocalDate.of(2026,3,16),
        LocalDate.of(2026,3,23),LocalDate.of(2026,3,30),LocalDate.of(2026,4,6),
        LocalDate.of(2026,4,13),LocalDate.of(2026,4,20)
    };
    private static final LocalDate[] WEEK_TO = {
        LocalDate.of(2026,1,23),LocalDate.of(2026,1,30),LocalDate.of(2026,2,6),
        LocalDate.of(2026,2,13),LocalDate.of(2026,2,20),LocalDate.of(2026,2,27),
        LocalDate.of(2026,3,6), LocalDate.of(2026,3,13),LocalDate.of(2026,3,19),
        LocalDate.of(2026,3,27),LocalDate.of(2026,4,3), LocalDate.of(2026,4,10),
        LocalDate.of(2026,4,17),LocalDate.of(2026,4,24)
    };

    // ── Entry point ───────────────────────────────────────────────────────────
    public AgentDto.Response process(AgentDto.Request req, String coachEmail) {
        User coach = userRepo.findByEmail(coachEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Coach not found"));

        String text  = req.getText().trim();
        String lower = text.toLowerCase();
        AgentDto.Response res = new AgentDto.Response();
        res.setOriginalText(text);

        // Detect BULK first — multiple mentor names or "and", commas with names
        List<Mentor> myMentors = mentorRepo.findByCoach(coach);
        List<Mentor> mentionedMentors = findAllMentors(text, lower, myMentors);

        String intent = detectIntent(lower);
        res.setIntent(intent);

        System.out.println("[Agent] Intent: " + intent
            + " | Mentors found: " + mentionedMentors.size()
            + " | Text: " + text);

        // BULK UPDATE: multiple mentors detected + tracker intent
        if (mentionedMentors.size() > 1 && "UPDATE_TRACKER".equals(intent)) {
            return processBulkUpdate(text, lower, req.isAutoSave(), coach, mentionedMentors, res);
        }

        if ("UPDATE_TRACKER".equals(intent)) {
            return processTrackerUpdate(text, lower, req.isAutoSave(), coach, res);
        }

        return processNewSession(text, lower, req.isAutoSave(), coach, res);
    }

    // ── Intent detection ─────────────────────────────────────────────────────
    private String detectIntent(String lower) {
        if (lower.startsWith("update ") || lower.startsWith("mark ")
                || lower.startsWith("record ") || lower.startsWith("log ")) {
            return "UPDATE_TRACKER";
        }
        boolean hasTimeRange = lower.matches(".*\\d{1,2}(am|pm|:\\d{2}).*to.*\\d{1,2}(am|pm|:\\d{2}).*");
        boolean hasUpdateWord = lower.matches(".*\\b(update|mark|record|done|completed|happened)\\b.*");
        if (hasUpdateWord && !hasTimeRange) return "UPDATE_TRACKER";
        if (lower.contains("tracker") || lower.contains("weekly connect")) return "UPDATE_TRACKER";
        return "NEW_SESSION";
    }

    // ── BULK UPDATE ───────────────────────────────────────────────────────────
    // Handles: "update 1 hr virtual for Arun, Divya and Meena on 2nd Apr"
    private AgentDto.Response processBulkUpdate(String text, String lower, boolean autoSave,
            User coach, List<Mentor> mentors, AgentDto.Response res) {

        LocalDate date  = parseDate(lower);
        int weekNum     = findWeekNumber(date);
        String weekRange= weekNum >= 1 && weekNum <= 14 ? WEEK_RANGES[weekNum-1] : null;
        Double hours    = extractHours(lower);
        String mode     = extractMode(lower);
        if (mode == null) mode = "VIRTUAL";

        res.setTrackerUpdate(true);
        res.setBulkUpdate(true);
        res.setWeekRange(weekRange);
        res.setWeekNumber(weekNum);
        res.setHours(hours);
        res.setMode(mode);
        res.setSessionDate(date);

        List<String> missing = new ArrayList<>();
        if (weekRange == null) missing.add("valid date");
        if (hours     == null) missing.add("hours");
        res.setMissingFields(missing);

        boolean complete = weekRange != null && hours != null;
        res.setComplete(complete);

        // Build preview message
        StringBuilder msg = new StringBuilder();
        msg.append("I understood (Bulk Tracker Update):\n\n");
        msg.append("📅 Date: ").append(date).append("\n");
        msg.append("📆 Week: ").append(weekRange != null ? weekRange : "❓").append("\n");
        msg.append("⏱ Hours: ").append(hours != null ? hours + " hrs" : "❓").append("\n");
        msg.append("💻 Mode: ").append(mode).append("\n\n");
        msg.append("👥 Mentors to update (").append(mentors.size()).append("):\n");
        for (Mentor m : mentors) {
            msg.append("  • ").append(m.getFullName())
               .append(" (").append(m.getAssociateId()).append(")\n");
        }

        if (!missing.isEmpty()) {
            msg.append("\n⚠️ Still need: ").append(String.join(", ", missing));
        }
        res.setConfirmationMessage(msg.toString());

        // Auto-save: update all mentors
        if (autoSave && complete) {
            List<AgentDto.BulkResult> results = new ArrayList<>();
            int successCount = 0;
            final String finalMode = mode;
            final Double finalHours = hours;

            for (Mentor mentor : mentors) {
                AgentDto.BulkResult result = new AgentDto.BulkResult();
                result.setMentorName(mentor.getFullName());
                result.setAssociateId(mentor.getAssociateId());
                result.setWeekRange(weekRange);
                result.setWeekNumber(weekNum);
                result.setHours(finalHours);
                result.setMode(finalMode);

                try {
                    List<MentorConnect> connects =
                            connectRepo.findByMentorIdOrderByWeekNumberAsc(mentor.getId());
                    final Integer targetWeek = weekNum;
                    MentorConnect mc = connects.stream()
                            .filter(c -> targetWeek.equals(c.getWeekNumber()))
                            .findFirst().orElse(null);

                    if (mc != null) {
                        MentorConnectDto.Request updateReq = new MentorConnectDto.Request();
                        updateReq.setHappened(true);
                        updateReq.setMode(finalMode);
                        updateReq.setConnectDate(date);
                        updateReq.setHours(finalHours);
                        mentorService.updateConnect(mc.getId(), updateReq);
                        result.setSaved(true);
                        successCount++;
                        System.out.println("[Agent] ✅ Bulk: " + mentor.getFullName() + " week=" + weekNum);
                    } else {
                        // Create row if missing
                        MentorConnect newMc = new MentorConnect();
                        newMc.setMentor(mentor);
                        newMc.setWeekRange(weekRange);
                        newMc.setWeekNumber(weekNum);
                        newMc.setHappened(true);
                        newMc.setConnectDate(date);
                        newMc.setHours(finalHours);
                        try { newMc.setMode(MentorConnectMode.valueOf(finalMode)); } catch (Exception ignored) {}
                        connectRepo.save(newMc);
                        result.setSaved(true);
                        successCount++;
                        System.out.println("[Agent] ✅ Bulk (new row): " + mentor.getFullName());
                    }
                } catch (Exception e) {
                    result.setSaved(false);
                    result.setError(e.getMessage());
                    System.out.println("[Agent] ❌ Bulk failed for " + mentor.getFullName() + ": " + e.getMessage());
                }
                results.add(result);
            }

            res.setBulkResults(results);
            res.setSaved(successCount > 0);

            // Build result message
            StringBuilder saved = new StringBuilder();
            saved.append("✅ Bulk Update Complete!\n\n");
            saved.append("📆 Week: ").append(weekRange).append("\n");
            saved.append("⏱ ").append(finalHours).append(" hrs · ").append(finalMode).append("\n\n");
            for (AgentDto.BulkResult r : results) {
                saved.append(r.isSaved() ? "✅ " : "❌ ")
                     .append(r.getMentorName())
                     .append(r.isSaved() ? " — updated" : " — " + r.getError())
                     .append("\n");
            }
            saved.append("\n").append(successCount).append("/").append(mentors.size())
                 .append(" mentors updated. Open Weekly Tracker to see green cells.");
            res.setConfirmationMessage(saved.toString());
        }

        return res;
    }

    // ── Single tracker update ─────────────────────────────────────────────────
    private AgentDto.Response processTrackerUpdate(String text, String lower,
            boolean autoSave, User coach, AgentDto.Response res) {

        List<Mentor> myMentors = mentorRepo.findByCoach(coach);
        Mentor mentor   = findSingleMentor(text, lower, myMentors);
        LocalDate date  = parseDate(lower);
        int weekNum     = findWeekNumber(date);
        String weekRange= weekNum >= 1 && weekNum <= 14 ? WEEK_RANGES[weekNum-1] : null;
        Double hours    = extractHours(lower);
        String mode     = extractMode(lower);
        if (mode == null) mode = "VIRTUAL";

        System.out.println("[Agent] Single update: mentor=" + (mentor != null ? mentor.getFullName() : "null")
            + " date=" + date + " weekNum=" + weekNum + " hours=" + hours);

        res.setTrackerUpdate(true);
        res.setMentorName(mentor != null ? mentor.getFullName() : null);
        res.setWeekRange(weekRange);
        res.setWeekNumber(weekNum);
        res.setHours(hours);
        res.setMode(mode);
        res.setSessionDate(date);

        StringBuilder msg = new StringBuilder();
        msg.append("I understood (Tracker Update):\n");
        msg.append("👤 Mentor: ").append(mentor != null ? mentor.getFullName() : "❓ Not found").append("\n");
        msg.append("📅 Date: ").append(date).append("\n");
        msg.append("📆 Week: ").append(weekRange != null ? weekRange : "❓ Date outside range").append("\n");
        msg.append("⏱ Hours: ").append(hours != null ? hours + " hrs" : "❓ Not found").append("\n");
        msg.append("💻 Mode: ").append(mode);
        res.setConfirmationMessage(msg.toString());

        List<String> missing = new ArrayList<>();
        if (mentor    == null) missing.add("mentor name or ID");
        if (weekRange == null) missing.add("valid date (Jan 19 – Apr 24)");
        if (hours     == null) missing.add("hours (e.g. 1 hr)");
        res.setMissingFields(missing);

        boolean complete = mentor != null && weekRange != null && hours != null;
        res.setComplete(complete);

        if (autoSave && complete) {
            try {
                final Integer targetWeek = weekNum;
                final String finalMode   = mode;
                final Double finalHours  = hours;

                List<MentorConnect> connects =
                        connectRepo.findByMentorIdOrderByWeekNumberAsc(mentor.getId());

                System.out.println("[Agent] Total connect rows: " + connects.size());

                MentorConnect mc = connects.stream()
                        .filter(c -> targetWeek.equals(c.getWeekNumber()))
                        .findFirst().orElse(null);

                System.out.println("[Agent] Found week " + weekNum + ": " + (mc != null ? "id="+mc.getId() : "NULL"));

                if (mc == null) {
                    // Create the row
                    mc = new MentorConnect();
                    mc.setMentor(mentor);
                    mc.setWeekRange(weekRange);
                    mc.setWeekNumber(weekNum);
                    connectRepo.save(mc);
                    System.out.println("[Agent] Created new connect row for week " + weekNum);
                }

                MentorConnectDto.Request updateReq = new MentorConnectDto.Request();
                updateReq.setHappened(true);
                updateReq.setMode(finalMode);
                updateReq.setConnectDate(date);
                updateReq.setHours(finalHours);
                mentorService.updateConnect(mc.getId(), updateReq);

                System.out.println("[Agent] ✅ Saved: " + mentor.getFullName()
                    + " week=" + weekNum + " hours=" + finalHours);

                res.setSaved(true);
                res.setConfirmationMessage(
                    "✅ Weekly Tracker updated!\n\n" +
                    "👤 " + mentor.getFullName() + "\n" +
                    "📆 Week " + weekNum + ": " + weekRange + "\n" +
                    "⏱ " + finalHours + " hrs · " + finalMode + "\n\n" +
                    "Click 'View in Weekly Tracker' below ↓");

            } catch (Exception e) {
                System.out.println("[Agent] ❌ Error: " + e.getMessage());
                e.printStackTrace();
                res.setSaved(false);
                res.setError("Update failed: " + e.getMessage());
            }
        }
        return res;
    }

    // ── New session ───────────────────────────────────────────────────────────
    private AgentDto.Response processNewSession(String text, String lower,
            boolean autoSave, User coach, AgentDto.Response res) {

        List<Mentor> myMentors = mentorRepo.findByCoach(coach);
        ParsedSession p = parseSession(text, lower, myMentors);
        res.setParsed(p);
        res.setConfirmationMessage(buildSessionConfirmation(p));
        res.setMissingFields(p.getMissingFields());
        res.setComplete(p.isComplete());

        if (autoSave && p.isComplete()) {
            try {
                CoachSessionDto.Request sr = new CoachSessionDto.Request();
                sr.setMentorId(p.getMentorId()); sr.setSessionDate(p.getSessionDate());
                sr.setFromTime(p.getFromTime()); sr.setToTime(p.getToTime());
                sr.setBatchOwnerName(p.getBatchOwnerName()); sr.setNotes(p.getNotes());
                CoachSessionDto.Response saved = sessionService.createSession(sr, coach.getEmail());
                res.setSaved(true);
                res.setSavedSessionId(saved.getId());
                res.setConfirmationMessage("✅ Session saved!\n\n" + res.getConfirmationMessage());
            } catch (Exception e) {
                res.setSaved(false);
                res.setError("Save failed: " + e.getMessage());
            }
        }
        return res;
    }

    // ── Find ALL mentioned mentors (for bulk) ─────────────────────────────────
    private List<Mentor> findAllMentors(String text, String lower, List<Mentor> mentors) {
        List<Mentor> found = new ArrayList<>();
        Set<Long> addedIds = new HashSet<>();

        for (Mentor m : mentors) {
            // Check associate ID
            if (m.getAssociateId() != null &&
                    lower.contains(m.getAssociateId().toLowerCase())) {
                if (addedIds.add(m.getId())) found.add(m);
                continue;
            }
            // Check full name (all parts present)
            String[] parts = m.getFullName().toLowerCase().split(" ");
            boolean allFound = Arrays.stream(parts)
                    .allMatch(p -> p.length() > 2 && lower.contains(p));
            if (allFound) {
                if (addedIds.add(m.getId())) found.add(m);
                continue;
            }
            // Check first name only if it's unique enough (length > 3)
            String firstName = parts[0];
            if (firstName.length() > 3 && lower.contains(firstName)) {
                // Make sure it's not a false match — check word boundary
                Pattern p = Pattern.compile("\\b" + Pattern.quote(firstName) + "\\b");
                if (p.matcher(lower).find()) {
                    if (addedIds.add(m.getId())) found.add(m);
                }
            }
        }
        return found;
    }

    // ── Find single mentor ────────────────────────────────────────────────────
    private Mentor findSingleMentor(String text, String lower, List<Mentor> mentors) {
        List<Mentor> all = findAllMentors(text, lower, mentors);
        return all.isEmpty() ? null : all.get(0);
    }

    // ── Week helpers ──────────────────────────────────────────────────────────
    private int findWeekNumber(LocalDate date) {
        if (date == null) return 1;
        for (int i = 0; i < WEEK_FROM.length; i++) {
            if (!date.isBefore(WEEK_FROM[i]) && !date.isAfter(WEEK_TO[i])) return i + 1;
        }
        long minDiff = Long.MAX_VALUE; int closest = 1;
        for (int i = 0; i < WEEK_FROM.length; i++) {
            long diff = Math.min(
                Math.abs(date.toEpochDay() - WEEK_FROM[i].toEpochDay()),
                Math.abs(date.toEpochDay() - WEEK_TO[i].toEpochDay())
            );
            if (diff < minDiff) { minDiff = diff; closest = i + 1; }
        }
        return closest;
    }

    // ── Extractors ────────────────────────────────────────────────────────────
    private Double extractHours(String lower) {
        Matcher m = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(?:hr|hrs|hour|hours)").matcher(lower);
        if (m.find()) return Double.parseDouble(m.group(1));
        Matcher m2 = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*h\\b").matcher(lower);
        if (m2.find()) return Double.parseDouble(m2.group(1));
        return null;
    }

    private String extractMode(String lower) {
        if (lower.contains("virtual")||lower.contains("online")||lower.contains("zoom")
                ||lower.contains("teams")||lower.contains("remote")) return "VIRTUAL";
        if (lower.contains("in person")||lower.contains("in-person")||lower.contains("f2f")
                ||lower.contains("office")||lower.contains("physical")) return "IN_PERSON";
        if (lower.contains("hybrid")) return "HYBRID";
        return null;
    }

    private LocalDate parseDate(String lower) {
        LocalDate today = LocalDate.now();
        if (lower.contains("today"))     return today;
        if (lower.contains("yesterday")) return today.minusDays(1);

        String[] months = {"jan","feb","mar","apr","may","jun","jul","aug","sep","oct","nov","dec"};
        for (int i = 0; i < months.length; i++) {
            if (lower.contains(months[i])) {
                Pattern mp = Pattern.compile(
                    "(\\d{1,2})(?:st|nd|rd|th)?\\s*" + months[i] +
                    "|" + months[i] + "\\s*(\\d{1,2})(?:st|nd|rd|th)?");
                Matcher mm = mp.matcher(lower);
                if (mm.find()) {
                    try {
                        String ds = mm.group(1) != null ? mm.group(1) : mm.group(2);
                        return LocalDate.of(2026, i + 1, Integer.parseInt(ds));
                    } catch (Exception ignored) {}
                }
            }
        }
        Matcher dm = Pattern.compile("(\\d{1,2})[/\\-](\\d{1,2})").matcher(lower);
        if (dm.find()) {
            try { return LocalDate.of(2026, Integer.parseInt(dm.group(2)), Integer.parseInt(dm.group(1))); }
            catch (Exception ignored) {}
        }
        return today;
    }

    // ── Session parser ────────────────────────────────────────────────────────
    private ParsedSession parseSession(String text, String lower, List<Mentor> mentors) {
        ParsedSession p = new ParsedSession();
        Mentor m = findSingleMentor(text, lower, mentors);
        if (m != null) { p.setMentorId(m.getId()); p.setMentorName(m.getFullName()); p.setAssociateId(m.getAssociateId()); }
        p.setMode(extractMode(lower));
        p.setSessionDate(parseDate(lower));
        String[] t = parseTimes(text);
        if (t[0] != null) p.setFromTime(t[0]);
        if (t[1] != null) p.setToTime(t[1]);
        if (p.getFromTime() != null && p.getToTime() != null) p.setHours(calcHours(p.getFromTime(), p.getToTime()));
        else p.setHours(extractHours(lower));
        Matcher bm = Pattern.compile("(?:batch\\s*owner|owner)[:\\s]+([A-Z][a-zA-Z]+(?:\\s+[A-Z][a-zA-Z]+)?)", Pattern.CASE_INSENSITIVE).matcher(text);
        if (bm.find()) p.setBatchOwnerName(bm.group(1).trim());
        Matcher nm = Pattern.compile("(?:notes?|topic|discussed)[:\\s]+(.+?)(?:\\.|$)", Pattern.CASE_INSENSITIVE).matcher(text);
        if (nm.find()) p.setNotes(nm.group(1).trim());
        return p;
    }

    private String[] parseTimes(String text) {
        String[] r = {null, null};
        String tp = "(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)?";
        Matcher rm = Pattern.compile(tp + "\\s*(?:to|until|-|–)\\s*" + tp, Pattern.CASE_INSENSITIVE).matcher(text);
        if (rm.find()) { r[0] = norm(rm.group(1),rm.group(2),rm.group(3)); r[1] = norm(rm.group(4),rm.group(5),rm.group(6)); }
        return r;
    }

    private String norm(String h, String m, String ap) {
        if (h == null) return null;
        int hh = Integer.parseInt(h), mm = m != null ? Integer.parseInt(m) : 0;
        if (ap != null) { if ("pm".equalsIgnoreCase(ap) && hh < 12) hh += 12; if ("am".equalsIgnoreCase(ap) && hh == 12) hh = 0; }
        return String.format("%02d:%02d", hh, mm);
    }

    private double calcHours(String from, String to) {
        String[] fp = from.split(":"), tp = to.split(":");
        int diff = (Integer.parseInt(tp[0])*60+Integer.parseInt(tp[1]))-(Integer.parseInt(fp[0])*60+Integer.parseInt(fp[1]));
        if (diff <= 0) diff += 1440;
        return Math.round(diff/60.0*2)/2.0;
    }

    private String buildSessionConfirmation(ParsedSession p) {
        return "I understood (New Session):\n" +
               "👤 " + (p.getMentorName() != null ? p.getMentorName() : "❓") + "\n" +
               "📅 " + (p.getSessionDate() != null ? p.getSessionDate() : "❓") + "\n" +
               "🕐 " + (p.getFromTime() != null ? p.getFromTime() + " → " + p.getToTime() : "❓") + "\n" +
               "⏱ "  + (p.getHours() != null ? p.getHours() + " hrs" : "❓") + "\n" +
               "💻 " + (p.getMode() != null ? p.getMode() : "❓");
    }

    // ── Inner model ───────────────────────────────────────────────────────────
    public static class ParsedSession {
        private Long mentorId; private String mentorName, associateId, mode, fromTime, toTime, batchOwnerName, notes;
        private LocalDate sessionDate; private Double hours;
        public boolean isComplete() { return mentorId != null && sessionDate != null && fromTime != null && toTime != null; }
        public List<String> getMissingFields() {
            List<String> m = new ArrayList<>();
            if (mentorId == null) m.add("mentor name"); if (sessionDate == null) m.add("date");
            if (fromTime == null) m.add("start time"); if (toTime == null) m.add("end time");
            return m;
        }
        public Long getMentorId()       { return mentorId; }   public String getMentorName()     { return mentorName; }
        public String getAssociateId()  { return associateId; } public String getMode()           { return mode; }
        public LocalDate getSessionDate(){ return sessionDate; } public String getFromTime()      { return fromTime; }
        public String getToTime()       { return toTime; }     public Double getHours()           { return hours; }
        public String getBatchOwnerName(){ return batchOwnerName; } public String getNotes()      { return notes; }
        public void setMentorId(Long v)       { mentorId = v; }    public void setMentorName(String v)  { mentorName = v; }
        public void setAssociateId(String v)  { associateId = v; } public void setMode(String v)        { mode = v; }
        public void setSessionDate(LocalDate v){ sessionDate = v; } public void setFromTime(String v)   { fromTime = v; }
        public void setToTime(String v)       { toTime = v; }     public void setHours(Double v)        { hours = v; }
        public void setBatchOwnerName(String v){ batchOwnerName = v; } public void setNotes(String v)   { notes = v; }
    }
}
