package com.solo.solo_music_academy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết 1-1 với User (tài khoản học viên)
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Giáo viên chính của học viên (có thể null nếu chưa assign)
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher mainTeacher;

    // Tên phụ huynh
    @Column(length = 100)
    private String parentName;

    // SĐT phụ huynh
    @Column(length = 20)
    private String parentPhone;

    // Email phụ huynh
    @Column(length = 100)
    private String parentEmail;

    // Hình thức tiết học: 1-1 / NHÓM / ONLINE / OFFLINE
    @Column(length = 30)
    private String lessonType;

    // Lịch học (mô tả ngắn, ví dụ: "CN 9h-10h, thứ 4 19h-20h")
    @Column(length = 255)
    private String scheduleText;

    // Thời gian học hiện tại (ví dụ: "CN 9h-10h")
    @Column(length = 100)
    private String currentTimeSlot;

    // Thời gian học đề xuất / mới nếu có đổi
    @Column(length = 100)
    private String newTimeSlot;

    // Ngày nộp học phí cho khoá hiện tại
    private LocalDate tuitionPaidDate;

    // Tổng số buổi trong khoá
    private Integer totalSessions;

    // Đã học
    private Integer completedSessions;

    // Còn lại
    private Integer remainingSessions;

    // Trạng thái chung của học viên: ACTIVE / PAUSED / STOPPED
    @Column(length = 20)
    private String status;

    @Column(length = 255)
    private String note;

    // CSKH phụ trách học viên này (User có role ROLE_SUPPORT)
    @ManyToOne
    @JoinColumn(name = "support_user_id")
    private User careStaff;

    // Khóa học mà học viên đang theo
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
