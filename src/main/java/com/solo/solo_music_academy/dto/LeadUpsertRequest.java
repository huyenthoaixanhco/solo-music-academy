package com.solo.solo_music_academy.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadUpsertRequest {
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
    private String status; // NEW / CONTACTED / TRIAL_BOOKED / ...
}
