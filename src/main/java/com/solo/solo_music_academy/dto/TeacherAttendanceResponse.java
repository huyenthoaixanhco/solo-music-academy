package com.solo.solo_music_academy.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherAttendanceResponse {

    private Long id;

    private Long teacherId;
    private String teacherName;

    private String date;       // yyyy-MM-dd
    private String status;     // PRESENT / ABSENT / LATE

    private String imagePath;  // đường dẫn file (relative)
    private String note;

    private String createdAt;  // ISO String
}
