package com.solo.solo_music_academy.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseListResponse {

    private Long id;
    private String code;
    private String name;

    private String instrument;
    private String level;

    private Integer totalSessions;
    private BigDecimal tuitionFee;

    private String status;
}
