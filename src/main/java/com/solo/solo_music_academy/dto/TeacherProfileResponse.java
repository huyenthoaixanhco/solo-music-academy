package com.solo.solo_music_academy.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherProfileResponse {

    private Long id;        // id teacher
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;

    private String instrument;    // Piano / Guitar...
    private String gender;
    private String position;      // JUNIOR / SENIOR / HEADTEACHER
    private String teachingType;  // FULL_TIME / PART_TIME
    private String status;
    private String note;
}
