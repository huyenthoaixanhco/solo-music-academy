package com.solo.solo_music_academy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Học viên của cuộc chat
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    // Người gửi
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    // Người nhận
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime sentAt;

    @Column(name = "is_read")
    private Boolean read; // sau này FE có thể update đọc/chưa đọc
}
