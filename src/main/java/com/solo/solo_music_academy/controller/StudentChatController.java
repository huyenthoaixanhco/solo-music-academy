package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.ChatMessageResponse;
import com.solo.solo_music_academy.dto.ChatSendRequest;
import com.solo.solo_music_academy.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student/chat")
@RequiredArgsConstructor
public class StudentChatController {

    private final ChatService chatService;

    // STUDENT xem cuộc hội thoại với CSKH của mình
    // GET /student/chat/{studentId}
    @GetMapping("/{studentId}")
    public ResponseEntity<List<ChatMessageResponse>> getConversation(
            @PathVariable Long studentId
    ) {
        return ResponseEntity.ok(chatService.getConversation(studentId));
    }

    // STUDENT gửi tin cho CSKH
    // POST /student/chat/{studentId}
    @PostMapping("/{studentId}")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable Long studentId,
            @RequestBody ChatSendRequest req
    ) {
        return ResponseEntity.ok(
                chatService.sendMessage(studentId, req.getContent())
        );
    }
}
