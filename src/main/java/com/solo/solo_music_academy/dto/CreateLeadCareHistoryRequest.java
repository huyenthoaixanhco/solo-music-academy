package com.solo.solo_music_academy.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLeadCareHistoryRequest {

    private String careType;
    private String channel;
    private String content;
    private String result;
    private boolean important;

    private LocalDateTime nextCareTime;   // FE gửi ISO string, Jackson parse
}
