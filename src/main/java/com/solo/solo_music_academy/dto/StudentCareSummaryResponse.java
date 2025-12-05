package com.solo.solo_music_academy.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCareSummaryResponse {

    // id để FE dùng trực tiếp
    private Long id;                    // = studentId

    private String fullName;            // tên học viên

    // Thông tin lớp / môn
    private String instrument;          // ví dụ: Piano cơ bản (lấy từ Course nếu có)

    // Phụ huynh
    private String parentName;
    private String parentPhone;

    // Thông tin lịch học
    private String lessonType;          // 1-1 / NHÓM / ONLINE / ...
    private String scheduleText;        // mô tả lịch

    private String status;              // ACTIVE / PAUSED / STOPPED / ...

    // Tiến độ
    private Integer totalSessions;
    private Integer completedSessions;
    private Integer remainingSessions;

    // Giáo viên chính
    private String mainTeacherName;

    // ===== HỌC PHÍ / NHẮC HẬN HỌC PHÍ =====
    private Long tuitionAmount;         // VND

    private LocalDate tuitionPaidDate;  // ngày đã đóng gần nhất
    private LocalDate tuitionDueDate;   // hạn đóng

    private String tuitionStatus;       // NOT_PAID / PARTIALLY_PAID / PAID / OVERDUE

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
}
