package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.AdHocSlotRequest;
import com.solo.solo_music_academy.dto.AttendanceSlotResponse;
import com.solo.solo_music_academy.service.AdminAttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/attendance")
@RequiredArgsConstructor
public class AdminAttendanceController {
      
    private final AdminAttendanceService attendanceService;

    @GetMapping("/week")
    public ResponseEntity<List<AttendanceSlotResponse>> getWeekAttendance(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "teacherId", required = false) Long teacherId
    ) {
        return ResponseEntity.ok(attendanceService.getWeekAttendance(startDate, teacherId));
    }

    @GetMapping("/teachers")
    public ResponseEntity<?> getAttendanceTeachers() {
        return ResponseEntity.ok(attendanceService.getAttendanceTeachers());
    }

    @PostMapping(value = "/mark", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> markAttendance(
            @RequestParam("slotId") Long slotId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("status") String status,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {
        attendanceService.markAttendance(slotId, date, status, image);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/image")
    public ResponseEntity<?> deleteAttendanceImage(
            @RequestParam("slotId") Long slotId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) throws IOException {
        attendanceService.deleteAttendanceImage(slotId, date);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/slot-instance")
    public ResponseEntity<?> deleteSlotInstance(
            @RequestParam("slotId") Long slotId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        attendanceService.deleteSlotInstance(slotId, date);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/slot-adhoc")
    public ResponseEntity<?> createAdHocSlot(@RequestBody AdHocSlotRequest request) {
        attendanceService.createAdHocSlot(request);
        return ResponseEntity.ok().build();
    }
}