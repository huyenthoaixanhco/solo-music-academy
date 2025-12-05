package com.solo.solo_music_academy.service;

import com.solo.solo_music_academy.dto.StudentTuitionOverviewResponse;
import com.solo.solo_music_academy.dto.TuitionPayRequest;
import com.solo.solo_music_academy.entity.Student;
import com.solo.solo_music_academy.entity.StudentPackage;
import com.solo.solo_music_academy.entity.User;
import com.solo.solo_music_academy.repository.StudentPackageRepository;
import com.solo.solo_music_academy.repository.StudentRepository;
import com.solo.solo_music_academy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TuitionService {

    private final StudentPackageRepository packageRepo;
    private final StudentRepository studentRepo;
    private final UserRepository userRepo;

    // ==================== STUDENT SIDE ====================

    /**
     * Student gọi: GET /student/tuition/overview
     * Lấy gói học hiện tại mới nhất của học viên + thông tin học phí + số buổi
     */
    public StudentTuitionOverviewResponse getCurrentStudentTuitionOverview() {
        User current = getCurrentUser();

        // Tìm student từ user
        Student student = studentRepo.findByUserUsername(current.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("Student not found for user " + current.getUsername())
                );

        // Lấy list gói học của student, gói mới nhất nằm đầu
        List<StudentPackage> list = packageRepo
                .findByStudentUserUsernameOrderByIdDesc(current.getUsername());

        if (list.isEmpty()) {
            // Học viên chưa có gói nào
            return StudentTuitionOverviewResponse.builder()
                    .packageId(null)
                    .tuitionStatus("NONE")
                    .daysToDue(null)
                    .totalSessions(null)
                    .completedSessions(null)
                    .remainingSessions(null)
                    .build();
        }

        StudentPackage sp = list.get(0);

        LocalDate today = LocalDate.now();
        LocalDate due = sp.getTuitionDueDate();
        String status = sp.getTuitionStatus();

        // Auto đánh dấu OVERDUE nếu quá hạn mà vẫn NOT_PAID / PARTIALLY_PAID
        if (due != null &&
                ("NOT_PAID".equals(status) || "PARTIALLY_PAID".equals(status))) {

            if (today.isAfter(due)) {
                status = "OVERDUE";
                sp.setTuitionStatus("OVERDUE");
                packageRepo.save(sp);
            }
        }

        Integer daysToDue = null;
        if (due != null) {
            daysToDue = (int) ChronoUnit.DAYS.between(today, due);
        }

        return StudentTuitionOverviewResponse.builder()
                .packageId(sp.getId())
                .tuitionAmount(sp.getTuitionAmount())
                .tuitionStatus(status)
                .tuitionDueDate(due != null ? due.toString() : null)
                .tuitionPaidDate(
                        sp.getTuitionPaidDate() != null ? sp.getTuitionPaidDate().toString() : null
                )
                .totalSessions(sp.getTotalSessions())
                .completedSessions(sp.getSessionsCompleted())
                .remainingSessions(sp.getSessionsRemaining())
                .daysToDue(daysToDue)
                .build();
    }

    // ==================== SUPPORT SIDE ====================

    /**
     * Support xác nhận đã thu học phí cho 1 gói học cụ thể
     * POST /support/tuition/students/{studentId}/packages/{packageId}/pay
     */
    public void confirmTuitionPaid(Long studentId, Long packageId, TuitionPayRequest req) {
        User current = getCurrentUser();
        // Nếu muốn chặt hơn thì check role:
        // if (!hasRole(current, "ROLE_SUPPORT")) throw new RuntimeException("Không có quyền");

        StudentPackage sp = packageRepo.findById(packageId)
                .orElseThrow(() ->
                        new RuntimeException("StudentPackage not found: " + packageId)
                );

        if (sp.getStudent() == null || !sp.getStudent().getId().equals(studentId)) {
            throw new RuntimeException("Package không thuộc học viên này");
        }

        Long amount = req.getAmount();
        // Nếu có truyền amount và có tuitionAmount thì xét PARTIALLY_PAID / PAID
        if (amount != null && sp.getTuitionAmount() != null && amount < sp.getTuitionAmount()) {
            sp.setTuitionStatus("PARTIALLY_PAID");
        } else {
            sp.setTuitionStatus("PAID");
        }

        LocalDate paidDate = req.getPaidDate() != null
                ? LocalDate.parse(req.getPaidDate())
                : LocalDate.now();

        sp.setTuitionPaidDate(paidDate);
        packageRepo.save(sp);
    }

    // ==================== helper ====================

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    @SuppressWarnings("unused")
    private boolean hasRole(User user, String roleName) {
        return user.getRoles().stream().anyMatch(r -> roleName.equals(r.getName()));
    }
}
