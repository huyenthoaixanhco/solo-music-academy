package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.*;
import com.solo.solo_music_academy.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    // ===== TẠO HỌC VIÊN =====
    @PostMapping("/students")
    public ResponseEntity<?> createStudent(@RequestBody CreateStudentRequest req) {
        return ResponseEntity.ok(adminUserService.createStudent(req));
    }

    // ===== SỬA HỌC VIÊN =====
    @PutMapping("/students/{id}")
    public ResponseEntity<?> updateStudent(
            @PathVariable Long id,
            @RequestBody CreateStudentRequest req
    ) {
        return ResponseEntity.ok(adminUserService.updateStudent(id, req));
    }

    // ===== XÓA HỌC VIÊN =====
    @DeleteMapping("/students/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        adminUserService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    // ===== GÁN CSKH CHO HỌC VIÊN =====
    @PutMapping("/students/assign-support")
    public ResponseEntity<?> assignSupport(@RequestBody AssignSupportRequest req) {
        return ResponseEntity.ok(adminUserService.assignSupport(req));
    }

    // ===== LẤY DS HỌC VIÊN =====
    @GetMapping("/students")
    public ResponseEntity<?> getAllStudents() {
        return ResponseEntity.ok(adminUserService.getAllStudents());
    }

    // ===== TẠO USER CSKH (SUPPORT) =====
    @PostMapping("/support-users")
    public ResponseEntity<?> createSupportUser(@RequestBody CreateSupportUserRequest req) {
        return ResponseEntity.ok(adminUserService.createSupportUser(req));
    }

    // ===== TẠO USER ADMIN (ROLE_ADMIN) ======
    @PostMapping("/admin-users")
    public ResponseEntity<?> createAdminUser(@RequestBody CreateSupportUserRequest req) {
        return ResponseEntity.ok(adminUserService.createAdminUser(req));
    }
}
