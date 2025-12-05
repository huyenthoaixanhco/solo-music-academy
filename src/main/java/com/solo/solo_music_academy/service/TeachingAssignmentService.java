package com.solo.solo_music_academy.service;

import com.solo.solo_music_academy.dto.TeachingAssignmentRequest;
import com.solo.solo_music_academy.dto.TeachingAssignmentResponse;
import com.solo.solo_music_academy.entity.*;
import com.solo.solo_music_academy.repository.LessonScheduleRepository;
import com.solo.solo_music_academy.repository.StudentPackageRepository;
import com.solo.solo_music_academy.repository.StudentRepository;
import com.solo.solo_music_academy.repository.TeacherRepository;
import com.solo.solo_music_academy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeachingAssignmentService {

    private final LessonScheduleRepository lessonScheduleRepo;
    private final StudentRepository studentRepo;
    private final TeacherRepository teacherRepo;
    private final UserRepository userRepo;

    // 👇 THÊM REPO GÓI HỌC
    private final StudentPackageRepository studentPackageRepo;

    // ========= ADMIN: tạo phân công (bảng lesson_schedules – dùng cho demo cũ) =========
    public TeachingAssignmentResponse createAssignment(TeachingAssignmentRequest req) {

        Student student = studentRepo.findById(req.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Teacher teacher = teacherRepo.findById(req.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        LessonSchedule schedule = LessonSchedule.builder()
                .student(student)
                .teacher(teacher)
                .dayOfWeek(req.getDayOfWeek())
                .startTime(req.getStartTime())   // kiểu gì (String/LocalTime) cũng toString được
                .endTime(req.getEndTime())
                .room(req.getRoom())
                .status(req.getStatus() != null ? req.getStatus() : "ACTIVE")
                .build();

        lessonScheduleRepo.save(schedule);

        return mapToResponse(schedule);
    }

    // ========= ADMIN: cập nhật phân công =========
    public TeachingAssignmentResponse updateAssignment(Long id, TeachingAssignmentRequest req) {
        LessonSchedule schedule = lessonScheduleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("LessonSchedule not found"));

        if (req.getStudentId() != null
                && !req.getStudentId().equals(schedule.getStudent().getId())) {
            Student student = studentRepo.findById(req.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            schedule.setStudent(student);
        }

        if (req.getTeacherId() != null
                && !req.getTeacherId().equals(schedule.getTeacher().getId())) {
            Teacher teacher = teacherRepo.findById(req.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));
            schedule.setTeacher(teacher);
        }

        if (req.getDayOfWeek() != 0) {
            schedule.setDayOfWeek(req.getDayOfWeek());
        }
        if (req.getStartTime() != null) {
            schedule.setStartTime(req.getStartTime());
        }
        if (req.getEndTime() != null) {
            schedule.setEndTime(req.getEndTime());
        }
        if (req.getRoom() != null) {
            schedule.setRoom(req.getRoom());
        }
        if (req.getStatus() != null) {
            schedule.setStatus(req.getStatus());
        }

        lessonScheduleRepo.save(schedule);
        return mapToResponse(schedule);
    }

    // ========= ADMIN: xoá phân công =========
    public void deleteAssignment(Long id) {
        lessonScheduleRepo.deleteById(id);
    }

    // ========= ADMIN: xem tất cả phân công =========
    public List<TeachingAssignmentResponse> getAllAssignments() {
        return lessonScheduleRepo.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ========= TEACHER: lịch dạy của chính mình (từ lesson_schedules) =========
    public List<TeachingAssignmentResponse> getMyScheduleAsTeacher() {
        User current = getCurrentUser();
        Teacher teacher = teacherRepo.findByUserId(current.getId())
                .orElseThrow(() -> new RuntimeException("Teacher not found for user"));

        List<LessonSchedule> list =
                lessonScheduleRepo.findByTeacherIdOrderByDayOfWeekAscStartTimeAsc(teacher.getId());

        return list.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ========= STUDENT: lịch học của chính mình (LẤY TỪ GÓI HỌC) =========
    public List<TeachingAssignmentResponse> getMyScheduleAsStudent() {
        User current = getCurrentUser();

        // 1. Tìm student của user hiện tại
        Student student = studentRepo.findByUserId(current.getId())
                .orElseThrow(() -> new RuntimeException("Student not found for user"));

        // 2. Lấy tất cả gói học ACTIVE của học viên
        List<StudentPackage> packages = studentPackageRepo
                .findByStudentIdAndStatus(student.getId(), StudentPackageStatus.ACTIVE);

        String studentName = (student.getUser() != null)
                ? student.getUser().getFullName()
                : null;

        // 3. Flatten tất cả slot trong các gói học -> TeachingAssignmentResponse
        return packages.stream()
                .flatMap(sp -> sp.getPackageSlots().stream())
                .map(sps -> {
                    TeacherScheduleSlot slot = sps.getSlot();
                    Teacher teacher = slot.getTeacher();

                    String teacherName = (teacher != null && teacher.getUser() != null)
                            ? teacher.getUser().getFullName()
                            : null;

                    // ❗ Ở đây NHỚ return, nếu không map sẽ báo lỗi
                    return TeachingAssignmentResponse.builder()
                            // dùng id slot làm id "schedule"
                            .id(slot.getId())
                            .studentId(student.getId())
                            .studentName(studentName)
                            .teacherId(teacher != null ? teacher.getId() : null)
                            .teacherName(teacherName)
                            .dayOfWeek(slot.getDayOfWeek())
                            // slot.getStartTime() / getEndTime() có thể là LocalTime -> toString()
                            .startTime(slot.getStartTime() != null
                                    ? slot.getStartTime().toString()
                                    : null)
                            .endTime(slot.getEndTime() != null
                                    ? slot.getEndTime().toString()
                                    : null)
                            // TeacherScheduleSlot không có room -> để null
                            .room(null)
                            .status(StudentPackageStatus.ACTIVE.name())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ========= Helper: map LessonSchedule -> DTO (cho admin/teacher) =========
    private TeachingAssignmentResponse mapToResponse(LessonSchedule s) {
        Student stu = s.getStudent();
        Teacher t = s.getTeacher();

        String studentName = (stu != null && stu.getUser() != null)
                ? stu.getUser().getFullName()
                : null;

        String teacherName = (t != null && t.getUser() != null)
                ? t.getUser().getFullName()
                : null;

        return TeachingAssignmentResponse.builder()
                .id(s.getId())
                .studentId(stu != null ? stu.getId() : null)
                .studentName(studentName)
                .teacherId(t != null ? t.getId() : null)
                .teacherName(teacherName)
                .dayOfWeek(s.getDayOfWeek())
                .startTime(s.getStartTime() != null
                        ? s.getStartTime().toString()
                        : null)
                .endTime(s.getEndTime() != null
                        ? s.getEndTime().toString()
                        : null)
                .room(s.getRoom())
                .status(s.getStatus())
                .build();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
}
