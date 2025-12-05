package com.solo.solo_music_academy.repository;

import com.solo.solo_music_academy.entity.CustomerCareHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CustomerCareHistoryRepository extends JpaRepository<CustomerCareHistory, Long> {

    // Đếm số lần CSKH theo học viên
    Long countByStudentId(Long studentId);

    // Đếm số lần CSKH theo support (để thống kê)
    Long countBySupportUserId(Long supportUserId);

    // Lần CSKH gần nhất cho 1 học viên
    Optional<CustomerCareHistory> findTopByStudentIdOrderByCareTimeDesc(Long studentId);

    // Lần CSKH gần nhất của 1 support bất kỳ học viên nào
    Optional<CustomerCareHistory> findTopBySupportUserIdOrderByCareTimeDesc(Long supportUserId);

    // Lịch sử CSKH theo support
    List<CustomerCareHistory> findBySupportUserIdOrderByCareTimeDesc(Long supportUserId);

    // Lịch sử CSKH theo học viên
    List<CustomerCareHistory> findByStudentIdOrderByCareTimeDesc(Long studentId);
     List<CustomerCareHistory> findBySupportUserIdAndNextCareTimeBetweenOrderByNextCareTimeAsc(
            Long supportUserId,
            LocalDateTime from,
            LocalDateTime to
    );
}
