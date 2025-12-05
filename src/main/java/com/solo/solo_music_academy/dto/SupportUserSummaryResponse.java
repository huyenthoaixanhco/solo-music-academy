package com.solo.solo_music_academy.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportUserSummaryResponse {

    private Long id;
    private String fullName;
    private String phone;
    private String email;

    // tổng số học viên đang phụ trách
    private Long totalStudents;

    // tổng số lần CSKH đã ghi nhận
    private Long totalCareRecords;

    // lần CSKH gần nhất
    private LocalDateTime lastCareTime;
}
