package com.solo.solo_music_academy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "student_packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Học viên
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Giáo viên phụ trách gói này
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    // ====== 1 gói có thể có nhiều slot ======
    @OneToMany(
            mappedBy = "studentPackage",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<StudentPackageSlot> packageSlots = new ArrayList<>();

    // ====== Contact phụ huynh ======
    @Column(name = "parent_name")
    private String parentName;

    @Column(name = "parent_contact")
    private String parentContact; // phone/email

    @Column(name = "contact_channel")
    private String contactChannel; // Zalo / Whatsapp / Instagram / ...

    // Hình thức tiết học
    @Column(name = "lesson_form")
    private String lessonForm;

    // Thời gian học cũ
    @Column(name = "old_period_start")
    private LocalDate oldPeriodStart;

    @Column(name = "old_period_end")
    private LocalDate oldPeriodEnd;

    // Thời gian học hiện tại / mới
    @Column(name = "current_period_start")
    private LocalDate currentPeriodStart;

    @Column(name = "current_period_end")
    private LocalDate currentPeriodEnd;

    // ====== HỌC PHÍ CHO GÓI NÀY ======

    // Số tiền học phí (VND) cho gói này, ví dụ 6_000_000
    @Column(name = "tuition_amount")
    private Long tuitionAmount;

    // Hạn phải đóng học phí (dùng cho nhắc nhở / OVERDUE)
    @Column(name = "tuition_due_date")
    private LocalDate tuitionDueDate;

    // Ngày nộp học phí khoá mới (đã đóng đủ)
    @Column(name = "tuition_paid_date")
    private LocalDate tuitionPaidDate;

    // Trạng thái học phí: NOT_PAID | PARTIALLY_PAID | PAID | OVERDUE
    @Column(name = "tuition_status", length = 20)
    private String tuitionStatus;

    // ====== Tổng số buổi / đã học / còn lại ======
    @Column(name = "total_sessions")
    private Integer totalSessions;

    @Column(name = "sessions_completed")
    private Integer sessionsCompleted;

    @Column(name = "sessions_remaining")
    private Integer sessionsRemaining;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private StudentPackageStatus status;

    @Column(columnDefinition = "TEXT")
    private String note;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @PrePersist
    @PreUpdate
    public void syncSessionNumbers() {
        // Đồng bộ số buổi
        if (totalSessions == null) totalSessions = 0;
        if (sessionsCompleted == null) sessionsCompleted = 0;
        if (sessionsRemaining == null) {
            sessionsRemaining = totalSessions - sessionsCompleted;
        }

        // Trạng thái gói học (ACTIVE / COMPLETED) dựa trên số buổi
        if (status == null) {
            status = sessionsCompleted >= totalSessions
                    ? StudentPackageStatus.COMPLETED
                    : StudentPackageStatus.ACTIVE;
        }

        // Default trạng thái học phí nếu có đặt tuitionAmount mà chưa set status
        if (tuitionAmount != null && tuitionStatus == null) {
            tuitionStatus = "NOT_PAID";
        }
    }
    
}
