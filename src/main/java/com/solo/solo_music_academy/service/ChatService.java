package com.solo.solo_music_academy.service;

import com.solo.solo_music_academy.dto.ChatMessageResponse;
import com.solo.solo_music_academy.entity.ChatMessage;
import com.solo.solo_music_academy.entity.Student;
import com.solo.solo_music_academy.entity.User;
import com.solo.solo_music_academy.repository.ChatMessageRepository;
import com.solo.solo_music_academy.repository.StudentRepository;
import com.solo.solo_music_academy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatRepo;
    private final StudentRepository studentRepo;
    private final UserRepository userRepo;

    // Gửi tin (dùng chung cho support + student)
    public ChatMessageResponse sendMessage(Long studentId, String content) {
        if (content == null || content.isBlank()) {
            throw new RuntimeException("Nội dung không được để trống");
        }

        User current = getCurrentUser();
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Xác định role & validate quyền
        boolean isSupport = hasRole(current, "ROLE_SUPPORT");
        boolean isStudent = hasRole(current, "ROLE_STUDENT");

        User receiver;

        if (isSupport) {
            // Support chỉ được chat với học viên mình phụ trách
            if (student.getCareStaff() == null
                    || !student.getCareStaff().getId().equals(current.getId())) {
                throw new RuntimeException("Bạn không phụ trách học viên này");
            }
            receiver = student.getUser();
        } else if (isStudent) {
            // Student chỉ được chat với chính mình
            if (student.getUser() == null
                    || !student.getUser().getId().equals(current.getId())) {
                throw new RuntimeException("Bạn không có quyền chat trong cuộc hội thoại này");
            }
            if (student.getCareStaff() == null) {
                throw new RuntimeException("Học viên chưa được gán nhân viên CSKH");
            }
            receiver = student.getCareStaff();
        } else {
            throw new RuntimeException("Role hiện tại không được phép chat");
        }

        ChatMessage msg = ChatMessage.builder()
                .student(student)
                .sender(current)
                .receiver(receiver)
                .content(content)
                .sentAt(LocalDateTime.now())
                .read(false)
                .build();

        chatRepo.save(msg);

        return mapToResponse(msg, current);
    }

    // Lấy toàn bộ cuộc hội thoại của 1 học viên (support hoặc student đều dùng)
    public List<ChatMessageResponse> getConversation(Long studentId) {
    User current = getCurrentUser();
    Student student = studentRepo.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));

    boolean isSupport = hasRole(current, "ROLE_SUPPORT");
    boolean isStudent = hasRole(current, "ROLE_STUDENT");

    // Check quyền
    if (isSupport) {
        if (student.getCareStaff() == null
                || !student.getCareStaff().getId().equals(current.getId())) {
            throw new RuntimeException("Bạn không phụ trách học viên này");
        }
    } else if (isStudent) {
        if (student.getUser() == null
                || !student.getUser().getId().equals(current.getId())) {
            throw new RuntimeException("Bạn không được xem cuộc hội thoại này");
        }
    } else {
        throw new RuntimeException("Role hiện tại không được phép xem hội thoại");
    }

    // Lấy toàn bộ tin nhắn của học viên này
    List<ChatMessage> list = chatRepo.findByStudentIdOrderBySentAtAsc(studentId);

    // ===== ĐÁNH DẤU ĐÃ ĐỌC =====
    List<ChatMessage> needUpdate = list.stream()
            .filter(m ->
                    Boolean.FALSE.equals(m.getRead())           // chưa đọc
                    && m.getReceiver() != null
                    && m.getReceiver().getId().equals(current.getId()) // mình là người nhận
            )
            .toList();

    if (!needUpdate.isEmpty()) {
        needUpdate.forEach(m -> m.setRead(true));
        chatRepo.saveAll(needUpdate);
    }

    // Trả về DTO (đã update read)
    return list.stream()
            .map(msg -> mapToResponse(msg, current))
            .toList();
}


    // ===== helper =====
    private ChatMessageResponse mapToResponse(ChatMessage m, User current) {
        User sender = m.getSender();
        User receiver = m.getReceiver();

        String senderRole =
                sender.getRoles().stream().anyMatch(r -> "ROLE_SUPPORT".equals(r.getName()))
                        ? "SUPPORT"
                        : "STUDENT";

        boolean mine = sender.getId().equals(current.getId());

        return ChatMessageResponse.builder()
                .id(m.getId())
                .studentId(m.getStudent() != null ? m.getStudent().getId() : null)
                .senderId(sender != null ? sender.getId() : null)
                .senderName(sender != null ? sender.getFullName() : null)
                .senderRole(senderRole)
                .receiverId(receiver != null ? receiver.getId() : null)
                .receiverName(receiver != null ? receiver.getFullName() : null)
                .content(m.getContent())
                .sentAt(m.getSentAt() != null ? m.getSentAt().toString() : null)
                .mine(mine)
                .read(m.getRead())
                .build();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    private boolean hasRole(User user, String roleName) {
        return user.getRoles().stream()
                .anyMatch(r -> roleName.equals(r.getName()));
    }
}
