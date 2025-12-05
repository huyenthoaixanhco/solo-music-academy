package com.solo.solo_music_academy.service;

import com.solo.solo_music_academy.dto.StudentCareSummaryResponse;
import com.solo.solo_music_academy.dto.StudentOfSupportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupportStudentService {

    private final SupportService supportService;

    /**
     * SUPPORT mở màn hình "Học viên của tôi"
     * -> gọi lại SupportService.getMyStudents() (đã tính học phí / hạn)
     * -> map sang DTO gọn cho FE.
     */
    public List<StudentCareSummaryResponse> getMyStudents() {
        List<StudentOfSupportResponse> list = supportService.getMyStudents();
        return list.stream()
                .map(this::mapToCareSummary)
                .toList();
    }

    private StudentCareSummaryResponse mapToCareSummary(StudentOfSupportResponse src) {
        return StudentCareSummaryResponse.builder()
                .id(src.getId())
                .fullName(src.getFullName())
                .instrument(src.getInstrument())
                .parentName(src.getParentName())
                .parentPhone(src.getParentPhone())
                .lessonType(src.getLessonType())
                .scheduleText(src.getScheduleText())
                .status(src.getStatus())
                .totalSessions(src.getTotalSessions())
                .completedSessions(src.getCompletedSessions())
                .remainingSessions(src.getRemainingSessions())
                .mainTeacherName(src.getMainTeacherName())

                // học phí / hạn
                .tuitionAmount(src.getTuitionAmount())
                .tuitionPaidDate(src.getTuitionPaidDate())
                .tuitionDueDate(src.getTuitionDueDate())
                .tuitionStatus(src.getTuitionStatus())
                .tuitionReminderStatus(src.getTuitionReminderStatus())
                .daysToDue(src.getDaysToDue())
                .build();
    }
}
