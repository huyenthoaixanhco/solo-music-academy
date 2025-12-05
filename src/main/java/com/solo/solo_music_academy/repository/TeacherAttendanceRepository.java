package com.solo.solo_music_academy.repository;

import com.solo.solo_music_academy.entity.TeacherAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TeacherAttendanceRepository extends JpaRepository<TeacherAttendance, Long> {

    List<TeacherAttendance> findByDateIn(Collection<LocalDate> dates);

    Optional<TeacherAttendance> findBySlotIdAndDate(Long slotId, LocalDate date);
}
