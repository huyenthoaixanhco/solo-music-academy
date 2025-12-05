package com.solo.solo_music_academy.repository;

import com.solo.solo_music_academy.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Long> {

    // Support xem lead của chính mình
    List<Lead> findBySupportUser_IdOrderByCreatedAtDesc(Long supportUserId);

    // Admin xem toàn bộ lead (mới nhất lên trên)
    List<Lead> findAllByOrderByCreatedAtDesc();
}
