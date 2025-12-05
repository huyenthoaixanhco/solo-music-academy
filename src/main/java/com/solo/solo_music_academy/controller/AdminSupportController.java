package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.*;
import com.solo.solo_music_academy.entity.User;
import com.solo.solo_music_academy.service.AdminSupportService;
import com.solo.solo_music_academy.service.AdminUserService;
import com.solo.solo_music_academy.service.CustomerCareHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/support")
@RequiredArgsConstructor
public class AdminSupportController {

    private final AdminSupportService adminSupportService;
    private final CustomerCareHistoryService customerCareService;
    private final AdminUserService adminUserService;

    // ==== 1) DS nhân viên CSKH + thống kê tổng quan ====
    // GET /admin/support/users
    @GetMapping("/users")
    public ResponseEntity<List<SupportUserSummaryResponse>> getAllSupportUsers() {
        return ResponseEntity.ok(adminSupportService.getAllSupportUsers());
    }

    // ==== 2) ADMIN: tạo tài khoản CSKH (ROLE_SUPPORT) ====
    // POST /admin/support/users
    @PostMapping("/users")
    public ResponseEntity<UserProfileResponse> createSupportUser(
            @RequestBody CreateSupportUserRequest req
    ) {
        User user = adminUserService.createSupportUser(req);

        UserProfileResponse dto = UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus())
                .roles(
                        user.getRoles().stream()
                                .map(r -> r.getName())
                                .toList()
                )
                .build();

        return ResponseEntity.ok(dto);
    }

    // ==== 3) DS học viên của 1 support bất kỳ ====
    // GET /admin/support/{supportUserId}/students
    @GetMapping("/{supportUserId}/students")
    public ResponseEntity<List<StudentOfSupportResponse>> getStudentsOfSupport(
            @PathVariable Long supportUserId
    ) {
        return ResponseEntity.ok(adminSupportService.getStudentsOfSupport(supportUserId));
    }

    // ==== 4) DS học viên chưa gán support ====
    // GET /admin/support/unassigned-students
    @GetMapping("/unassigned-students")
    public ResponseEntity<List<StudentOfSupportResponse>> getUnassignedStudents() {
        return ResponseEntity.ok(adminSupportService.getUnassignedStudents());
    }

    // ==== 5) ADMIN: gán học viên -> support ====
    // POST /admin/support/assign
    @PostMapping("/assign")
    public ResponseEntity<StudentListResponse> assignStudentToSupport(
            @RequestBody AssignSupportRequest req
    ) {
        StudentListResponse updated = adminUserService.assignSupport(req);
        return ResponseEntity.ok(updated);
    }

    // ==== 6) Lịch sử CSKH của 1 support ====
    // GET /admin/support/{supportUserId}/care-history
    @GetMapping("/{supportUserId}/care-history")
    public ResponseEntity<List<CustomerCareHistoryResponse>> getCareHistoryBySupport(
            @PathVariable Long supportUserId
    ) {
        return ResponseEntity.ok(customerCareService.adminGetHistoryBySupport(supportUserId));
    }

    // ==== 7) Lịch sử CSKH của 1 học viên (admin xem) ====
    // GET /admin/support/care-history/student/{studentId}
    @GetMapping("/care-history/student/{studentId}")
    public ResponseEntity<List<CustomerCareHistoryResponse>> getCareHistoryByStudent(
            @PathVariable Long studentId
    ) {
        return ResponseEntity.ok(customerCareService.adminGetHistoryByStudent(studentId));
    }
}
