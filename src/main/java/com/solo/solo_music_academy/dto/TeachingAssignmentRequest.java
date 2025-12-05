package com.solo.solo_music_academy.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeachingAssignmentRequest {

    private Long studentId;   // id Student
    private Long teacherId;   // id Teacher

    private int dayOfWeek;    // 1-7
    private String startTime; // "HH:mm"
    private String endTime;   // "HH:mm"
    private String room;
    private String status;    // ACTIVE / PAUSED / CANCELLED
}
