package com.solo.solo_music_academy.controller;

import com.solo.solo_music_academy.dto.TuitionPayRequest;
import com.solo.solo_music_academy.service.TuitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/support/tuition")
@RequiredArgsConstructor
public class SupportTuitionController {

    private final TuitionService tuitionService;

    // Support bấm "Xác nhận đã đóng"
    @PostMapping("/students/{studentId}/packages/{packageId}/pay")
    public ResponseEntity<?> confirmPaid(
            @PathVariable Long studentId,
            @PathVariable Long packageId,
            @RequestBody TuitionPayRequest req
    ) {
        tuitionService.confirmTuitionPaid(studentId, packageId, req);
        return ResponseEntity.ok().build();
    }
}
