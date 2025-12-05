package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.TeachingAssignmentResponse;
import com.solo.solo_music_academy.service.TeachingAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student/schedule")
@RequiredArgsConstructor
public class StudentScheduleController {

    private final TeachingAssignmentService teachingAssignmentService;

    // Học viên mở app → xem lịch học cố định trong tuần
    // GET /student/schedule
    @GetMapping
    public ResponseEntity<List<TeachingAssignmentResponse>> getMySchedule() {
        return ResponseEntity.ok(
                teachingAssignmentService.getMyScheduleAsStudent()
        );
    }
}
