package com.solo.solo_music_academy.service;

import com.solo.solo_music_academy.dto.AdminDashboardSummaryResponse;
import com.solo.solo_music_academy.entity.*;
import com.solo.solo_music_academy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final StudentRepository studentRepo;
    private final TeacherRepository teacherRepo;
    private final CourseRepository courseRepo;
    private final UserRepository userRepo;
    private final CustomerCareHistoryRepository careRepo;

    public AdminDashboardSummaryResponse getSummary() {

        // ====== LOAD DATA ======
        List<Student> students = studentRepo.findAll();
        List<Teacher> teachers = teacherRepo.findAll();
        List<Course> courses = courseRepo.findAll();
        List<CustomerCareHistory> cares = careRepo.findAll();

        // Nếu m đã có userRepo.findAllSupportUsers() như bên AdminSupportService
        List<User> supports = userRepo.findAllSupportUsers();

        // ====== HỌC VIÊN ======
        long totalStudents = students.size();
        long activeStudents = students.stream()
                .filter(s -> "ACTIVE".equalsIgnoreCase(s.getStatus()))
                .count();
        long pausedStudents = students.stream()
                .filter(s -> "PAUSED".equalsIgnoreCase(s.getStatus()))
                .count();
        long stoppedStudents = students.stream()
                .filter(s -> "STOPPED".equalsIgnoreCase(s.getStatus()))
                .count();

        long unassignedStudents = students.stream()
                .filter(s -> s.getCareStaff() == null)
                .count();

        long almostFinishedStudents = students.stream()
                .filter(s -> s.getRemainingSessions() != null
                        && s.getRemainingSessions() <= 2
                        && "ACTIVE".equalsIgnoreCase(s.getStatus()))
                .count();

        // ====== GIÁO VIÊN ======
        long totalTeachers = teachers.size();
        long activeTeachers = teachers.stream()
                .filter(t -> "ACTIVE".equalsIgnoreCase(t.getStatus()))
                .count();

        // ====== KHÓA HỌC ======
        long totalCourses = courses.size();
        long activeCourses = courses.stream()
                .filter(c -> "ACTIVE".equalsIgnoreCase(c.getStatus()))
                .count();

        // ====== CSKH (SUPPORT USERS) ======
        long totalSupportUsers = supports.size();

        // ====== NHẮC VIỆC HÔM NAY (ALL SUPPORT) ======
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        long todayReminders = cares.stream()
                .filter(h -> h.getNextCareTime() != null
                        && !h.getNextCareTime().isBefore(start)
                        && !h.getNextCareTime().isAfter(end))
                .count();

        // ====== BUILD RESPONSE ======
        return AdminDashboardSummaryResponse.builder()
                .totalStudents(totalStudents)
                .activeStudents(activeStudents)
                .pausedStudents(pausedStudents)
                .stoppedStudents(stoppedStudents)
                .unassignedStudents(unassignedStudents)
                .almostFinishedStudents(almostFinishedStudents)

                .totalTeachers(totalTeachers)
                .activeTeachers(activeTeachers)

                .totalSupportUsers(totalSupportUsers)

                .totalCourses(totalCourses)
                .activeCourses(activeCourses)

                .todayReminders(todayReminders)
                .build();
    }
}
