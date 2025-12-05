package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.CustomerCareHistoryRequest;
import com.solo.solo_music_academy.dto.CustomerCareHistoryResponse;
import com.solo.solo_music_academy.service.CustomerCareHistoryService;
import com.solo.solo_music_academy.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/support/care-history")
@RequiredArgsConstructor
public class SupportCareController {

    private final CustomerCareHistoryService customerCareHistoryService;
    private final SupportService supportService;

    /**
     * SUPPORT tạo log CSKH cho học viên của mình
     */
    @PostMapping
    public ResponseEntity<CustomerCareHistoryResponse> createCareHistory(
            @RequestBody CustomerCareHistoryRequest req
    ) {
        return ResponseEntity.ok(
                customerCareHistoryService.supportCreateCareHistory(req)
        );
    }

    /**
     * SUPPORT xem lịch sử CSKH của 1 học viên (chỉ xem được học viên thuộc mình)
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<CustomerCareHistoryResponse>> getHistoryByStudent(
            @PathVariable Long studentId
    ) {
        return ResponseEntity.ok(
                customerCareHistoryService.supportGetHistoryByStudent(studentId)
        );
    }

    /**
     * SUPPORT xem tất cả log CSKH mà chính mình đã tạo
     */
    @GetMapping("/my")
    public ResponseEntity<List<CustomerCareHistoryResponse>> getMyCareHistory() {
        return ResponseEntity.ok(
                supportService.getMyCareHistory()
        );
    }
}
