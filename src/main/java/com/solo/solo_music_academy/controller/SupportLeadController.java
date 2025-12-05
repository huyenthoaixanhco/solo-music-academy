package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.CreateLeadCareHistoryRequest;
import com.solo.solo_music_academy.dto.LeadCareHistoryResponse;
import com.solo.solo_music_academy.dto.LeadSummaryResponse;
import com.solo.solo_music_academy.service.SupportLeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/support/leads")
@RequiredArgsConstructor
public class SupportLeadController {

    private final SupportLeadService supportLeadService;

    /**
     * CSKH xem danh sách khách hàng tiềm năng của CHÍNH MÌNH
     * GET /support/leads
     */
    @GetMapping
    public ResponseEntity<List<LeadSummaryResponse>> getMyLeads() {
        return ResponseEntity.ok(supportLeadService.getMyLeads());
    }

    /**
     * CSKH xem timeline CSKH cho 1 lead
     * GET /support/leads/{leadId}/history
     */
    @GetMapping("/{leadId}/history")
    public ResponseEntity<List<LeadCareHistoryResponse>> getLeadCareHistory(
            @PathVariable Long leadId
    ) {
        return ResponseEntity.ok(supportLeadService.getLeadCareHistory(leadId));
    }

    /**
     * CSKH tạo log CSKH cho 1 lead
     * POST /support/leads/{leadId}/care-history
     */
    @PostMapping("/{leadId}/care-history")
    public ResponseEntity<Void> createLeadCareHistory(
            @PathVariable Long leadId,
            @RequestBody CreateLeadCareHistoryRequest req
    ) {
        supportLeadService.createLeadCareLog(leadId, req);
        return ResponseEntity.ok().build();
    }
}
