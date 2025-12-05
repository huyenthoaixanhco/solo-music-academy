package com.solo.solo_music_academy.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_package_slots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentPackageSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Gói học cha
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_package_id", nullable = false)
    private StudentPackage studentPackage;

    // Slot lịch tuần
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private TeacherScheduleSlot slot;
}
