package com.solo.solo_music_academy.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfileResponse {

    private Long id;        // id student
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;

    // Thông tin phụ huynh
    private String parentName;
    private String parentPhone;
    private String parentEmail;

    // Học tập
    private String lessonType;       // 1-1 / NHÓM / ONLINE...
    private String scheduleText;     // mô tả lịch
    private String currentTimeSlot;  // khung giờ hiện tại
    private String newTimeSlot;      // nếu đề xuất đổi ca

    // Học phí / tiến độ (legacy từ Student hoặc override bởi gói học)
    private LocalDate tuitionPaidDate;
    private Integer totalSessions;
    private Integer completedSessions;
    private Integer remainingSessions;

    private String status;
    private String note;

    // Liên quan giáo viên & CSKH
    private Long mainTeacherId;
    private String mainTeacherName;

    private Long careStaffUserId;
    private String careStaffName;

    // ====== MỚI: Thông tin gói học & nhắc học phí ======

    // Id gói học đang active / mới nhất
    private Long activePackageId;

    // Học phí gói hiện tại (VND) – lấy từ StudentPackage.tuitionAmount
    private Long tuitionAmount;

    // Hạn đóng học phí, dùng cho nhắc nhở
    private LocalDate tuitionDueDate;

    // Trạng thái học phí thô trong gói: NOT_PAID / PARTIALLY_PAID / PAID / OVERDUE (từ entity)
    private String tuitionStatus;

    /**
     * Trạng thái nhắc nhở để FE show banner:
     *  - NO_PACKAGE  : chưa có gói học
     *  - NO_TUITION  : gói không cấu hình học phí
     *  - PAID        : đã đóng
     *  - NOT_PAID    : chưa đóng, còn xa hạn
     *  - DUE_SOON    : sắp đến hạn (<= 7 ngày)
     *  - OVERDUE     : quá hạn
     */
    private String tuitionReminderStatus;

    // Số ngày từ hôm nay đến hạn:
    //  >0  : còn bao nhiêu ngày
    //   0  : hôm nay là hạn
    //  <0  : trễ bao nhiêu ngày
    private Integer daysToDue;
}
