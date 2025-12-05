package com.solo.solo_music_academy.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceSlotResponse {

    private Long slotId;
    private Long attendanceId;    // null nếu chưa chấm

    private int dayOfWeek;        // 1..7
    private LocalDate date;       // ngày thực tế của tuần

    private String startTime;     // "10:00"
    private String endTime;       // "10:45"

    private Long teacherId;
    private String teacherName;

    // ✅ THÊM DÒNG NÀY ĐỂ HIỆN TÊN HỌC VIÊN
    private String studentName;

    private String status;        // PRESENT / ABSENT / null
    private boolean hasImage;     // true nếu có ảnh
    private String imageUrl;      // URL ảnh (nếu có)
    private boolean isAdHoc;  // Có phải lịch học bù ???
}