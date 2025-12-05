package com.solo.solo_music_academy.service;

import com.solo.solo_music_academy.dto.CustomerCareHistoryRequest;
import com.solo.solo_music_academy.dto.CustomerCareHistoryResponse;
import com.solo.solo_music_academy.entity.CustomerCareHistory;
import com.solo.solo_music_academy.entity.Student;
import com.solo.solo_music_academy.entity.User;
import com.solo.solo_music_academy.repository.CustomerCareHistoryRepository;
import com.solo.solo_music_academy.repository.StudentRepository;
import com.solo.solo_music_academy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerCareHistoryService {

    private final CustomerCareHistoryRepository careRepo;
    private final StudentRepository studentRepo;
    private final UserRepository userRepo;

    // ===== SUPPORT: tạo log CSKH cho học viên của mình =====
    public CustomerCareHistoryResponse supportCreateCareHistory(CustomerCareHistoryRequest req) {
        User support = getCurrentUser();

        Student student = studentRepo
                .findByIdAndCareStaffId(req.getStudentId(), support.getId())
                .orElseThrow(() ->
                        new RuntimeException("Student not found or not assigned to this support")
                );

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextCareTime = parseDateTime(req.getNextCareTime());

        CustomerCareHistory history = CustomerCareHistory.builder()
                .student(student)
                .supportUser(support)
                .careTime(now)
                .careType(req.getCareType())
                .channel(req.getChannel())
                .content(req.getContent())
                .result(req.getResult())
                .important(req.getImportant() != null && req.getImportant())
                .nextCareTime(nextCareTime)
                .build();

        careRepo.save(history);

        return mapToResponse(history);
    }

    // ===== SUPPORT: xem lịch sử CSKH của 1 học viên thuộc mình =====
    public List<CustomerCareHistoryResponse> supportGetHistoryByStudent(Long studentId) {
        User support = getCurrentUser();

        Student student = studentRepo
                .findByIdAndCareStaffId(studentId, support.getId())
                .orElseThrow(() ->
                        new RuntimeException("Student not found or not assigned to this support")
                );

        List<CustomerCareHistory> list =
                careRepo.findByStudentIdOrderByCareTimeDesc(student.getId());

        return list.stream().map(this::mapToResponse).toList();
    }

    // ===== ADMIN: xem log theo support =====
    public List<CustomerCareHistoryResponse> adminGetHistoryBySupport(Long supportUserId) {
        List<CustomerCareHistory> list =
                careRepo.findBySupportUserIdOrderByCareTimeDesc(supportUserId);
        return list.stream().map(this::mapToResponse).toList();
    }

    // ===== ADMIN: xem log theo học viên =====
    public List<CustomerCareHistoryResponse> adminGetHistoryByStudent(Long studentId) {
        List<CustomerCareHistory> list =
                careRepo.findByStudentIdOrderByCareTimeDesc(studentId);
        return list.stream().map(this::mapToResponse).toList();
    }

    // ===== MAP ENTITY -> RESPONSE DTO =====
    private CustomerCareHistoryResponse mapToResponse(CustomerCareHistory h) {
        Student s = h.getStudent();
        User stuUser = (s != null) ? s.getUser() : null;
        User sup = h.getSupportUser();

        return CustomerCareHistoryResponse.builder()
                .id(h.getId())
                .studentId(s != null ? s.getId() : null)
                .studentName(stuUser != null ? stuUser.getFullName() : null)
                .supportUserId(sup != null ? sup.getId() : null)
                .supportFullName(sup != null ? sup.getFullName() : null)
                .careTime(h.getCareTime() != null ? h.getCareTime().toString() : null)
                .careType(h.getCareType())
                .channel(h.getChannel())
                .content(h.getContent())
                .result(h.getResult())
                .important(h.getImportant())
                .nextCareTime(h.getNextCareTime() != null ? h.getNextCareTime().toString() : null)
                .build();
    }

    // ===== PARSE String -> LocalDateTime =====
    private LocalDateTime parseDateTime(String text) {
        if (text == null || text.isBlank()) return null;
        try {
            return LocalDateTime.parse(text);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // ===== Lấy user hiện tại từ SecurityContext =====
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
}
