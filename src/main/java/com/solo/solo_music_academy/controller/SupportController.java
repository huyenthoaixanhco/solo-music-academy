package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.CareReminderResponse;
import com.solo.solo_music_academy.dto.StudentCareSummaryResponse;
import com.solo.solo_music_academy.service.SupportStudentService;
import com.solo.solo_music_academy.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportStudentService supportStudentService;
    private final SupportService supportService;

    // ==== SUPPORT mở app → thấy list học viên mình phụ trách ====
    @GetMapping("/my-students")
    public ResponseEntity<List<StudentCareSummaryResponse>> getMyStudents() {
        return ResponseEntity.ok(supportStudentService.getMyStudents());
    }

    // ==== NHẮC VIỆC HÔM NAY (lịch học, học phí...) ====
    // GET /support/reminders/today
    @GetMapping("/reminders/today")
    public ResponseEntity<List<CareReminderResponse>> getMyRemindersToday() {
        return ResponseEntity.ok(supportService.getMyRemindersToday());
    }

    // ==== NHẮC VIỆC 7 NGÀY TỚI ====
    // GET /support/reminders/upcoming?days=7
    @GetMapping("/reminders/upcoming")
    public ResponseEntity<List<CareReminderResponse>> getMyUpcomingReminders(
            @RequestParam(defaultValue = "7") int days
    ) {
        return ResponseEntity.ok(supportService.getMyUpcomingReminders(days));
    }
}
