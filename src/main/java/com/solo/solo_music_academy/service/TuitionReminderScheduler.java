package com.solo.solo_music_academy.service;

import com.solo.solo_music_academy.entity.CustomerCareHistory;
import com.solo.solo_music_academy.entity.Student;
import com.solo.solo_music_academy.repository.CustomerCareHistoryRepository;
import com.solo.solo_music_academy.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TuitionReminderScheduler {

    private final StudentRepository studentRepo;
    private final CustomerCareHistoryRepository careRepo;

    // Chạy mỗi ngày lúc 9h sáng
    @Scheduled(cron = "0 0 9 * * *")
    public void createTuitionReminders() {

        // Ví dụ: tìm học viên sắp hết buổi (remainingSessions <= 2)
        List<Student> students = studentRepo.findByRemainingSessionsLessThanEqual(2);

        for (Student s : students) {
            if (s.getCareStaff() == null) continue;

            CustomerCareHistory history = CustomerCareHistory.builder()
                    .student(s)
                    .supportUser(s.getCareStaff())
                    .careTime(LocalDateTime.now())
                    .careType("REMINDER_TUITION_AUTO")
                    .channel("SYSTEM")
                    .content("Nhắc học phí: học viên sắp hết buổi")
                    .result(null)
                    .important(true)
                    .nextCareTime(LocalDateTime.now().plusDays(1)) // ví dụ hẹn ngày mai gọi
                    .build();

            careRepo.save(history);
        }
    }
}
