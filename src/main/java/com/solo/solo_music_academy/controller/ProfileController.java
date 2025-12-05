package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.StudentProfileResponse;
import com.solo.solo_music_academy.dto.TeacherProfileResponse;
import com.solo.solo_music_academy.dto.UserProfileResponse;
import com.solo.solo_music_academy.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // ==== INFO CHUNG (dùng cho header) ====
    @GetMapping("/user")
    public ResponseEntity<UserProfileResponse> getUserProfile() {
        return ResponseEntity.ok(profileService.getUserProfile());
    }

    // ==== HỒ SƠ HỌC VIÊN ====
    @GetMapping("/student")
    public ResponseEntity<StudentProfileResponse> getStudentProfile() {
        return ResponseEntity.ok(profileService.getStudentProfile());
    }

    // ==== HỒ SƠ GIÁO VIÊN ====
    @GetMapping("/teacher")
    public ResponseEntity<TeacherProfileResponse> getTeacherProfile() {
        return ResponseEntity.ok(profileService.getTeacherProfile());
    }
}
