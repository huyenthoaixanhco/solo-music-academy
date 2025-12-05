package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.ChatMessageResponse;
import com.solo.solo_music_academy.dto.ChatSendRequest;
import com.solo.solo_music_academy.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWsController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Support / Student gửi tin:
     * SEND tới: /app/chat/student/{studentId}
     * body: { "content": "..." }
     *
     * Server:
     *  - validate + lưu DB qua ChatService
     *  - broadcast tin nhắn mới ra channel: /topic/chat/student/{studentId}
     */
    @MessageMapping("/chat/student/{studentId}")
    public void handleChatMessage(
            @DestinationVariable Long studentId,
            ChatSendRequest payload
    ) {
        // Gửi + lưu DB (dùng ChatService)
        ChatMessageResponse saved = chatService.sendMessage(studentId, payload.getContent());

        // Gửi lại cho tất cả client đang subscribe room này
        String destination = "/topic/chat/student/" + studentId;
        messagingTemplate.convertAndSend(destination, saved);
    }
}
