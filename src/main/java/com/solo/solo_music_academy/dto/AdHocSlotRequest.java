package com.solo.solo_music_academy.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AdHocSlotRequest {
    private Long teacherId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String studentName;
}