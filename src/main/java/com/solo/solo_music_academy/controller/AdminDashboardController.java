package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.AdminDashboardSummaryResponse;
import com.solo.solo_music_academy.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    // ===== ADMIN: TỔNG QUAN DASHBOARD =====
    // GET /admin/dashboard/summary
    @GetMapping("/summary")
    public ResponseEntity<AdminDashboardSummaryResponse> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }
}
