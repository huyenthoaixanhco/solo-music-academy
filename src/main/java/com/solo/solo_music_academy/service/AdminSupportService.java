package com.solo.solo_music_academy.service;

import com.solo.solo_music_academy.dto.StudentOfSupportResponse;
import com.solo.solo_music_academy.dto.SupportUserSummaryResponse;
import com.solo.solo_music_academy.entity.CustomerCareHistory;
import com.solo.solo_music_academy.entity.Student;
import com.solo.solo_music_academy.entity.StudentPackage;
import com.solo.solo_music_academy.entity.User;
import com.solo.solo_music_academy.repository.CustomerCareHistoryRepository;
import com.solo.solo_music_academy.repository.StudentPackageRepository;
import com.solo.solo_music_academy.repository.StudentRepository;
import com.solo.solo_music_academy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminSupportService {

    private final UserRepository userRepo;
    private final StudentRepository studentRepo;
    private final CustomerCareHistoryRepository careRepo;
    private final StudentPackageRepository studentPackageRepo;

    // ===== 1) ADMIN: TỔNG QUAN TỪNG SUPPORT =====
    public List<SupportUserSummaryResponse> getAllSupportSummary() {
        List<User> supports = userRepo.findAllSupportUsers();   // List<User>

        return supports.stream()
                .map(support -> {
                    Long totalStudents = studentRepo.countByCareStaffId(support.getId());
                    Long totalCareRecords = careRepo.countBySupportUserId(support.getId());

                    Optional<CustomerCareHistory> lastCareOpt =
                            careRepo.findTopBySupportUserIdOrderByCareTimeDesc(support.getId());
                    LocalDateTime lastCareTime =
                            lastCareOpt.map(CustomerCareHistory::getCareTime).orElse(null);

                    return SupportUserSummaryResponse.builder()
                            .id(support.getId())
                            .fullName(support.getFullName())
                            .phone(support.getPhone())
                            .email(support.getEmail())
                            .totalStudents(totalStudents)
                            .totalCareRecords(totalCareRecords)
                            .lastCareTime(lastCareTime)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // Cho controller gọi
    public List<SupportUserSummaryResponse> getAllSupportUsers() {
        return getAllSupportSummary();
    }

    // ===== helper dùng chung để map Student -> DTO =====
    private StudentOfSupportResponse mapStudent(Student student) {
        User u = student.getUser();

        String fullName = u != null ? u.getFullName() : null;
        String phone = u != null ? u.getPhone() : null;

        String parentName = student.getParentName();
        String parentPhone = student.getParentPhone();
        String parentEmail = student.getParentEmail();

        String instrument = student.getCourse() != null
                ? student.getCourse().getName()
                : null;
        String lessonType = student.getLessonType();
        String scheduleText = student.getScheduleText();

        Integer totalSessions = student.getTotalSessions();
        Integer completedSessions = student.getCompletedSessions();
        Integer remainingSessions = student.getRemainingSessions();

        String mainTeacherName = (student.getMainTeacher() != null
                && student.getMainTeacher().getUser() != null)
                ? student.getMainTeacher().getUser().getFullName()
                : null;

        Long careCount = careRepo.countByStudentId(student.getId());
        Optional<CustomerCareHistory> lastCareOpt =
                careRepo.findTopByStudentIdOrderByCareTimeDesc(student.getId());
        LocalDateTime lastCareTime =
                lastCareOpt.map(CustomerCareHistory::getCareTime).orElse(null);

        StudentOfSupportResponse.StudentOfSupportResponseBuilder builder =
                StudentOfSupportResponse.builder()
                        .id(student.getId())
                        .fullName(fullName)
                        .phone(phone)
                        .parentName(parentName)
                        .parentPhone(parentPhone)
                        .parentEmail(parentEmail)
                        .instrument(instrument)
                        .lessonType(lessonType)
                        .scheduleText(scheduleText)
                        .status(student.getStatus())
                        .totalSessions(totalSessions)
                        .completedSessions(completedSessions)
                        .remainingSessions(remainingSessions)
                        .mainTeacherName(mainTeacherName)
                        .careRecordCount(careCount)
                        .lastCareTime(lastCareTime);

        fillTuitionInfo(student, builder);

        return builder.build();
    }

    // giống SupportService
    private void fillTuitionInfo(
            Student student,
            StudentOfSupportResponse.StudentOfSupportResponseBuilder builder
    ) {
        builder
                .tuitionAmount(null)
                .tuitionPaidDate(student.getTuitionPaidDate())
                .tuitionDueDate(null)
                .tuitionStatus(null)
                .tuitionReminderStatus("NO_PACKAGE")
                .daysToDue(null);

        Optional<StudentPackage> pkgOpt =
                studentPackageRepo.findTopByStudentIdOrderByCurrentPeriodStartDesc(student.getId());

        if (pkgOpt.isEmpty()) {
            return;
        }

        StudentPackage pkg = pkgOpt.get();

        Long amount = pkg.getTuitionAmount();
        LocalDate paidDate = pkg.getTuitionPaidDate() != null
                ? pkg.getTuitionPaidDate()
                : student.getTuitionPaidDate();
        LocalDate dueDate = pkg.getCurrentPeriodEnd();

        String rawStatus = pkg.getTuitionStatus();

        builder
                .tuitionAmount(amount)
                .tuitionPaidDate(paidDate)
                .tuitionDueDate(dueDate)
                .tuitionStatus(rawStatus);

        if (amount == null || dueDate == null) {
            builder.tuitionReminderStatus("NO_TUITION");
            builder.daysToDue(null);
            return;
        }

        int days = (int) ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
        builder.daysToDue(days);

        String reminder;
        if ("PAID".equalsIgnoreCase(rawStatus)) {
            reminder = "PAID";
        } else if (days < 0) {
            reminder = "OVERDUE";
        } else if (days <= 7) {
            reminder = "DUE_SOON";
        } else {
            reminder = "NOT_PAID";
        }

        builder.tuitionReminderStatus(reminder);
    }

    // ===== 2) LIST HỌC VIÊN CỦA 1 SUPPORT =====
    public List<StudentOfSupportResponse> getStudentsOfSupport(Long supportUserId) {
        List<Student> students = studentRepo.findByCareStaffId(supportUserId);

        return students.stream()
                .map(this::mapStudent)
                .collect(Collectors.toList());
    }

    // ===== 3) LIST HỌC VIÊN CHƯA GÁN SUPPORT =====
    public List<StudentOfSupportResponse> getUnassignedStudents() {
        List<Student> students = studentRepo.findByCareStaffIsNull();

        return students.stream()
                .map(this::mapStudent)
                .collect(Collectors.toList());
    }

    // ===== 4) ADMIN GÁN HỌC VIÊN -> SUPPORT =====
    public void assignStudentToSupport(Long studentId, Long supportUserId) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        User supportUser = userRepo.findById(supportUserId)
                .orElseThrow(() -> new RuntimeException("Support user not found"));

        student.setCareStaff(supportUser);
        studentRepo.save(student);
    }
}
