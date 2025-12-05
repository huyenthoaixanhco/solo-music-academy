package com.solo.solo_music_academy.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadCareHistoryResponse {

    private Long id;
    private Long leadId;
    private String leadName;         // tên bé hoặc "PH: ..."

    private Long supportUserId;
    private String supportFullName;

    private LocalDateTime careTime;
    private String careType;
    private String channel;
    private String content;
    private String result;
    private boolean important;

    private LocalDateTime nextCareTime;
}
