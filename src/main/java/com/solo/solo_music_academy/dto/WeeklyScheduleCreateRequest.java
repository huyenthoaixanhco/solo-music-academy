package com.solo.solo_music_academy.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeeklyScheduleCreateRequest {

    private Integer dayOfWeek;   // 1..7
    private String startTime;    // "HH:mm"
    private String endTime;      // "HH:mm"
    private String slotNote;     // VD: "Hannah – Piano 1-1"
}
