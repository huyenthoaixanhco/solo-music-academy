package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.SimpleUserResponse;
import com.solo.solo_music_academy.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/students")
@RequiredArgsConstructor
public class AdminStudentSimpleController {

    private final StudentRepository studentRepository;

    // GET /admin/students/simple
    @GetMapping("/simple")
    public ResponseEntity<List<SimpleUserResponse>> getSimpleStudents() {
        var result = studentRepository.findAll().stream()
                .map(s -> SimpleUserResponse.builder()
                        .id(s.getId())
                        .fullName(s.getUser().getFullName())
                        .build()
                )
                .toList();

        return ResponseEntity.ok(result);
    }
}
