package com.solo.solo_music_academy.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherListResponse {

    private Long id;        // id trong bảng teachers
    private Long userId;    // id user liên kết

    private String username;
    private String fullName;
    private String email;
    private String phone;

    private String instrument;    // Nhạc cụ
    private String gender;        // MALE/FEMALE/OTHER
    private String position;      // JUNIOR/SENIOR/HEADTEACHER
    private String teachingType;  // FULL_TIME/PART_TIME

    private String status;        // ACTIVE/TEMP_STOP/QUIT
    private String note;          // Ghi chú
}
