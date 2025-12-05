package com.solo.solo_music_academy.repository;

import com.solo.solo_music_academy.entity.Student;
import com.solo.solo_music_academy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUser(User user);

    // Lấy danh sách học viên được gán cho 1 support (careStaff)
    List<Student> findByCareStaffId(Long careStaffId);

    // Để support chỉ xem được học viên mình phụ trách
    Optional<Student> findByIdAndCareStaffId(Long id, Long careStaffId);

    // Đếm số học viên đang được 1 support phụ trách
    Long countByCareStaffId(Long careStaffId);

    // Danh sách học viên chưa có support
    List<Student> findByCareStaffIsNull();
    List<Student> findByRemainingSessionsLessThanEqual(Integer remainingSessions);
    Optional<Student> findByUserId(Long userId);
    Optional<Student> findByUserUsername(String username);

}
