package com.solo.solo_music_academy.service;

import com.solo.solo_music_academy.dto.StudentPackageCreateRequest;
import com.solo.solo_music_academy.dto.StudentPackageResponse;
import com.solo.solo_music_academy.dto.WeeklyScheduleCreateRequest;
import com.solo.solo_music_academy.entity.*;
import com.solo.solo_music_academy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

// StudentPackageService.java
@Service
@RequiredArgsConstructor
public class StudentPackageService {

    private final StudentPackageRepository packageRepo;
    private final StudentRepository studentRepo;
    private final TeacherRepository teacherRepo;
    private final TeacherScheduleSlotRepository slotRepo;
    private final CourseRepository courseRepo;

    // ===== TẠO GÓI HỌC =====
    @Transactional
    public StudentPackage createPackage(StudentPackageCreateRequest req) {

        if (req.getSchedules() == null || req.getSchedules().isEmpty()) {
            throw new IllegalArgumentException("Schedules must not be empty");
        }

        StudentPackage pkg = new StudentPackage();
        applyRequestToEntity(req, pkg);

        return packageRepo.save(pkg);
    }

    // ===== CẬP NHẬT GÓI HỌC =====
    @Transactional
    public StudentPackage updatePackage(Long id, StudentPackageCreateRequest req) {
        if (req.getSchedules() == null || req.getSchedules().isEmpty()) {
            throw new IllegalArgumentException("Schedules must not be empty");
        }

        StudentPackage pkg = packageRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("StudentPackage not found: " + id));

        applyRequestToEntity(req, pkg);

        return packageRepo.save(pkg);
    }

    // ===== XÓA GÓI HỌC =====
    @Transactional
    public void deletePackage(Long id) {
        if (!packageRepo.existsById(id)) {
            throw new IllegalArgumentException("StudentPackage not found: " + id);
        }
        packageRepo.deleteById(id);
    }

    public List<StudentPackageResponse> getAllPackages() {
        return packageRepo.findAll().stream()
                .map(StudentPackageResponse::fromEntity)
                .toList();
    }

    // ===== HELPER DÙNG CHUNG CHO CREATE + UPDATE =====
    private void applyRequestToEntity(StudentPackageCreateRequest req, StudentPackage pkg) {

        // ----- Học viên -----
        Student student = studentRepo.findById(req.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + req.getStudentId()));
        pkg.setStudent(student);

        // ----- Giáo viên -----
        Teacher teacher = teacherRepo.findById(req.getTeacherId())
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + req.getTeacherId()));
        pkg.setTeacher(teacher);

        // ----- Khóa học (nếu có) -----
        Course course = null;
        if (req.getCourseId() != null) {
            course = courseRepo.findById(req.getCourseId())
                    .orElseThrow(() -> new IllegalArgumentException("Course not found: " + req.getCourseId()));
        }
        pkg.setCourse(course);

        // ===== Auto fill từ course nếu có =====
        Integer totalSessions = req.getTotalSessions();
        String lessonForm = req.getLessonForm();
        Long tuitionAmount = req.getTuitionAmount();

        if (course != null) {
            if (totalSessions == null) {
                totalSessions = course.getTotalSessions();
            }
            if (lessonForm == null || lessonForm.isBlank()) {
                lessonForm = course.getName();
            }
            if (course.getTuitionFee() != null) {
                tuitionAmount = course.getTuitionFee().longValue();
            }
        }

        int sessionsCompleted = req.getSessionsCompleted() != null
                ? req.getSessionsCompleted()
                : 0;

        // Tính số buổi còn lại
        Integer sessionsRemaining = null;
        if (totalSessions != null) {
            sessionsRemaining = totalSessions - sessionsCompleted;
            if (sessionsRemaining < 0) sessionsRemaining = 0;
        }

        // Xử lý trạng thái học phí
        String tuitionStatus = null;
        if (tuitionAmount != null) {
            if (req.getTuitionPaidDate() != null) {
                tuitionStatus = "PAID";
            } else {
                tuitionStatus = "NOT_PAID";
            }
        }

        // ===== Set field chính trên gói =====
        pkg.setParentName(req.getParentName());
        pkg.setParentContact(req.getParentContact());
        pkg.setContactChannel(req.getContactChannel());

        pkg.setLessonForm(lessonForm);
        pkg.setTuitionAmount(tuitionAmount);

        pkg.setOldPeriodStart(req.getOldPeriodStart());
        pkg.setOldPeriodEnd(req.getOldPeriodEnd());
        pkg.setCurrentPeriodStart(req.getCurrentPeriodStart());
        pkg.setCurrentPeriodEnd(req.getCurrentPeriodEnd());

        // Học phí
        pkg.setTuitionDueDate(req.getTuitionDueDate());
        pkg.setTuitionPaidDate(req.getTuitionPaidDate());
        pkg.setTuitionStatus(tuitionStatus);

        // Số buổi
        pkg.setTotalSessions(totalSessions);
        pkg.setSessionsCompleted(sessionsCompleted);
        pkg.setSessionsRemaining(sessionsRemaining);

        // Ghi chú
        pkg.setNote(req.getNote());

        // ===== 🔁 SYNC TÓM TẮT VỀ BẢNG STUDENTS =====
        // Để AdminStudentsPage & StudentHome dùng chung data đã chuẩn hóa theo gói học

        // Giáo viên chính lấy từ gói
        student.setMainTeacher(teacher);

        // Khóa học + thông tin buổi
        student.setCourse(course);
        student.setTotalSessions(totalSessions);
        student.setCompletedSessions(sessionsCompleted);
        student.setRemainingSessions(sessionsRemaining);

        // Hình thức học (ví dụ: "One to one / 45 mins / Piano")
        if (lessonForm != null) {
            student.setLessonType(lessonForm);
        }

        // Đồng bộ ngày đã đóng học phí (nếu m còn dùng field này trong Student)
        student.setTuitionPaidDate(req.getTuitionPaidDate());

        // (Nếu muốn sau này, có thể build scheduleText từ req.getSchedules())
        studentRepo.save(student);

        // ===== Gắn lại các slot lịch học =====
        pkg.getPackageSlots().clear(); // với update: xóa list cũ (orphanRemoval = true)

        for (WeeklyScheduleCreateRequest sReq : req.getSchedules()) {
            int dayOfWeek = sReq.getDayOfWeek();
            LocalTime start = LocalTime.parse(sReq.getStartTime());
            LocalTime end = LocalTime.parse(sReq.getEndTime());

            TeacherScheduleSlot slot = slotRepo
                    .findByTeacherIdAndDayOfWeekAndStartTimeAndEndTime(
                            teacher.getId(), dayOfWeek, start, end
                    )
                    .orElseGet(() -> {
                        TeacherScheduleSlot s = TeacherScheduleSlot.builder()
                                .teacher(teacher)
                                .dayOfWeek(dayOfWeek)
                                .startTime(start)
                                .endTime(end)
                                .note(sReq.getSlotNote())
                                .build();
                        return slotRepo.save(s);
                    });

            StudentPackageSlot pkgSlot = StudentPackageSlot.builder()
                    .studentPackage(pkg)
                    .slot(slot)
                    .build();

            pkg.getPackageSlots().add(pkgSlot);
        }
    }
}
