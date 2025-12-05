package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.StudentPackageCreateRequest;
import com.solo.solo_music_academy.dto.StudentPackageResponse;
import com.solo.solo_music_academy.entity.StudentPackage;
import com.solo.solo_music_academy.service.StudentPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/packages")
@RequiredArgsConstructor
public class AdminStudentPackageController {

    private final StudentPackageService packageService;

    // Tạo gói học (1 gói – nhiều lịch)
    @PostMapping
    public ResponseEntity<StudentPackageResponse> createPackage(
            @RequestBody StudentPackageCreateRequest req
    ) {
        StudentPackage pkg = packageService.createPackage(req);
        return ResponseEntity.ok(StudentPackageResponse.fromEntity(pkg));
    }

    // Lấy danh sách gói học
    @GetMapping
    public ResponseEntity<List<StudentPackageResponse>> getAllPackages() {
        return ResponseEntity.ok(packageService.getAllPackages());
    }

    // ====== MỚI: Cập nhật gói học ======
    @PutMapping("/{id}")
    public ResponseEntity<StudentPackageResponse> updatePackage(
            @PathVariable Long id,
            @RequestBody StudentPackageCreateRequest req
    ) {
        StudentPackage pkg = packageService.updatePackage(id, req);
        return ResponseEntity.ok(StudentPackageResponse.fromEntity(pkg));
    }

    // ====== MỚI: Xóa gói học ======
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePackage(
            @PathVariable Long id
    ) {
        packageService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }
}
