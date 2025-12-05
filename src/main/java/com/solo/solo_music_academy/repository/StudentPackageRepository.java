package com.solo.solo_music_academy.repository;

import com.solo.solo_music_academy.entity.StudentPackage;
import com.solo.solo_music_academy.entity.StudentPackageStatus;
import com.solo.solo_music_academy.entity.TeacherScheduleSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentPackageRepository extends JpaRepository<StudentPackage, Long> {

    @Query("SELECT DISTINCT sp " +
           "FROM StudentPackage sp " +
           "JOIN sp.packageSlots sps " +
           "WHERE sps.slot.id = :slotId " +
           "AND sp.status = :status")
    List<StudentPackage> findBySlotIdAndStatus(@Param("slotId") Long slotId,
                                               @Param("status") StudentPackageStatus status);

    @Query("SELECT DISTINCT sp " +
           "FROM StudentPackage sp " +
           "JOIN sp.packageSlots sps " +
           "WHERE sps.slot.id = :slotId " +
           "AND sp.status = :status " +
           "AND (sp.currentPeriodStart IS NULL OR sp.currentPeriodStart <= :date) " +
           "AND (sp.currentPeriodEnd   IS NULL OR sp.currentPeriodEnd   >= :date)")
    List<StudentPackage> findBySlotIdAndStatusAndDate(@Param("slotId") Long slotId,
                                                      @Param("status") StudentPackageStatus status,
                                                      @Param("date") LocalDate date);

    // ✅ Hàm cũ: Lọc theo Giáo viên (Giữ nguyên)
    @Query("SELECT DISTINCT sps.slot " +
           "FROM StudentPackage sp " +
           "JOIN sp.packageSlots sps " +
           "WHERE sp.teacher.id = :teacherId " +
           "AND sp.status = :status " +
           "AND (sp.currentPeriodStart IS NULL OR sp.currentPeriodStart <= :weekEnd) " +
           "AND (sp.currentPeriodEnd   IS NULL OR sp.currentPeriodEnd   >= :weekStart) " +
           "AND sps.slot.dayOfWeek IN :dows")
    List<TeacherScheduleSlot> findActiveSlotsForTeacherAndWeek(
            @Param("teacherId") Long teacherId,
            @Param("status") StudentPackageStatus status,
            @Param("weekStart") LocalDate weekStart,
            @Param("weekEnd") LocalDate weekEnd,
            @Param("dows") List<Integer> dows
    );

    // 🔥 HÀM MỚI (Thêm vào đây): Lấy TẤT CẢ slot active (cho Master View)
    // Logic: Giống hàm trên nhưng bỏ dòng "WHERE sp.teacher.id = :teacherId"
    @Query("SELECT DISTINCT sps.slot " +
           "FROM StudentPackage sp " +
           "JOIN sp.packageSlots sps " +
           "WHERE sp.status = :status " +
           "AND (sp.currentPeriodStart IS NULL OR sp.currentPeriodStart <= :weekEnd) " +
           "AND (sp.currentPeriodEnd   IS NULL OR sp.currentPeriodEnd   >= :weekStart) " +
           "AND sps.slot.dayOfWeek IN :dows")
    List<TeacherScheduleSlot> findAllActiveSlotsForWeek(
            @Param("status") StudentPackageStatus status,
            @Param("weekStart") LocalDate weekStart,
            @Param("weekEnd") LocalDate weekEnd,
            @Param("dows") List<Integer> dows
    );

    // ⭐ HỌC VIÊN: lấy các gói ACTIVE của 1 học viên
    List<StudentPackage> findByStudentIdAndStatus(Long studentId, StudentPackageStatus status);

    // Lấy các gói theo username, sort mới nhất trước
    List<StudentPackage> findByStudentUserUsernameOrderByIdDesc(String username);

    // ⭐ Helper: gói ACTIVE mới nhất theo currentPeriodStart
    Optional<StudentPackage> findTopByStudentIdAndStatusOrderByCurrentPeriodStartDesc(
            Long studentId,
            StudentPackageStatus status
    );
    Optional<StudentPackage> findFirstByStudentIdOrderByIdDesc(Long studentId);
    Optional<StudentPackage> findTopByStudentIdOrderByCurrentPeriodStartDesc(Long studentId);
}