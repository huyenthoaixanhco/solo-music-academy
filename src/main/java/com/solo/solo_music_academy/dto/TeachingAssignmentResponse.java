package com.solo.solo_music_academy.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeachingAssignmentResponse {

    private Long id;          // id LessonSchedule

    private Long studentId;
    private String studentName;

    private Long teacherId;
    private String teacherName;

    private int dayOfWeek;
    private String startTime;   // "HH:mm"
    private String endTime;     // "HH:mm"
    private String room;
    private String status;
}
