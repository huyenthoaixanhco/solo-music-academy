package com.solo.solo_music_academy.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lesson_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Học viên
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    // Giáo viên
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    // Thứ trong tuần: 1 = Monday, 7 = Sunday
    private int dayOfWeek;

    // Giờ bắt đầu / kết thúc, format "HH:mm"
    private String startTime;
    private String endTime;

    // Phòng / địa điểm
    private String room;

    // ACTIVE / PAUSED / CANCELLED
    private String status;
}
