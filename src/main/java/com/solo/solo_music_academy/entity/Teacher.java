package com.solo.solo_music_academy.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teachers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết 1-1 với User (tài khoản đăng nhập)
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // NHẠC CỤ CHÍNH: Piano / Guitar / Vocal / Drum,...
    @Column(length = 50)
    private String instrument;

    // Giới tính: MALE / FEMALE / OTHER (để string cho đơn giản)
    @Column(length = 10)
    private String gender;

    // Vị trí: JUNIOR / SENIOR / HEADTEACHER
    @Column(length = 20)
    private String position;

    // Hình thức: FULL_TIME (cơ hữu) / PART_TIME (thỉnh giảng)
    @Column(length = 20)
    private String teachingType;

    // Trạng thái: ACTIVE / TEMP_STOP / QUIT
    @Column(length = 20)
    private String status;

    @Column(length = 255)
    private String note;
}
