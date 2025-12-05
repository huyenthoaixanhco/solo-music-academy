package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.TeachingAssignmentRequest;
import com.solo.solo_music_academy.dto.TeachingAssignmentResponse;
import com.solo.solo_music_academy.service.TeachingAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/teaching-assignments")
@RequiredArgsConstructor
public class AdminTeachingAssignmentController {

    private final TeachingAssignmentService teachingAssignmentService;

    // ===== ADMIN: xem tất cả phân công =====
    @GetMapping
    public ResponseEntity<List<TeachingAssignmentResponse>> getAllAssignments() {
        return ResponseEntity.ok(teachingAssignmentService.getAllAssignments());
    }

    // ===== ADMIN: tạo phân công mới =====
    @PostMapping
    public ResponseEntity<TeachingAssignmentResponse> createAssignment(
            @RequestBody TeachingAssignmentRequest req
    ) {
        return ResponseEntity.ok(teachingAssignmentService.createAssignment(req));
    }

    // ===== ADMIN: cập nhật phân công =====
    @PutMapping("/{id}")
    public ResponseEntity<TeachingAssignmentResponse> updateAssignment(
            @PathVariable Long id,
            @RequestBody TeachingAssignmentRequest req
    ) {
        return ResponseEntity.ok(teachingAssignmentService.updateAssignment(id, req));
    }

    // ===== ADMIN: xoá phân công =====
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(
            @PathVariable Long id
    ) {
        teachingAssignmentService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }
}
