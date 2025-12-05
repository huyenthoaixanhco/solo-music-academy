package com.solo.solo_music_academy.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentOfSupportResponse {

    // Dùng id để FE dùng trực tiếp s.id
    private Long id;                    // = studentId

    private String fullName;            // tên học viên
    private String phone;               // SĐT học viên

    // Phụ huynh
    private String parentName;
    private String parentPhone;
    private String parentEmail;

    // Thông tin lớp / môn
    private String instrument;          // ví dụ: Piano cơ bản (lấy từ Course nếu có)
    private String lessonType;          // 1-1 / NHÓM / ONLINE / ...
    private String scheduleText;        // mô tả lịch

    private String status;              // ACTIVE / PAUSED / STOPPED / ...

    // Tiến độ
    private Integer totalSessions;
    private Integer completedSessions;
    private Integer remainingSessions;

    // Giáo viên chính
    private String mainTeacherName;

    // ===== Thông tin học phí / nhắc hạn =====
    // Học phí gói hiện tại (VND)
    private Long tuitionAmount;

    // Ngày đã đóng gần nhất (từ StudentPackage.tuitionPaidDate hoặc Student.tuitionPaidDate)
    private LocalDate tuitionPaidDate;

    // Hạn đóng học phí (lấy từ gói: currentPeriodEnd hoặc field riêng)
    private LocalDate tuitionDueDate;

    // Trạng thái học phí raw từ entity gói: NOT_PAID / PARTIALLY_PAID / PAID / OVERDUE
    private String tuitionStatus;

    /**
     * Trạng thái nhắc nhở cho FE:
     *  - NO_PACKAGE  : chưa có gói học
     *  - NO_TUITION  : gói không cấu hình học phí
     *  - PAID        : đã đóng
     *  - NOT_PAID    : chưa đóng, còn xa hạn
     *  - DUE_SOON    : sắp đến hạn (<= 7 ngày)
     *  - OVERDUE     : quá hạn
     */
    private String tuitionReminderStatus;

    // Số ngày từ hôm nay đến hạn ( >0: còn, 0: hôm nay, <0: trễ)
    private Integer daysToDue;

    // ===== Thống kê CSKH =====
    private Long careRecordCount;
    private LocalDateTime lastCareTime;
}
