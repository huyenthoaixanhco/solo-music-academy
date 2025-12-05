package com.solo.solo_music_academy.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTeacherRequest {

    // KHÔNG cho đổi username/password ở đây cho đơn giản
    private String fullName;
    private String email;
    private String phone;

    private String instrument;
    private String gender;
    private String position;
    private String teachingType;

    private String status;
    private String note;
}
