package com.solo.solo_music_academy.repository;

import com.solo.solo_music_academy.entity.Teacher;
import com.solo.solo_music_academy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByUser(User user);
    Optional<Teacher> findByUserId(Long userId);
}
