package com.solo.solo_music_academy.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignSupportRequest {
    private Long studentId;      // id student
    private Long supportUserId;  // id user có ROLE_SUPPORT
}
