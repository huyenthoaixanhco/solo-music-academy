package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.LeadAdminSummaryResponse;
import com.solo.solo_music_academy.dto.LeadCareHistoryResponse;
import com.solo.solo_music_academy.dto.LeadUpsertRequest;
import com.solo.solo_music_academy.service.SupportLeadService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/leads")
@RequiredArgsConstructor
public class AdminLeadController {

    private final SupportLeadService supportLeadService;

    // 1) List tất cả lead
    @GetMapping
    public ResponseEntity<List<LeadAdminSummaryResponse>> getAllLeads() {
        return ResponseEntity.ok(supportLeadService.getAllLeadsForAdmin());
    }

    // 2) Tạo lead
    @PostMapping
    public ResponseEntity<LeadAdminSummaryResponse> createLead(
            @RequestBody LeadUpsertRequest request
    ) {
        return ResponseEntity.ok(supportLeadService.createLead(request));
    }

    // 3) Cập nhật lead
    @PutMapping("/{leadId}")
    public ResponseEntity<LeadAdminSummaryResponse> updateLead(
            @PathVariable Long leadId,
            @RequestBody LeadUpsertRequest request
    ) {
        return ResponseEntity.ok(supportLeadService.updateLead(leadId, request));
    }

    // 4) Xoá lead
    @DeleteMapping("/{leadId}")
    public ResponseEntity<Void> deleteLead(@PathVariable Long leadId) {
        supportLeadService.deleteLead(leadId);
        return ResponseEntity.noContent().build();
    }

    // 5) Gán CSKH cho nhiều lead
    @PostMapping("/assign-support")
    public ResponseEntity<Void> assignSupportToLeads(
            @RequestBody AssignLeadsRequest request
    ) {
        supportLeadService.assignSupportToLeads(
                request.getSupportUserId(),
                request.getLeadIds()
        );
        return ResponseEntity.ok().build();
    }

    @Getter
    @Setter
    public static class AssignLeadsRequest {
        private Long supportUserId;
        private List<Long> leadIds;
    }

    // 6) Xem lịch sử CSKH 1 lead
    @GetMapping("/{leadId}/care-history")
    public ResponseEntity<List<LeadCareHistoryResponse>> getLeadCareHistory(
            @PathVariable Long leadId
    ) {
        return ResponseEntity.ok(
                supportLeadService.getLeadCareHistoryForAdmin(leadId)
        );
    }
}
