package com.solo.solo_music_academy.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {

    private Long id;
    private Long studentId;

    private Long senderId;
    private String senderName;
    private String senderRole; // "STUDENT" hoặc "SUPPORT"

    private Long receiverId;
    private String receiverName;

    private String content;
    private String sentAt;     // ISO string
    private Boolean mine;      // true nếu do current user gửi

    private Boolean read;      // 👈 THÊM: trạng thái đã đọc

}
