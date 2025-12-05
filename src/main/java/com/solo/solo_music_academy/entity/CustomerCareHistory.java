package com.solo.solo_music_academy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_care_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerCareHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Học viên được chăm sóc
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    // Nhân viên support chăm sóc
    @ManyToOne
    @JoinColumn(name = "support_user_id")
    private User supportUser;

    // Thời điểm chăm sóc
    private LocalDateTime careTime;

    // Loại chăm sóc (gọi điện, nhắn tin, gặp trực tiếp,...)
    private String careType;

    // Kênh (Zalo, Facebook, Phone, Offline,...)
    private String channel;

    @Column(columnDefinition = "TEXT")
    private String content;  // Nội dung trao đổi

    private String result;   // Kết quả buổi chăm sóc

    private Boolean important; // Đánh dấu quan trọng

    private LocalDateTime nextCareTime; // Lịch chăm sóc tiếp theo
}
