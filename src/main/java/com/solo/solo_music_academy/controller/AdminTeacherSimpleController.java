package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.SimpleUserResponse;
import com.solo.solo_music_academy.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/teachers")
@RequiredArgsConstructor
public class AdminTeacherSimpleController {

    private final TeacherRepository teacherRepository;

    // GET /admin/teachers/simple
    @GetMapping("/simple")
    public ResponseEntity<List<SimpleUserResponse>> getSimpleTeachers() {
        var result = teacherRepository.findAll().stream()
                .map(t -> SimpleUserResponse.builder()
                        .id(t.getId())
                        .fullName(t.getUser().getFullName())
                        .build()
                )
                .toList();

        return ResponseEntity.ok(result);
    }
}
