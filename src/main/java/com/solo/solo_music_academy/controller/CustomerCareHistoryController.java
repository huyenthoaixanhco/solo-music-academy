package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.CustomerCareHistoryResponse;
import com.solo.solo_music_academy.service.CustomerCareHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/care-history")
@RequiredArgsConstructor
public class CustomerCareHistoryController {

    private final CustomerCareHistoryService customerCareHistoryService;

    /**
     * ADMIN xem mọi lịch sử CSKH của 1 học viên bất kỳ
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<CustomerCareHistoryResponse>> getHistoryByStudent(
            @PathVariable Long studentId
    ) {
        return ResponseEntity.ok(
                customerCareHistoryService.adminGetHistoryByStudent(studentId)
        );
    }
}
