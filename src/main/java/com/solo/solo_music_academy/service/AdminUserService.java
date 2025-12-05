package com.solo.solo_music_academy.service;

import com.solo.solo_music_academy.dto.*;
import com.solo.solo_music_academy.entity.*;
import com.solo.solo_music_academy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final TeacherRepository teacherRepo;
    private final StudentRepository studentRepo;
    private final CourseRepository courseRepo;

    private final BCryptPasswordEncoder passwordEncoder;

    // ====== TẠO GIÁO VIÊN ======
    public TeacherListResponse createTeacher(CreateTeacherRequest req) {
        if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            throw new RuntimeException("Username đã tồn tại");
        }

        Role teacherRole = roleRepo.findByName("ROLE_TEACHER")
                .orElseThrow(() -> new RuntimeException("ROLE_TEACHER chưa được tạo"));

        // 1. Tạo user
        User user = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .status("ACTIVE")
                .roles(new HashSet<>(Set.of(teacherRole)))
                .build();
        userRepo.save(user);

        // 2. Tạo teacher profile
        Teacher teacher = Teacher.builder()
                .user(user)
                .instrument(req.getInstrument())
                .gender(req.getGender())
                .position(req.getPosition())
                .teachingType(req.getTeachingType())
                .status(req.getStatus() != null ? req.getStatus() : "ACTIVE")
                .note(req.getNote())
                .build();
        teacherRepo.save(teacher);

        return mapTeacherToListResponse(teacher);
    }

    // ====== SỬA GIÁO VIÊN ======
    public TeacherListResponse updateTeacher(Long teacherId, UpdateTeacherRequest req) {
        Teacher teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher không tồn tại"));

        User user = teacher.getUser();

        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getEmail() != null) user.setEmail(req.getEmail());
        if (req.getPhone() != null) user.setPhone(req.getPhone());

        if (req.getInstrument() != null) teacher.setInstrument(req.getInstrument());
        if (req.getGender() != null) teacher.setGender(req.getGender());
        if (req.getPosition() != null) teacher.setPosition(req.getPosition());
        if (req.getTeachingType() != null) teacher.setTeachingType(req.getTeachingType());
        if (req.getStatus() != null) teacher.setStatus(req.getStatus());
        if (req.getNote() != null) teacher.setNote(req.getNote());

        userRepo.save(user);
        teacherRepo.save(teacher);

        return mapTeacherToListResponse(teacher);
    }

    // ====== XÓA GIÁO VIÊN ======
    public void deleteTeacher(Long teacherId) {
        Teacher teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher không tồn tại"));

        User user = teacher.getUser();

        // XÓA MỀM
        teacher.setStatus("QUIT");
        user.setStatus("INACTIVE");
        teacherRepo.save(teacher);
        userRepo.save(user);
    }

    // ====== TẠO HỌC VIÊN ======
    public StudentListResponse createStudent(CreateStudentRequest req) {
        if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            throw new RuntimeException("Username đã tồn tại");
        }

        Role studentRole = roleRepo.findByName("ROLE_STUDENT")
                .orElseThrow(() -> new RuntimeException("ROLE_STUDENT chưa được tạo"));

        // 1. Tạo user
        User user = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .status("ACTIVE")
                .roles(new HashSet<>(Set.of(studentRole)))
                .build();
        userRepo.save(user);

        // 2. Lấy teacher & support (nếu có)
        Teacher mainTeacher = null;
        if (req.getMainTeacherId() != null) {
            mainTeacher = teacherRepo.findById(req.getMainTeacherId())
                    .orElseThrow(() -> new RuntimeException("Teacher không tồn tại"));
        }

        User careStaff = null;
        if (req.getCareStaffUserId() != null) {
            careStaff = userRepo.findById(req.getCareStaffUserId())
                    .orElseThrow(() -> new RuntimeException("User CSKH không tồn tại"));
        }

        // 3. Lấy course (nếu có)
        Course course = null;
        if (req.getCourseId() != null) {
            course = courseRepo.findById(req.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Course không tồn tại"));
        }

        // 4. Tính total/completed/remaining sessions
        Integer totalSessions = req.getTotalSessions();
        if (totalSessions == null && course != null && course.getTotalSessions() != null) {
            totalSessions = course.getTotalSessions();
        }

        Integer completedSessions = req.getCompletedSessions() != null
                ? req.getCompletedSessions()
                : 0;

        Integer remainingSessions = req.getRemainingSessions();
        if (remainingSessions == null && totalSessions != null) {
            remainingSessions = totalSessions - completedSessions;
        }

        // 5. Tạo student profile
        Student student = Student.builder()
                .user(user)
                .mainTeacher(mainTeacher)
                .careStaff(careStaff)
                .course(course)
                .parentName(req.getParentName())
                .parentPhone(req.getParentPhone())
                .parentEmail(req.getParentEmail())
                .lessonType(req.getLessonType())
                .scheduleText(req.getScheduleText())
                .currentTimeSlot(req.getCurrentTimeSlot())
                .newTimeSlot(req.getNewTimeSlot())
                .tuitionPaidDate(req.getTuitionPaidDate())
                .totalSessions(totalSessions)
                .completedSessions(completedSessions)
                .remainingSessions(remainingSessions)
                .status(req.getStatus() != null ? req.getStatus() : "ACTIVE")
                .note(req.getNote())
                .build();
        studentRepo.save(student);

        return mapStudentToListResponse(student);
    }

    // ====== SỬA HỌC VIÊN ======
    public StudentListResponse updateStudent(Long studentId, CreateStudentRequest req) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student không tồn tại"));

        User user = student.getUser();

        // Không đổi username ở đây để tránh rối, FE cũng đã disable
        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getEmail() != null) user.setEmail(req.getEmail());
        if (req.getPhone() != null) user.setPhone(req.getPhone());

        // Đổi mật khẩu nếu có gửi lên
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        // Thông tin phụ huynh
        student.setParentName(req.getParentName());
        student.setParentPhone(req.getParentPhone());
        student.setParentEmail(req.getParentEmail());

        // Trạng thái & note
        if (req.getStatus() != null) {
            student.setStatus(req.getStatus());
        }
        student.setNote(req.getNote());

        // (Nếu sau này muốn cho sửa mainTeacher / course, có thể xử lý thêm ở đây)

        userRepo.save(user);
        studentRepo.save(student);

        return mapStudentToListResponse(student);
    }

    // ====== XÓA HỌC VIÊN ======
    public void deleteStudent(Long studentId) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student không tồn tại"));

        User user = student.getUser();

        // XÓA MỀM giống teacher
        student.setStatus("STOPPED");
        if (user != null) {
            user.setStatus("INACTIVE");
        }

        studentRepo.save(student);
        if (user != null) {
            userRepo.save(user);
        }

        // Nếu muốn xóa hẳn DB:
        // studentRepo.delete(student);
        // if (user != null) userRepo.delete(user);
    }

    // ====== GÁN CSKH CHO HỌC VIÊN ======
    public StudentListResponse assignSupport(AssignSupportRequest req) {
        Student student = studentRepo.findById(req.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student không tồn tại"));

        User supportUser = userRepo.findById(req.getSupportUserId())
                .orElseThrow(() -> new RuntimeException("User CSKH không tồn tại"));

        student.setCareStaff(supportUser);
        studentRepo.save(student);

        return mapStudentToListResponse(student);
    }

    // ====== LẤY DS TEACHER ======
    public List<TeacherListResponse> getAllTeachers() {
        return teacherRepo.findAll().stream()
                .map(this::mapTeacherToListResponse)
                .collect(Collectors.toList());
    }

    // ====== LẤY DS STUDENT ======
    public List<StudentListResponse> getAllStudents() {
        return studentRepo.findAll().stream()
                .map(this::mapStudentToListResponse)
                .collect(Collectors.toList());
    }

    // Helper map Student -> DTO
    private StudentListResponse mapStudentToListResponse(Student s) {
        Course c = s.getCourse();

        return StudentListResponse.builder()
                .id(s.getId())
                .userId(s.getUser().getId())
                .username(s.getUser().getUsername())
                .fullName(s.getUser().getFullName())
                .email(s.getUser().getEmail())
                .phone(s.getUser().getPhone())
                .parentName(s.getParentName())
                .parentPhone(s.getParentPhone())
                .lessonType(s.getLessonType())
                .scheduleText(s.getScheduleText())
                .totalSessions(s.getTotalSessions())
                .completedSessions(s.getCompletedSessions())
                .remainingSessions(s.getRemainingSessions())
                .status(s.getStatus())
                .mainTeacherName(
                        s.getMainTeacher() != null
                                ? s.getMainTeacher().getUser().getFullName()
                                : null
                )
                .careStaffName(
                        s.getCareStaff() != null
                                ? s.getCareStaff().getFullName()
                                : null
                )
                .courseId(c != null ? c.getId() : null)
                .courseName(c != null ? c.getName() : null)
                .build();
    }

    // Helper map Teacher -> DTO
    private TeacherListResponse mapTeacherToListResponse(Teacher t) {
        return TeacherListResponse.builder()
                .id(t.getId())
                .userId(t.getUser().getId())
                .username(t.getUser().getUsername())
                .fullName(t.getUser().getFullName())
                .email(t.getUser().getEmail())
                .phone(t.getUser().getPhone())
                .instrument(t.getInstrument())
                .gender(t.getGender())
                .position(t.getPosition())
                .teachingType(t.getTeachingType())
                .status(t.getStatus())
                .note(t.getNote())
                .build();
    }

    // ====== TẠO USER CSKH (SUPPORT) ======
    public User createSupportUser(CreateSupportUserRequest req) {
        if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            throw new RuntimeException("Username đã tồn tại");
        }

        Role supportRole = roleRepo.findByName("ROLE_SUPPORT")
                .orElseThrow(() -> new RuntimeException("ROLE_SUPPORT chưa được tạo"));

        User user = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .status(req.getStatus() != null ? req.getStatus() : "ACTIVE")
                .roles(new HashSet<>(Set.of(supportRole)))
                .build();

        return userRepo.save(user);
    }

    // ====== TẠO USER ADMIN (ROLE_ADMIN) ======
    public User createAdminUser(CreateSupportUserRequest req) {
        if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            throw new RuntimeException("Username đã tồn tại");
        }

        Role adminRole = roleRepo.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("ROLE_ADMIN chưa được tạo"));

        User user = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .status(req.getStatus() != null ? req.getStatus() : "ACTIVE")
                .roles(new HashSet<>(Set.of(adminRole)))
                .build();

        return userRepo.save(user);
    }
}
