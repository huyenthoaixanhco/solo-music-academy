package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.CourseListResponse;
import com.solo.solo_music_academy.dto.CreateCourseRequest;
import com.solo.solo_music_academy.service.AdminCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/courses")
@RequiredArgsConstructor
public class AdminCourseController {

    private final AdminCourseService adminCourseService;

    // DS KHÓA HỌC
    @GetMapping
    public ResponseEntity<List<CourseListResponse>> getAllCourses() {
        return ResponseEntity.ok(adminCourseService.getAllCourses());
    }

    // LẤY 1 KHÓA
    @GetMapping("/{id}")
    public ResponseEntity<CourseListResponse> getCourse(@PathVariable Long id) {
        return ResponseEntity.ok(adminCourseService.getCourse(id));
    }

    // TẠO KHÓA
    @PostMapping
    public ResponseEntity<CourseListResponse> createCourse(
            @RequestBody CreateCourseRequest req
    ) {
        return ResponseEntity.ok(adminCourseService.createCourse(req));
    }

    // CẬP NHẬT
    @PutMapping("/{id}")
    public ResponseEntity<CourseListResponse> updateCourse(
            @PathVariable Long id,
            @RequestBody CreateCourseRequest req
    ) {
        return ResponseEntity.ok(adminCourseService.updateCourse(id, req));
    }

    // XOÁ
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        adminCourseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
