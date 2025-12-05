package com.solo.solo_music_academy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "leads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Phụ huynh
    @Column(name = "parent_name")
    private String parentName;

    @Column(name = "parent_phone")
    private String parentPhone;

    @Column(name = "parent_email")
    private String parentEmail;

    // Bé (nếu có)
    @Column(name = "student_name")
    private String studentName;

    @Column(name = "student_age")
    private Integer studentAge;

    // Nhu cầu
    @Column(name = "instrument")
    private String instrument;

    @Column(name = "lesson_type")
    private String lessonType;

    @Column(name = "level")
    private String level;

    @Column(name = "preferred_schedule")
    private String preferredSchedule;

    // Nguồn khách
    @Column(name = "source")
    private String source;

    // Trạng thái pipeline lead
    @Column(name = "status")
    private String status;   // NEW / CONTACTED / ...

    // CSKH phụ trách = User có role SUPPORT
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_user_id")
    private User supportUser;

    // Thời gian
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_care_time")
    private LocalDateTime lastCareTime;

    @Column(name = "next_care_time")
    private LocalDateTime nextCareTime;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
}
