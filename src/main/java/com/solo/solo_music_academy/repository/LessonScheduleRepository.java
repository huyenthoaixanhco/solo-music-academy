package com.solo.solo_music_academy.repository;

import com.solo.solo_music_academy.entity.LessonSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonScheduleRepository extends JpaRepository<LessonSchedule, Long> {

    List<LessonSchedule> findByTeacherIdOrderByDayOfWeekAscStartTimeAsc(Long teacherId);

    List<LessonSchedule> findByStudentIdOrderByDayOfWeekAscStartTimeAsc(Long studentId);
}
