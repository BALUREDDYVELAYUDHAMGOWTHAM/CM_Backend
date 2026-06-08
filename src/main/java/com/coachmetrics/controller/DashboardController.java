package com.coachmetrics.controller;

import com.coachmetrics.dto.DashboardDto;
import com.coachmetrics.dto.ReportDto;
import com.coachmetrics.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/admin/dashboard")
    public ResponseEntity<DashboardDto> adminDashboard() {
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }

    @GetMapping("/coach/dashboard")
    public ResponseEntity<DashboardDto> coachDashboard(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(dashboardService.getCoachDashboard(ud.getUsername()));
    }

    @GetMapping("/admin/reports")
    public ResponseEntity<List<ReportDto>> getReport(
            @RequestParam(required = false) String dept,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return ResponseEntity.ok(dashboardService.getReport(dept, from, to));
    }
}
