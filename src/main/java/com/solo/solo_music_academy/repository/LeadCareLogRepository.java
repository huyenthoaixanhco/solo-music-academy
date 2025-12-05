package com.solo.solo_music_academy.repository;

import com.solo.solo_music_academy.entity.LeadCareLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeadCareLogRepository extends JpaRepository<LeadCareLog, Long> {

    List<LeadCareLog> findByLeadIdOrderByCareTimeDesc(Long leadId);

    Optional<LeadCareLog> findTopByLeadIdOrderByCareTimeDesc(Long leadId);

    Optional<LeadCareLog> findTopByLeadIdAndNextCareTimeIsNotNullOrderByNextCareTimeAsc(Long leadId);

    // THÊM DÒNG NÀY ĐỂ XOÁ LOG KHI XOÁ LEAD
    void deleteByLeadId(Long leadId);
}
