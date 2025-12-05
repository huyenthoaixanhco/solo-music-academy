package com.solo.solo_music_academy.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentTuitionOverviewResponse {

    private Long packageId;

    private Long tuitionAmount;        // Số tiền gói (VND)
    private String tuitionStatus;      // NOT_PAID | PARTIALLY_PAID | PAID | OVERDUE

    private String tuitionDueDate;     // yyyy-MM-dd (string cho FE dễ dùng)
    private String tuitionPaidDate;    // yyyy-MM-dd hoặc null

    private Integer totalSessions;
    private Integer completedSessions;
    private Integer remainingSessions;

    // daysToDue = dueDate - today (dương: còn n ngày, âm: quá hạn n ngày)
    private Integer daysToDue;
}
