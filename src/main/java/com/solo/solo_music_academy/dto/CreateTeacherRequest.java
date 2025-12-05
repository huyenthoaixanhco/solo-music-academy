package com.solo.solo_music_academy.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTeacherRequest {

    private String username;
    private String password;

    private String fullName;
    private String email;
    private String phone;

    private String instrument;
    private String gender;
    private String position;
    private String teachingType;

    private String status;   // ACTIVE / TEMP_STOP / QUIT
    private String note;
}
