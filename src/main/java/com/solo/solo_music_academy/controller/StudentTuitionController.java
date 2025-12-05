package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.StudentTuitionOverviewResponse;
import com.solo.solo_music_academy.service.TuitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student/tuition")
@RequiredArgsConstructor
public class StudentTuitionController {

    private final TuitionService tuitionService;

    @GetMapping("/overview")
    public ResponseEntity<StudentTuitionOverviewResponse> getOverview() {
        return ResponseEntity.ok(tuitionService.getCurrentStudentTuitionOverview());
    }
}
