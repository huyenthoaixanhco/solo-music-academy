package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.CreateTeacherRequest;
import com.solo.solo_music_academy.dto.TeacherListResponse;
import com.solo.solo_music_academy.dto.UpdateTeacherRequest;
import com.solo.solo_music_academy.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/teachers")
@RequiredArgsConstructor
public class AdminTeacherController {

    private final AdminUserService adminUserService;

    // GET /admin/teachers
    @GetMapping
    public ResponseEntity<List<TeacherListResponse>> getAllTeachers() {
        return ResponseEntity.ok(adminUserService.getAllTeachers());
    }

    // POST /admin/teachers
    @PostMapping
    public ResponseEntity<TeacherListResponse> createTeacher(
            @RequestBody CreateTeacherRequest req
    ) {
        return ResponseEntity.ok(adminUserService.createTeacher(req));
    }

    // PUT /admin/teachers/{id}
    @PutMapping("/{id}")
    public ResponseEntity<TeacherListResponse> updateTeacher(
            @PathVariable Long id,
            @RequestBody UpdateTeacherRequest req
    ) {
        return ResponseEntity.ok(adminUserService.updateTeacher(id, req));
    }

    // DELETE /admin/teachers/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        adminUserService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }
}
