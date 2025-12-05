package com.solo.solo_music_academy.service;

import com.solo.solo_music_academy.dto.StudentProfileResponse;
import com.solo.solo_music_academy.dto.TeacherProfileResponse;
import com.solo.solo_music_academy.dto.UserProfileResponse;
import com.solo.solo_music_academy.entity.*;
import com.solo.solo_music_academy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepo;
    private final StudentRepository studentRepo;
    private final TeacherRepository teacherRepo;
    private final StudentPackageRepository studentPackageRepo; // ⭐ repo gói học

    // ==== LẤY USER HIỆN TẠI TỪ SECURITY CONTEXT ====
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("Không tìm thấy thông tin đăng nhập");
        }

        String username = auth.getName();
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại: " + username));
    }

    // ==== PROFILE CHUNG (DÙNG CHO HEADER, ADMIN, SUPPORT, v.v.) ====
    public UserProfileResponse getUserProfile() {
        User user = getCurrentUser();

        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus())
                .roles(roleNames)
                .build();
    }

    // ==== PROFILE HỌC VIÊN ====
    public StudentProfileResponse getStudentProfile() {
        User user = getCurrentUser();

        Student s = studentRepo.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ học viên cho user này"));

        // Base: map từ Student (dữ liệu cũ / fallback khi chưa tạo gói)
        StudentProfileResponse.StudentProfileResponseBuilder builder =
                StudentProfileResponse.builder()
                        .id(s.getId())
                        .userId(user.getId())
                        .username(user.getUsername())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .phone(user.getPhone())

                        .parentName(s.getParentName())
                        .parentPhone(s.getParentPhone())
                        .parentEmail(s.getParentEmail())

                        .lessonType(s.getLessonType())
                        .scheduleText(s.getScheduleText())
                        .currentTimeSlot(s.getCurrentTimeSlot())
                        .newTimeSlot(s.getNewTimeSlot())

                        .tuitionPaidDate(s.getTuitionPaidDate())
                        .totalSessions(s.getTotalSessions())
                        .completedSessions(s.getCompletedSessions())
                        .remainingSessions(s.getRemainingSessions())

                        .status(s.getStatus())
                        .note(s.getNote())

                        .mainTeacherId(
                                s.getMainTeacher() != null ? s.getMainTeacher().getId() : null
                        )
                        .mainTeacherName(
                                s.getMainTeacher() != null
                                        ? s.getMainTeacher().getUser().getFullName()
                                        : null
                        )
                        .careStaffUserId(
                                s.getCareStaff() != null ? s.getCareStaff().getId() : null
                        )
                        .careStaffName(
                                s.getCareStaff() != null ? s.getCareStaff().getFullName() : null
                        );

        // ====== OVERRIDE BẰNG GÓI HỌC (NẾU CÓ) ======
        StudentPackage pkg = null;

        // 1. Ưu tiên gói ACTIVE mới nhất theo currentPeriodStart
        Optional<StudentPackage> activeOpt =
                studentPackageRepo.findTopByStudentIdAndStatusOrderByCurrentPeriodStartDesc(
                        s.getId(), StudentPackageStatus.ACTIVE
                );

        if (activeOpt.isPresent()) {
            pkg = activeOpt.get();
        } else {
            // 2. Nếu không có ACTIVE thì lấy gói mới nhất bất kỳ status
            List<StudentPackage> allPkgs =
                    studentPackageRepo.findByStudentUserUsernameOrderByIdDesc(user.getUsername());
            if (!allPkgs.isEmpty()) {
                pkg = allPkgs.get(0);
            }
        }

        // Default reminder nếu chưa có gói
        String reminderStatus = "NO_PACKAGE";
        Integer daysToDue = null;

        if (pkg != null) {
            builder.activePackageId(pkg.getId());

            // Giáo viên chính từ gói học
            if (pkg.getTeacher() != null) {
                builder.mainTeacherId(pkg.getTeacher().getId());
                builder.mainTeacherName(pkg.getTeacher().getUser().getFullName());
            }

            // Hình thức / tiến độ / trạng thái từ gói học
            if (pkg.getLessonForm() != null) {
                builder.lessonType(pkg.getLessonForm());
            }

            builder.tuitionPaidDate(pkg.getTuitionPaidDate());
            builder.totalSessions(pkg.getTotalSessions());
            builder.completedSessions(pkg.getSessionsCompleted());
            builder.remainingSessions(pkg.getSessionsRemaining());

            if (pkg.getStatus() != null) {
                builder.status(pkg.getStatus().name());
            }
            if (pkg.getNote() != null) {
                builder.note(pkg.getNote());
            }

            // Học phí + hạn đóng
            builder.tuitionAmount(pkg.getTuitionAmount());
            builder.tuitionDueDate(pkg.getTuitionDueDate());
            builder.tuitionStatus(pkg.getTuitionStatus());

            // Build scheduleText từ packageSlots
            if (pkg.getPackageSlots() != null && !pkg.getPackageSlots().isEmpty()) {
                String schedule = pkg.getPackageSlots().stream()
                        .map(sps -> {
                            TeacherScheduleSlot slot = sps.getSlot();
                            if (slot == null) return null;

                            String dowLabel;
                            switch (slot.getDayOfWeek()) {
                                case 1 -> dowLabel = "Thứ 2";
                                case 2 -> dowLabel = "Thứ 3";
                                case 3 -> dowLabel = "Thứ 4";
                                case 4 -> dowLabel = "Thứ 5";
                                case 5 -> dowLabel = "Thứ 6";
                                case 6 -> dowLabel = "Thứ 7";
                                case 7 -> dowLabel = "CN";
                                default -> dowLabel = "Thứ " + slot.getDayOfWeek();
                            }

                            String start = slot.getStartTime() != null
                                    ? slot.getStartTime().toString().substring(0, 5)
                                    : "";
                            String end = slot.getEndTime() != null
                                    ? slot.getEndTime().toString().substring(0, 5)
                                    : "";

                            return dowLabel + " " + start + "-" + end;
                        })
                        .filter(str -> str != null && !str.isBlank())
                        .distinct()
                        .collect(Collectors.joining(", "));

                if (!schedule.isBlank()) {
                    builder.scheduleText(schedule);
                    builder.currentTimeSlot(schedule);
                }
            }

            // ====== TÍNH TRẠNG THÁI NHẮC HỌC PHÍ ======
            if (pkg.getTuitionAmount() == null) {
                // Gói này không cấu hình học phí
                reminderStatus = "NO_TUITION";
            } else if (pkg.getTuitionPaidDate() != null) {
                // Đã đóng rồi
                reminderStatus = "PAID";
            } else {
                // Chưa có ngày đóng -> xem hạn
                LocalDate due = pkg.getTuitionDueDate();
                if (due != null) {
                    final LocalDate today = LocalDate.now();
                    long diff = ChronoUnit.DAYS.between(today, due);
                    daysToDue = (int) diff;

                    if (diff < 0) {
                        // Quá hạn
                        reminderStatus = "OVERDUE";
                    } else if (diff <= 7) {
                        // Trong vòng 7 ngày tới
                        reminderStatus = "DUE_SOON";
                    } else {
                        reminderStatus = "NOT_PAID";
                    }
                } else {
                    // Không có due date -> coi như chưa đóng nhưng không biết hạn
                    reminderStatus = "NOT_PAID";
                }
            }
        }

        builder.tuitionReminderStatus(reminderStatus);
        builder.daysToDue(daysToDue);

        return builder.build();
    }

    // ==== PROFILE GIÁO VIÊN ====
    public TeacherProfileResponse getTeacherProfile() {
        User user = getCurrentUser();

        Teacher t = teacherRepo.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ giáo viên cho user này"));

        return TeacherProfileResponse.builder()
                .id(t.getId())
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .instrument(t.getInstrument())
                .gender(t.getGender())
                .position(t.getPosition())
                .teachingType(t.getTeachingType())
                .status(t.getStatus())
                .note(t.getNote())
                .build();
    }
}
