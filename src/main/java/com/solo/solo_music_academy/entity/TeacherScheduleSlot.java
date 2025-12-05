package com.solo.solo_music_academy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Table(name = "teacher_schedule_slots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherScheduleSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Giáo viên dạy slot này
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;  // FIXME: dùng entity Teacher thật của cậu

    /**
     * Thứ trong tuần: 1 = Monday, ..., 7 = Sunday
     * Khớp với LocalDate.getDayOfWeek().getValue()
     */
    @Column(name = "day_of_week", nullable = false)
    private int dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    /**
     * Ghi chú nhanh: tên nhóm / loại buổi / phòng / học viên tiêu biểu
     * VD: "Hannah (Piano 1-1)", "Group Piano Beginner"
     */
    @Column(name = "note")
    private String note;
}
