package com.solo.solo_music_academy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mã khóa học: VD: PIANO_BASIC, GUITAR_ADV
    @Column(unique = true, nullable = false)
    private String code;

    // Tên khóa học: "Piano cơ bản 1-1", ...
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Nhạc cụ chính: Piano / Guitar / Vocal...
    private String instrument;

    // Trình độ: BEGINNER / INTERMEDIATE / ADVANCED
    private String level;

    // Tổng số buổi của khóa
    private Integer totalSessions;

    // ⭐ Học phí trọn khóa (VND)
    @Column(name = "tuition_fee")
    private BigDecimal tuitionFee;

    // ACTIVE / INACTIVE
    private String status;

    private String note;
}
