package com.solo.solo_music_academy.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateCustomerCareHistoryRequest {

    // Kênh CSKH: CALL / ZALO / FB / EMAIL / IN_PERSON ...
    private String channel;

    // Nội dung chính, ngắn gọn
    private String summary;

    // Hành động tiếp theo (nếu có)
    private String nextAction;

    // Ngày dự kiến cho hành động tiếp theo
    private LocalDate nextActionDate;

    // Ghi chú thêm
    private String note;
}
