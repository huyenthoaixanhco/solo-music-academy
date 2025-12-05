package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.TeachingAssignmentResponse;
import com.solo.solo_music_academy.service.TeachingAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teacher/schedule")
@RequiredArgsConstructor
public class TeacherScheduleController {

    private final TeachingAssignmentService teachingAssignmentService;

    // Giáo viên mở app → xem lịch dạy cố định trong tuần
    // GET /teacher/schedule
    @GetMapping
    public ResponseEntity<List<TeachingAssignmentResponse>> getMySchedule() {
        return ResponseEntity.ok(
                teachingAssignmentService.getMyScheduleAsTeacher()
        );
    }
}
