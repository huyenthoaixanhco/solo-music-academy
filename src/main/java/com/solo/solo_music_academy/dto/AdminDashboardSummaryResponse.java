package com.solo.solo_music_academy.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardSummaryResponse {

    // ==== HỌC VIÊN ====
    private long totalStudents;
    private long activeStudents;
    private long pausedStudents;
    private long stoppedStudents;
    private long unassignedStudents;        // chưa gán CSKH
    private long almostFinishedStudents;    // còn <= 2 buổi

    // ==== GIÁO VIÊN ====
    private long totalTeachers;
    private long activeTeachers;

    // ==== CSKH (SUPPORT) ====
    private long totalSupportUsers;

    // ==== KHÓA HỌC ====
    private long totalCourses;
    private long activeCourses;

    // ==== CSKH / NHẮC VIỆC ====
    private long todayReminders;            // số lịch CSKH có nextCareTime hôm nay
}
