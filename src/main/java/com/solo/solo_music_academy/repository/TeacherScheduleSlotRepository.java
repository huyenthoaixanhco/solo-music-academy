package com.solo.solo_music_academy.repository;

import com.solo.solo_music_academy.entity.TeacherScheduleSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TeacherScheduleSlotRepository extends JpaRepository<TeacherScheduleSlot, Long> {

    List<TeacherScheduleSlot> findByDayOfWeekIn(Collection<Integer> dayOfWeeks);

    // tìm slot trùng teacher + thứ + giờ để tái sử dụng (1 slot có thể nhiều học viên group)
    Optional<TeacherScheduleSlot> findByTeacherIdAndDayOfWeekAndStartTimeAndEndTime(
            Long teacherId,
            int dayOfWeek,
            LocalTime startTime,
            LocalTime endTime
    );
    List<TeacherScheduleSlot> findByDayOfWeekInAndTeacher_Id(List<Integer> dows, Long teacherId);
}
