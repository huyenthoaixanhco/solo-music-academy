package com.solo.solo_music_academy.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareReminderResponse {

    private Long historyId;

    private Long studentId;
    private String studentName;

    private String careType;       // VD: REMINDER_LESSON / REMINDER_TUITION / ...
    private String channel;        // ZALO / CALL / ...
    private String content;        // ghi chú ngắn về việc cần làm
    private Boolean important;

    private String nextCareTime;   // thời điểm cần nhắc (ISO string)
}
