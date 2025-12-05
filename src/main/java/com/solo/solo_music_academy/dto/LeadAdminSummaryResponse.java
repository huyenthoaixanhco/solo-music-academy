package com.solo.solo_music_academy.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadAdminSummaryResponse {

    private Long id;

    private String parentName;
    private String parentPhone;
    private String parentEmail;

    private String studentName;
    private Integer studentAge;

    private String instrument;
    private String lessonType;
    private String level;
    private String preferredSchedule;

    private String source;
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime lastCareTime;
    private LocalDateTime nextCareTime;

    // CSKH được gán
    private Long supportUserId;
    private String supportFullName;
}
