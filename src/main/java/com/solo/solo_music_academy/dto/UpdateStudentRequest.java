package com.solo.solo_music_academy.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStudentRequest {

    private String fullName;
    private String email;
    private String phone;

    private String parentName;
    private String parentPhone;
    private String parentEmail;

    private String status;
    private String note;
}
