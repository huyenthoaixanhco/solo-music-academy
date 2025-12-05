package com.solo.solo_music_academy.repository;

import com.solo.solo_music_academy.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Lấy toàn bộ tin nhắn của 1 học viên (support ↔ student)
    List<ChatMessage> findByStudentIdOrderBySentAtAsc(Long studentId);
}
