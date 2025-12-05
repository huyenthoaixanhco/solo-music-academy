package com.solo.solo_music_academy.dto;

import com.solo.solo_music_academy.entity.Course;
import com.solo.solo_music_academy.entity.StudentPackage;
import com.solo.solo_music_academy.entity.StudentPackageSlot;
import com.solo.solo_music_academy.entity.TeacherScheduleSlot;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentPackageResponse {

    private Long id;

    // ====== ID để FE prefill combobox ======
    private Long studentId;
    private Long teacherId;
    private Long courseId;

    // ====== Tên hiển thị ======
    private String studentName;
    private String teacherName;
    private String courseName;

    // ====== Thông tin phụ huynh / liên hệ ======
    private String parentName;
    private String parentContact;
    private String contactChannel;

    // ====== Hình thức tiết học ======
    private String lessonForm;

    // ====== Thời gian gói ======
    private LocalDate oldPeriodStart;
    private LocalDate oldPeriodEnd;
    private LocalDate currentPeriodStart;
    private LocalDate currentPeriodEnd;

    // ====== Học phí ======
    private Long tuitionAmount;
    private LocalDate tuitionDueDate;
    private LocalDate tuitionPaidDate;
    private String tuitionStatus;   // NOT_PAID / PAID / ...

    // ====== Số buổi ======
    private Integer totalSessions;
    private Integer sessionsCompleted;
    private Integer sessionsRemaining;

    // ====== Trạng thái gói ======
    private String status;   // ACTIVE / COMPLETED / ...

    private String note;

    // Nhiều lịch
    private List<ScheduleInfo> schedules;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ScheduleInfo {
        private Integer dayOfWeek;
        private String startTime;
        private String endTime;
        private String slotNote;
    }

    public static StudentPackageResponse fromEntity(StudentPackage pkg) {
        List<ScheduleInfo> scheduleDtos = pkg.getPackageSlots().stream()
                .map(StudentPackageResponse::fromPackageSlot)
                .toList();

        Course course = pkg.getCourse();

        return StudentPackageResponse.builder()
                .id(pkg.getId())

                // ID cho FE
                .studentId(pkg.getStudent() != null ? pkg.getStudent().getId() : null)
                .teacherId(pkg.getTeacher() != null ? pkg.getTeacher().getId() : null)
                .courseId(course != null ? course.getId() : null)

                // Tên hiển thị
                .studentName(pkg.getStudent() != null && pkg.getStudent().getUser() != null
                        ? pkg.getStudent().getUser().getFullName()
                        : null)
                .teacherName(pkg.getTeacher() != null && pkg.getTeacher().getUser() != null
                        ? pkg.getTeacher().getUser().getFullName()
                        : null)
                .courseName(course != null ? course.getName() : null)

                // Thông tin phụ huynh
                .parentName(pkg.getParentName())
                .parentContact(pkg.getParentContact())
                .contactChannel(pkg.getContactChannel())

                // Hình thức
                .lessonForm(pkg.getLessonForm())

                // Thời gian gói
                .oldPeriodStart(pkg.getOldPeriodStart())
                .oldPeriodEnd(pkg.getOldPeriodEnd())
                .currentPeriodStart(pkg.getCurrentPeriodStart())
                .currentPeriodEnd(pkg.getCurrentPeriodEnd())

                // Học phí
                .tuitionAmount(pkg.getTuitionAmount())
                .tuitionDueDate(pkg.getTuitionDueDate())
                .tuitionPaidDate(pkg.getTuitionPaidDate())
                .tuitionStatus(pkg.getTuitionStatus())

                // Số buổi
                .totalSessions(pkg.getTotalSessions())
                .sessionsCompleted(pkg.getSessionsCompleted())
                .sessionsRemaining(pkg.getSessionsRemaining())

                // Trạng thái gói
                .status(pkg.getStatus() != null ? pkg.getStatus().name() : null)

                .note(pkg.getNote())
                .schedules(scheduleDtos)
                .build();
    }

    private static ScheduleInfo fromPackageSlot(StudentPackageSlot ps) {
        TeacherScheduleSlot slot = ps.getSlot();
        return ScheduleInfo.builder()
                .dayOfWeek(slot.getDayOfWeek())
                .startTime(slot.getStartTime().toString())
                .endTime(slot.getEndTime().toString())
                .slotNote(slot.getNote())
                .build();
    }
}
