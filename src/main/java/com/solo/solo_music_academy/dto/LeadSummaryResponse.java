package com.solo.solo_music_academy.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadSummaryResponse {

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

    private String source;        // kênh khách
    private String status;        // NEW / CONTACTED / ...

    private LocalDateTime createdAt;
    private LocalDateTime lastCareTime;
    private LocalDateTime nextCareTime;
}
