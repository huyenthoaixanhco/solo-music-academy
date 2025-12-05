package com.solo.solo_music_academy.service;

import com.solo.solo_music_academy.dto.CreateLeadCareHistoryRequest;
import com.solo.solo_music_academy.dto.LeadAdminSummaryResponse;
import com.solo.solo_music_academy.dto.LeadCareHistoryResponse;
import com.solo.solo_music_academy.dto.LeadSummaryResponse;
import com.solo.solo_music_academy.dto.LeadUpsertRequest;
import com.solo.solo_music_academy.entity.Lead;
import com.solo.solo_music_academy.entity.LeadCareLog;
import com.solo.solo_music_academy.entity.User;
import com.solo.solo_music_academy.repository.LeadCareLogRepository;
import com.solo.solo_music_academy.repository.LeadRepository;
import com.solo.solo_music_academy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupportLeadService {

    private final LeadRepository leadRepository;
    private final LeadCareLogRepository leadCareLogRepository;
    private final SupportService supportService;
    private final UserRepository userRepository;

    // ===== Helper: map Lead -> DTO cho admin =====
    private LeadAdminSummaryResponse toAdminSummary(Lead lead) {
        Optional<LeadCareLog> lastLogOpt =
                leadCareLogRepository.findTopByLeadIdOrderByCareTimeDesc(lead.getId());
        Optional<LeadCareLog> nextLogOpt =
                leadCareLogRepository.findTopByLeadIdAndNextCareTimeIsNotNullOrderByNextCareTimeAsc(lead.getId());
        User support = lead.getSupportUser();

        return LeadAdminSummaryResponse.builder()
                .id(lead.getId())
                .parentName(lead.getParentName())
                .parentPhone(lead.getParentPhone())
                .parentEmail(lead.getParentEmail())
                .studentName(lead.getStudentName())
                .studentAge(lead.getStudentAge())
                .instrument(lead.getInstrument())
                .lessonType(lead.getLessonType())
                .level(lead.getLevel())
                .preferredSchedule(lead.getPreferredSchedule())
                .source(lead.getSource())
                .status(lead.getStatus())
                .createdAt(lead.getCreatedAt())
                .lastCareTime(lastLogOpt.map(LeadCareLog::getCareTime).orElse(null))
                .nextCareTime(nextLogOpt.map(LeadCareLog::getNextCareTime).orElse(null))
                .supportUserId(support != null ? support.getId() : null)
                .supportFullName(support != null ? support.getFullName() : null)
                .build();
    }

    // ===== 1) Support: list lead của chính mình =====
    @Transactional(readOnly = true)
    public List<LeadSummaryResponse> getMyLeads() {
        Long supportUserId = supportService.getCurrentSupportUserId();

        List<Lead> leads = leadRepository.findBySupportUser_IdOrderByCreatedAtDesc(supportUserId);
        List<LeadSummaryResponse> result = new ArrayList<>();

        for (Lead lead : leads) {
            Optional<LeadCareLog> lastLogOpt =
                    leadCareLogRepository.findTopByLeadIdOrderByCareTimeDesc(lead.getId());
            Optional<LeadCareLog> nextLogOpt =
                    leadCareLogRepository.findTopByLeadIdAndNextCareTimeIsNotNullOrderByNextCareTimeAsc(lead.getId());

            LeadSummaryResponse dto = LeadSummaryResponse.builder()
                    .id(lead.getId())
                    .parentName(lead.getParentName())
                    .parentPhone(lead.getParentPhone())
                    .parentEmail(lead.getParentEmail())
                    .studentName(lead.getStudentName())
                    .studentAge(lead.getStudentAge())
                    .instrument(lead.getInstrument())
                    .lessonType(lead.getLessonType())
                    .level(lead.getLevel())
                    .preferredSchedule(lead.getPreferredSchedule())
                    .source(lead.getSource())
                    .status(lead.getStatus())
                    .createdAt(lead.getCreatedAt())
                    .lastCareTime(lastLogOpt.map(LeadCareLog::getCareTime).orElse(null))
                    .nextCareTime(nextLogOpt.map(LeadCareLog::getNextCareTime).orElse(null))
                    .build();

            result.add(dto);
        }

        return result;
    }

    // ===== Helper: build list history CSKH cho 1 lead =====
    private List<LeadCareHistoryResponse> buildCareHistoryList(Lead lead) {
        List<LeadCareLog> logs = leadCareLogRepository.findByLeadIdOrderByCareTimeDesc(lead.getId());

        String leadName;
        if (lead.getStudentName() != null && !lead.getStudentName().isBlank()) {
            leadName = lead.getStudentName();
        } else if (lead.getParentName() != null && !lead.getParentName().isBlank()) {
            leadName = "PH: " + lead.getParentName();
        } else {
            leadName = "Lead #" + lead.getId();
        }

        List<LeadCareHistoryResponse> result = new ArrayList<>();
        for (LeadCareLog log : logs) {
            User support = log.getSupportUser();

            LeadCareHistoryResponse dto = LeadCareHistoryResponse.builder()
                    .id(log.getId())
                    .leadId(lead.getId())
                    .leadName(leadName)
                    .supportUserId(support != null ? support.getId() : null)
                    .supportFullName(support != null ? support.getFullName() : null)
                    .careTime(log.getCareTime())
                    .careType(log.getCareType())
                    .channel(log.getChannel())
                    .content(log.getContent())
                    .result(log.getResult())
                    .important(log.isImportant())
                    .nextCareTime(log.getNextCareTime())
                    .build();
            result.add(dto);
        }
        return result;
    }

    // ===== 2) Support: xem lịch sử CSKH 1 lead (chỉ lead của mình) =====
    @Transactional(readOnly = true)
    public List<LeadCareHistoryResponse> getLeadCareHistory(Long leadId) {
        Long supportUserId = supportService.getCurrentSupportUserId();

        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead không tồn tại"));

        if (lead.getSupportUser() == null
                || !lead.getSupportUser().getId().equals(supportUserId)) {
            throw new RuntimeException("Bạn không có quyền xem lead này");
        }

        return buildCareHistoryList(lead);
    }

    // ===== 3) Support: tạo log CSKH cho lead =====
    @Transactional
    public void createLeadCareLog(Long leadId, CreateLeadCareHistoryRequest req) {
        Long supportUserId = supportService.getCurrentSupportUserId();
        User currentSupport = supportService.getCurrentSupportUser();

        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead không tồn tại"));

        if (lead.getSupportUser() == null
                || !lead.getSupportUser().getId().equals(supportUserId)) {
            throw new RuntimeException("Bạn không có quyền tạo log cho lead này");
        }

        LocalDateTime now = LocalDateTime.now();

        LeadCareLog log = LeadCareLog.builder()
                .lead(lead)
                .supportUser(currentSupport)
                .careTime(now)
                .careType(currentSupport.getFullName())
                .channel(req.getChannel())
                .content(req.getContent())
                .result(req.getResult())
                .important(req.isImportant())
                .nextCareTime(req.getNextCareTime())
                .createdAt(now)
                .build();

        leadCareLogRepository.save(log);

        lead.setLastCareTime(now);
        if (req.getNextCareTime() != null) {
            lead.setNextCareTime(req.getNextCareTime());
        }
        leadRepository.save(lead);
    }

    // ===== 4) Admin: list tất cả lead =====
    @Transactional(readOnly = true)
    public List<LeadAdminSummaryResponse> getAllLeadsForAdmin() {
        List<Lead> leads = leadRepository.findAllByOrderByCreatedAtDesc();
        List<LeadAdminSummaryResponse> result = new ArrayList<>();
        for (Lead lead : leads) {
            result.add(toAdminSummary(lead));
        }
        return result;
    }

    // ===== 5) Admin: gán 1 CSKH cho nhiều lead =====
    @Transactional
    public void assignSupportToLeads(Long supportUserId, List<Long> leadIds) {
        if (supportUserId == null || leadIds == null || leadIds.isEmpty()) {
            throw new RuntimeException("Thiếu supportUserId hoặc danh sách leadIds");
        }

        User supportUser = userRepository.findById(supportUserId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user CSKH id=" + supportUserId));

        List<Lead> leads = leadRepository.findAllById(leadIds);
        if (leads.isEmpty()) {
            return;
        }

        for (Lead lead : leads) {
            lead.setSupportUser(supportUser);
        }

        leadRepository.saveAll(leads);
    }

    // ===== 6) Admin: xem lịch sử CSKH 1 lead (không check quyền) =====
    @Transactional(readOnly = true)
    public List<LeadCareHistoryResponse> getLeadCareHistoryForAdmin(Long leadId) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead không tồn tại: id=" + leadId));
        return buildCareHistoryList(lead);
    }

    // ===== 7) Admin: tạo lead =====
    @Transactional
    public LeadAdminSummaryResponse createLead(LeadUpsertRequest req) {
        Lead lead = new Lead();
        applyUpsertRequest(lead, req);

        if (lead.getStatus() == null || lead.getStatus().isBlank()) {
            lead.setStatus("NEW");
        }
        if (lead.getCreatedAt() == null) {
            lead.setCreatedAt(LocalDateTime.now());
        }

        Lead saved = leadRepository.save(lead);
        return toAdminSummary(saved);
    }

    // ===== 8) Admin: cập nhật lead =====
    @Transactional
    public LeadAdminSummaryResponse updateLead(Long leadId, LeadUpsertRequest req) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead không tồn tại: id=" + leadId));

        applyUpsertRequest(lead, req);
        Lead saved = leadRepository.save(lead);
        return toAdminSummary(saved);
    }

    private void applyUpsertRequest(Lead lead, LeadUpsertRequest req) {
        lead.setParentName(req.getParentName());
        lead.setParentPhone(req.getParentPhone());
        lead.setParentEmail(req.getParentEmail());
        lead.setStudentName(req.getStudentName());
        lead.setStudentAge(req.getStudentAge());
        lead.setInstrument(req.getInstrument());
        lead.setLessonType(req.getLessonType());
        lead.setLevel(req.getLevel());
        lead.setPreferredSchedule(req.getPreferredSchedule());
        lead.setSource(req.getSource());
        lead.setStatus(req.getStatus());
    }

    // ===== 9) Admin: xoá lead =====
    @Transactional
    public void deleteLead(Long leadId) {
        // xoá log trước để tránh lỗi FK (nếu không có cascade)
        leadCareLogRepository.deleteByLeadId(leadId);
        leadRepository.deleteById(leadId);
    }
}
