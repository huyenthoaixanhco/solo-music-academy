package com.solo.solo_music_academy.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStudentRequest {

    // Account cho học viên
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;

    // Profile học viên
    private Long mainTeacherId;      // id Teacher, có thể null
    private Long careStaffUserId;    // user id của CSKH, có thể null
    private Long courseId;           // id Course, có thể null  👈 THÊM

    private String parentName;
    private String parentPhone;
    private String parentEmail;

    private String lessonType;
    private String scheduleText;
    private String currentTimeSlot;
    private String newTimeSlot;

    private LocalDate tuitionPaidDate;
    private Integer totalSessions;
    private Integer completedSessions;
    private Integer remainingSessions;

    private String status;
    private String note;
}
