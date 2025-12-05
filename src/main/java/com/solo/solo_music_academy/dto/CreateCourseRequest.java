package com.solo.solo_music_academy.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCourseRequest {

    private String code;
    private String name;
    private String description;

    private String instrument;   // Piano / Guitar / Vocal...
    private String level;        // BEGINNER / INTERMEDIATE / ADVANCED

    private Integer totalSessions;
    private BigDecimal tuitionFee;

    private String status;       // ACTIVE / INACTIVE
    private String note;
}
