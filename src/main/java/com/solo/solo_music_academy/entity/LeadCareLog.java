package com.solo.solo_music_academy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lead_care_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadCareLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lead được chăm
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;

    // CSKH thực hiện = User role SUPPORT
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_user_id", nullable = false)
    private User supportUser;

    @Column(name = "care_time")
    private LocalDateTime careTime;

    @Column(name = "care_type")
    private String careType;   // vẫn lưu DB nhưng FE không show

    @Column(name = "channel")
    private String channel;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "result", columnDefinition = "TEXT")
    private String result;

    @Column(name = "important")
    private boolean important;

    @Column(name = "next_care_time")
    private LocalDateTime nextCareTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
