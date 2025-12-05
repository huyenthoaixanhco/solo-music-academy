package com.solo.solo_music_academy.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentListResponse {

    private Long id;         // id student
    private Long userId;     // id user
    private String username;
    private String fullName;
    private String email;
    private String phone;

    private String parentName;
    private String parentPhone;
    private String parentEmail;     // NEW

    private String lessonType;
    private String scheduleText;

    private Integer totalSessions;
    private Integer completedSessions;
    private Integer remainingSessions;

    private String status;

    private Long courseId;
    private String courseName;

    private String mainTeacherName;  // tên GV chính
    private String careStaffName;    // tên CSKH (user)

    private String note;             // NEW
}
