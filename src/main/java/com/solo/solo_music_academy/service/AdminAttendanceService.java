package com.solo.solo_music_academy.service;

import com.solo.solo_music_academy.dto.AdHocSlotRequest;
import com.solo.solo_music_academy.dto.AttendanceSlotResponse;
import com.solo.solo_music_academy.dto.SimpleTeacherResponse;
import com.solo.solo_music_academy.entity.*;
import com.solo.solo_music_academy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AdminAttendanceService {

    private final TeacherScheduleSlotRepository slotRepo;
    private final TeacherAttendanceRepository attendanceRepo;
    private final StudentPackageRepository packageRepo;
    private final TeacherRepository teacherRepo;
    private final FileStorageService fileStorageService;


    //LẤY DANH SÁCH (CÓ LOGIC LỊCH BÙ & TRẠNG THÁI)
 
    public List<AttendanceSlotResponse> getWeekAttendance(LocalDate startDate, Long teacherId) {
        LocalDate monday = toMonday(startDate);
        LocalDate weekStart = monday;
        LocalDate weekEnd = monday.plusDays(6);

        List<LocalDate> weekDates = IntStream.range(0, 7).mapToObj(monday::plusDays).toList();
        Map<Integer, LocalDate> dowToDate = weekDates.stream().collect(Collectors.toMap(d -> d.getDayOfWeek().getValue(), d -> d));
        List<Integer> dows = new ArrayList<>(dowToDate.keySet());

        //Slot cố định
        List<TeacherScheduleSlot> regularSlots;
        if (teacherId == null) {
            regularSlots = packageRepo.findAllActiveSlotsForWeek(StudentPackageStatus.ACTIVE, weekStart, weekEnd, dows);
        } else {
            regularSlots = packageRepo.findActiveSlotsForTeacherAndWeek(teacherId, StudentPackageStatus.ACTIVE, weekStart, weekEnd, dows);
        }

        //Slot dạy bù / Ad-hoc
        List<TeacherAttendance> attendancesInWeek = attendanceRepo.findByDateIn(weekDates);
        if (teacherId != null) {
            attendancesInWeek = attendancesInWeek.stream()
                    .filter(att -> att.getSlot().getTeacher().getId().equals(teacherId))
                    .toList();
        }
        List<TeacherScheduleSlot> adHocSlots = attendancesInWeek.stream().map(TeacherAttendance::getSlot).toList();

        //Gộp danh sách
        Set<TeacherScheduleSlot> allSlots = new HashSet<>(regularSlots);
        allSlots.addAll(adHocSlots);

        Map<String, TeacherAttendance> attMap = attendancesInWeek.stream()
                .collect(Collectors.toMap(att -> buildAttKey(att.getSlot().getId(), att.getDate()), att -> att));

        List<AttendanceSlotResponse> result = new ArrayList<>();

        for (TeacherScheduleSlot slot : allSlots) {
            LocalDate date = dowToDate.get(slot.getDayOfWeek());
            if (date == null) continue;

            String key = buildAttKey(slot.getId(), date);
            TeacherAttendance att = attMap.get(key);

            if (att != null && att.getStatus() == AttendanceStatus.CANCELLED) continue;

            String studentName = buildStudentNameForSlotAndDate(slot.getId(), date);
            boolean isAdHoc = (studentName == null && att != null);

            // Nếu là lịch bù, ưu tiên lấy tên từ note
            if (isAdHoc && att.getNote() != null && !att.getNote().isEmpty()) {
                studentName = att.getNote();
            }

            AttendanceSlotResponse dto = AttendanceSlotResponse.builder()
                    .slotId(slot.getId())
                    .attendanceId(att != null ? att.getId() : null)
                    .dayOfWeek(slot.getDayOfWeek())
                    .date(date)
                    .startTime(formatTime(slot.getStartTime()))
                    .endTime(formatTime(slot.getEndTime()))
                    .teacherId(slot.getTeacher().getId())
                    .teacherName(slot.getTeacher().getUser().getFullName())
                    .studentName(studentName)
                    .status(att != null && att.getStatus() != null ? att.getStatus().name() : null)
                    .hasImage(att != null && att.getImagePath() != null)
                    .imageUrl(att != null ? att.getImagePath() : null)
                    .isAdHoc(isAdHoc)
                    .build();

            result.add(dto);
        }
        result.sort(Comparator.comparing(AttendanceSlotResponse::getDayOfWeek).thenComparing(AttendanceSlotResponse::getStartTime));
        return result;
    }


    //CHẤM CÔNG
 
    @Transactional
    public void markAttendance(Long slotId, LocalDate date, String statusStr, MultipartFile image) throws IOException {
        TeacherScheduleSlot slot = slotRepo.findById(slotId).orElseThrow(() -> new IllegalArgumentException("Slot not found"));
        
        // Chuyển String -> Enum (PRESENT hoặc LATE)
        AttendanceStatus newStatus = (statusStr != null && !statusStr.isBlank()) 
                ? AttendanceStatus.valueOf(statusStr.toUpperCase()) 
                : null;

        TeacherAttendance att = attendanceRepo.findBySlotIdAndDate(slotId, date)
                .orElseGet(() -> TeacherAttendance.builder().slot(slot).date(date).build());
        
        AttendanceStatus oldStatus = att.getStatus();
        att.setStatus(newStatus);
        
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.saveAttendanceImage(image, slotId, date);
            att.setImagePath(imageUrl);
        }
        attendanceRepo.save(att);

        // Cập nhật số buổi học viên
        int delta = attendanceDelta(oldStatus, newStatus);
        if (delta != 0) {
            updateStudentPackageSessions(slotId, date, delta);
        }
    }


    //TẠO LỊCH BÙ

    @Transactional
    public void createAdHocSlot(AdHocSlotRequest req) {
        Teacher teacher = teacherRepo.findById(req.getTeacherId()).orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
        TeacherScheduleSlot slot = new TeacherScheduleSlot();
        slot.setTeacher(teacher);
        slot.setDayOfWeek(req.getDate().getDayOfWeek().getValue());
        slot.setStartTime(req.getStartTime());
        slot.setEndTime(req.getEndTime());
        slotRepo.save(slot);

        TeacherAttendance att = TeacherAttendance.builder()
                .slot(slot)
                .date(req.getDate())
                .status(null)
                .note(req.getStudentName()) // Lưu tên học viên
                .build();
        attendanceRepo.save(att);
    }

    //HỦY & XÓA ẢNH

    @Transactional
    public void deleteSlotInstance(Long slotId, LocalDate date) {
        TeacherAttendance att = attendanceRepo.findBySlotIdAndDate(slotId, date).orElse(null);
        if (att == null) {
            TeacherScheduleSlot slot = slotRepo.findById(slotId).orElseThrow(() -> new IllegalArgumentException("Slot not found"));
            att = TeacherAttendance.builder().slot(slot).date(date).status(AttendanceStatus.CANCELLED).build();
            attendanceRepo.save(att);
            return;
        }
        AttendanceStatus oldStatus = att.getStatus();
        String imageUrl = att.getImagePath();
        if (imageUrl != null && !imageUrl.isBlank()) {
            try { fileStorageService.deleteAttendanceImageByUrl(imageUrl); } catch (Exception e) {}
        }
        
        // Hủy -> delta tính với trạng thái đích là CANCELLED (hoặc null)
        int delta = attendanceDelta(oldStatus, AttendanceStatus.CANCELLED);
        if (delta != 0) {
            updateStudentPackageSessions(slotId, date, delta);
        }
        att.setStatus(AttendanceStatus.CANCELLED);
        att.setImagePath(null);
        attendanceRepo.save(att);
    }

    @Transactional
    public void deleteAttendanceImage(Long slotId, LocalDate date) throws IOException {
        TeacherAttendance att = attendanceRepo.findBySlotIdAndDate(slotId, date).orElseThrow(() -> new IllegalArgumentException("Not found"));
        String img = att.getImagePath();
        AttendanceStatus old = att.getStatus();
        if ((img == null || img.isBlank()) && old == null) return;
        if (img != null) fileStorageService.deleteAttendanceImageByUrl(img);
        
        att.setImagePath(null);
        att.setStatus(null);
        attendanceRepo.save(att);
        
        int delta = attendanceDelta(old, null);
        if (delta != 0) {
            updateStudentPackageSessions(slotId, date, delta);
        }
    }


    
    //Tính toán cộng trừ buổi: PRESENT hoặc LATE đều tính là 1 buổi đã học
    private boolean isSessionDeducted(AttendanceStatus status) {
        return status == AttendanceStatus.PRESENT || status == AttendanceStatus.LATE;
    }

    private int attendanceDelta(AttendanceStatus oldStatus, AttendanceStatus newStatus) {
        int oldVal = isSessionDeducted(oldStatus) ? 1 : 0;
        int newVal = isSessionDeducted(newStatus) ? 1 : 0;
        return newVal - oldVal;
    }

    private void updateStudentPackageSessions(Long slotId, LocalDate date, int delta) {
        List<StudentPackage> packages = packageRepo.findBySlotIdAndStatusAndDate(slotId, StudentPackageStatus.ACTIVE, date);
        for (StudentPackage pkg : packages) {
            int completed = Optional.ofNullable(pkg.getSessionsCompleted()).orElse(0) + delta;
            if (completed < 0) completed = 0;
            pkg.setSessionsCompleted(completed);
            pkg.syncSessionNumbers();
        }
        packageRepo.saveAll(packages);
    }

    public List<SimpleTeacherResponse> getAttendanceTeachers() {
        return teacherRepo.findAll().stream().map(t -> SimpleTeacherResponse.builder().id(t.getId()).fullName(t.getUser().getFullName()).build()).toList();
    }
    private LocalDate toMonday(LocalDate d) { return d.minusDays(d.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue()); }
    private String buildAttKey(Long s, LocalDate d) { return s + "#" + d; }
    private String formatTime(LocalTime t) { return t == null ? null : t.toString().substring(0, 5); }
    private String buildStudentNameForSlotAndDate(Long slotId, LocalDate date) {
        List<StudentPackage> packages = packageRepo.findBySlotIdAndStatusAndDate(slotId, StudentPackageStatus.ACTIVE, date);
        if (packages.isEmpty()) return null;
        List<String> names = packages.stream().map(pkg -> pkg.getStudent().getUser().getFullName()).filter(Objects::nonNull).distinct().toList();
        if (names.isEmpty()) return null;
        return names.size() == 1 ? names.get(0) : names.get(0) + " + " + (names.size() - 1) + " HV";
    }
}