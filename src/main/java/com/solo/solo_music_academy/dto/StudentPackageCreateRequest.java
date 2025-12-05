package com.solo.solo_music_academy.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class StudentPackageCreateRequest {

    private Long studentId;
    private Long teacherId;
    private Long courseId;

    // NHIỀU lịch trong tuần
    private List<WeeklyScheduleCreateRequest> schedules;

    // Contact
    private String parentName;
    private String parentContact;
    private String contactChannel;  // Zalo / Whatsapp / ...

    // Hình thức tiết học
    private String lessonForm;

    // Thời gian học cũ / mới
    private LocalDate oldPeriodStart;
    private LocalDate oldPeriodEnd;

    private LocalDate currentPeriodStart;
    private LocalDate currentPeriodEnd;

    // ====== HỌC PHÍ ======
    // Số tiền học phí cho gói này (nếu null thì BE sẽ lấy từ Course.price nếu có)
    private Long tuitionAmount;

    // Hạn phải đóng học phí (dùng để nhắc nhở)
    private LocalDate tuitionDueDate;

    // Ngày thực tế đã đóng (nếu nhập luôn)
    private LocalDate tuitionPaidDate;

    private Integer totalSessions;
    private Integer sessionsCompleted;

    private String note;
}
