package com.solo.solo_music_academy.service;

import com.solo.solo_music_academy.dto.CareReminderResponse;
import com.solo.solo_music_academy.dto.CustomerCareHistoryResponse;
import com.solo.solo_music_academy.dto.StudentOfSupportResponse;
import com.solo.solo_music_academy.entity.CustomerCareHistory;
import com.solo.solo_music_academy.entity.Student;
import com.solo.solo_music_academy.entity.StudentPackage;
import com.solo.solo_music_academy.entity.User;
import com.solo.solo_music_academy.repository.CustomerCareHistoryRepository;
import com.solo.solo_music_academy.repository.StudentPackageRepository;
import com.solo.solo_music_academy.repository.StudentRepository;
import com.solo.solo_music_academy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final StudentRepository studentRepo;
    private final CustomerCareHistoryRepository careRepo;
    private final UserRepository userRepo;

    // repo gói học để lấy hạn học phí + số buổi
    private final StudentPackageRepository studentPackageRepo;

    // ===== LẤY USER HIỆN TẠI (dùng chung nội bộ service) =====
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    // ===== HÀM PUBLIC ĐỂ SERVICE KHÁC DÙNG (LEAD, REMINDER, ...) =====
    public User getCurrentSupportUser() {
        // hiện tại user SUPPORT cũng chỉ là User bình thường, nên dùng lại getCurrentUser()
        return getCurrentUser();
    }

    public Long getCurrentSupportUserId() {
        return getCurrentUser().getId();
    }

    // ===== 1) SUPPORT: danh sách học viên của chính mình =====
    public List<StudentOfSupportResponse> getMyStudents() {
        User support = getCurrentUser();
        List<Student> students = studentRepo.findByCareStaffId(support.getId());

        return students.stream()
                .map(this::mapStudentForSupport)
                .toList();
    }

    private StudentOfSupportResponse mapStudentForSupport(Student student) {
        User u = student.getUser();

        String fullName = u != null ? u.getFullName() : null;
        String phone = u != null ? u.getPhone() : null;

        // Phụ huynh
        String parentName = student.getParentName();
        String parentPhone = student.getParentPhone();
        String parentEmail = student.getParentEmail();

        // Thông tin lớp / môn
        String instrument = student.getCourse() != null
                ? student.getCourse().getName()
                : null;
        String lessonType = student.getLessonType();
        String scheduleText = student.getScheduleText();

        // Tiến độ (từ entity Student – có thể bị override bởi gói học)
        Integer totalSessions = student.getTotalSessions();
        Integer completedSessions = student.getCompletedSessions();
        Integer remainingSessions = student.getRemainingSessions();

        // GV chính
        String mainTeacherName = (student.getMainTeacher() != null
                && student.getMainTeacher().getUser() != null)
                ? student.getMainTeacher().getUser().getFullName()
                : null;

        // Thống kê CSKH
        Long careCount = careRepo.countByStudentId(student.getId());
        Optional<CustomerCareHistory> lastCareOpt =
                careRepo.findTopByStudentIdOrderByCareTimeDesc(student.getId());
        LocalDateTime lastCareTime =
                lastCareOpt.map(CustomerCareHistory::getCareTime).orElse(null);

        // Builder cơ bản
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

        // Gắn thêm thông tin học phí / hạn + override số buổi từ gói học
        fillTuitionInfo(student, builder);

        return builder.build();
    }

    /**
     * Tính thông tin học phí + hạn đóng cho 1 học viên.
     * Ưu tiên lấy từ gói học mới nhất, fallback qua field cũ trên Student nếu cần.
     */
    private void fillTuitionInfo(
            Student student,
            StudentOfSupportResponse.StudentOfSupportResponseBuilder builder
    ) {
        // Default: chưa có gói học
        builder
                .tuitionAmount(null)
                .tuitionPaidDate(student.getTuitionPaidDate())
                .tuitionDueDate(null)
                .tuitionStatus(null)
                .tuitionReminderStatus("NO_PACKAGE")
                .daysToDue(null);

        // Lấy gói học mới nhất theo ID
        Optional<StudentPackage> pkgOpt =
                studentPackageRepo.findFirstByStudentIdOrderByIdDesc(student.getId());

        if (pkgOpt.isEmpty()) {
            // không có gói học -> giữ "NO_PACKAGE"
            return;
        }

        StudentPackage pkg = pkgOpt.get();

        // ===== override SỐ BUỔI từ gói học nếu có =====
        if (pkg.getTotalSessions() != null) {
            builder.totalSessions(pkg.getTotalSessions());
        }
        if (pkg.getSessionsCompleted() != null) {
            builder.completedSessions(pkg.getSessionsCompleted());
        }
        if (pkg.getSessionsRemaining() != null) {
            builder.remainingSessions(pkg.getSessionsRemaining());
        }

        // ===== HỌC PHÍ / HẠN =====
        Long amount = pkg.getTuitionAmount();

        LocalDate paidDate = pkg.getTuitionPaidDate() != null
                ? pkg.getTuitionPaidDate()
                : student.getTuitionPaidDate();

        // Hạn đóng: ưu tiên tuitionDueDate, nếu null thì dùng currentPeriodEnd
        LocalDate dueDate = pkg.getTuitionDueDate() != null
                ? pkg.getTuitionDueDate()
                : pkg.getCurrentPeriodEnd();

        String rawStatus = pkg.getTuitionStatus();   // NOT_PAID / PARTIALLY_PAID / PAID / OVERDUE...

        builder
                .tuitionAmount(amount)
                .tuitionPaidDate(paidDate)
                .tuitionDueDate(dueDate)
                .tuitionStatus(rawStatus);

        // Nếu không có amount hoặc không có dueDate = chưa cấu hình học phí
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

    // ===== 2) SUPPORT: xem lịch sử CSKH của chính mình (tất cả học viên) =====
    public List<CustomerCareHistoryResponse> getMyCareHistory() {
        User support = getCurrentUser();
        List<CustomerCareHistory> list =
                careRepo.findBySupportUserIdOrderByCareTimeDesc(support.getId());
        return list.stream().map(this::mapToResponse).toList();
    }

    private CustomerCareHistoryResponse mapToResponse(CustomerCareHistory h) {
        Student s = h.getStudent();
        User stuUser = (s != null) ? s.getUser() : null;
        User sup = h.getSupportUser();

        return CustomerCareHistoryResponse.builder()
                .id(h.getId())
                .studentId(s != null ? s.getId() : null)
                .studentName(stuUser != null ? stuUser.getFullName() : null)
                .supportUserId(sup != null ? sup.getId() : null)
                .supportFullName(sup != null ? sup.getFullName() : null)
                .careTime(h.getCareTime() != null ? h.getCareTime().toString() : null)
                .careType(h.getCareType())
                .channel(h.getChannel())
                .content(h.getContent())
                .result(h.getResult())
                .important(h.getImportant())
                .nextCareTime(h.getNextCareTime() != null ? h.getNextCareTime().toString() : null)
                .build();
    }

    private CareReminderResponse mapToReminder(CustomerCareHistory h) {
        Student s = h.getStudent();
        User stuUser = (s != null) ? s.getUser() : null;

        return CareReminderResponse.builder()
                .historyId(h.getId())
                .studentId(s != null ? s.getId() : null)
                .studentName(stuUser != null ? stuUser.getFullName() : null)
                .careType(h.getCareType())
                .channel(h.getChannel())
                .content(h.getContent())
                .important(h.getImportant())
                .nextCareTime(
                        h.getNextCareTime() != null ? h.getNextCareTime().toString() : null
                )
                .build();
    }

    // ===== 3) SUPPORT: danh sách nhắc việc trong khoảng ngày =====
    public List<CareReminderResponse> getMyRemindersInRange(LocalDate from, LocalDate to) {
        User support = getCurrentUser();

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(LocalTime.MAX);

        List<CustomerCareHistory> list =
                careRepo.findBySupportUserIdAndNextCareTimeBetweenOrderByNextCareTimeAsc(
                        support.getId(), start, end
                );

        return list.stream()
                .map(this::mapToReminder)
                .toList();
    }

    // ===== 4) SUPPORT: nhắc việc hôm nay =====
    public List<CareReminderResponse> getMyRemindersToday() {
        LocalDate today = LocalDate.now();
        return getMyRemindersInRange(today, today);
    }

    // ===== 5) SUPPORT: nhắc việc 7 ngày tới =====
    public List<CareReminderResponse> getMyUpcomingReminders(int days) {
        LocalDate today = LocalDate.now();
        LocalDate to = today.plusDays(days);
        return getMyRemindersInRange(today, to);
    }
}
