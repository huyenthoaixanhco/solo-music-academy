package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.ChatMessageResponse;
import com.solo.solo_music_academy.dto.ChatSendRequest;
import com.solo.solo_music_academy.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/support/chat")
@RequiredArgsConstructor
public class SupportChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;  // 👈 THÊM

    // SUPPORT xem cuộc hội thoại với 1 học viên
    // GET /support/chat/student/{studentId}
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ChatMessageResponse>> getConversation(
            @PathVariable Long studentId
    ) {
        return ResponseEntity.ok(chatService.getConversation(studentId));
    }

    // SUPPORT gửi tin cho học viên
    // POST /support/chat/student/{studentId}
    @PostMapping("/student/{studentId}")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable Long studentId,
            @RequestBody ChatSendRequest req
    ) {
        // 1. Lưu DB + build DTO cho "current user" (SUPPORT)
        ChatMessageResponse saved = chatService.sendMessage(studentId, req.getContent());

        // 2. Build DTO để đẩy cho HỌC VIÊN (mine = false từ góc nhìn student)
        ChatMessageResponse pushToStudent = ChatMessageResponse.builder()
                .id(saved.getId())
                .studentId(saved.getStudentId())
                .senderId(saved.getSenderId())
                .senderName(saved.getSenderName())
                .senderRole(saved.getSenderRole())
                .receiverId(saved.getReceiverId())
                .receiverName(saved.getReceiverName())
                .content(saved.getContent())
                .sentAt(saved.getSentAt())
                .mine(false)              // 👈 bên student KHÔNG phải người gửi
                .read(false)              // hoặc saved.getRead(), tuỳ m
                .build();

        // 3. Bắn WebSocket tới tất cả client đang subscribe topic này
        String destination = "/topic/chat/student/" + studentId;
        messagingTemplate.convertAndSend(destination, pushToStudent);

        // 4. Trả saved cho FE support (mine = true)
        return ResponseEntity.ok(saved);
    }
}
