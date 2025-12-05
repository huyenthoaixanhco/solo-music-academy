package com.solo.solo_music_academy.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerCareHistoryResponse {

    private Long id;

    private Long studentId;
    private String studentName;

    private Long supportUserId;
    private String supportFullName;

    private String careTime;
    private String careType;
    private String channel;
    private String content;
    private String result;
    private Boolean important;
    private String nextCareTime;
}
