package com.solo.solo_music_academy.service;

import com.solo.solo_music_academy.dto.CourseListResponse;
import com.solo.solo_music_academy.dto.CreateCourseRequest;
import com.solo.solo_music_academy.entity.Course;
import com.solo.solo_music_academy.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCourseService {

    private final CourseRepository courseRepo;

    // ===== TẠO KHÓA HỌC =====
    public CourseListResponse createCourse(CreateCourseRequest req) {

        if (courseRepo.existsByCode(req.getCode())) {
            throw new RuntimeException("Mã khóa học đã tồn tại");
        }

        Course course = Course.builder()
                .code(req.getCode())
                .name(req.getName())
                .description(req.getDescription())
                .instrument(req.getInstrument())
                .level(req.getLevel())
                .totalSessions(req.getTotalSessions())
                .tuitionFee(req.getTuitionFee())   // BigDecimal
                .status(req.getStatus() != null ? req.getStatus() : "ACTIVE")
                .note(req.getNote())
                .build();

        courseRepo.save(course);
        return mapToListResponse(course);
    }

    // ===== CẬP NHẬT KHÓA HỌC =====
    public CourseListResponse updateCourse(Long id, CreateCourseRequest req) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Nếu cho phép đổi code
        if (req.getCode() != null && !req.getCode().equals(course.getCode())) {
            if (courseRepo.existsByCode(req.getCode())) {
                throw new RuntimeException("Mã khóa học đã tồn tại");
            }
            course.setCode(req.getCode());
        }

        if (req.getName() != null)         course.setName(req.getName());
        if (req.getDescription() != null)  course.setDescription(req.getDescription());
        if (req.getInstrument() != null)   course.setInstrument(req.getInstrument());
        if (req.getLevel() != null)        course.setLevel(req.getLevel());
        if (req.getTotalSessions() != null) course.setTotalSessions(req.getTotalSessions());
        if (req.getTuitionFee() != null)   course.setTuitionFee(req.getTuitionFee());
        if (req.getStatus() != null)       course.setStatus(req.getStatus());
        if (req.getNote() != null)         course.setNote(req.getNote());

        courseRepo.save(course);
        return mapToListResponse(course);
    }

    // ===== XOÁ KHÓA HỌC =====
    public void deleteCourse(Long id) {
        courseRepo.deleteById(id);
    }

    // ===== LẤY DS KHÓA HỌC =====
    public List<CourseListResponse> getAllCourses() {
        return courseRepo.findAll().stream()
                .map(this::mapToListResponse)
                .toList();
    }

    // ===== LẤY 1 KHÓA HỌC =====
    public CourseListResponse getCourse(Long id) {
        Course c = courseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return mapToListResponse(c);
    }

    private CourseListResponse mapToListResponse(Course c) {
        return CourseListResponse.builder()
                .id(c.getId())
                .code(c.getCode())
                .name(c.getName())
                .instrument(c.getInstrument())
                .level(c.getLevel())
                .totalSessions(c.getTotalSessions())
                .tuitionFee(c.getTuitionFee())   // BigDecimal -> JSON number/string
                .status(c.getStatus())
                .build();
    }
}
