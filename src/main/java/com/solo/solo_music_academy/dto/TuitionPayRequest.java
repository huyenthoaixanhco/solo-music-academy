package com.solo.solo_music_academy.dto;

import lombok.Data;

@Data
public class TuitionPayRequest {

    private Long amount;        // số tiền thực tế thu
    private String paidDate;    // "yyyy-MM-dd", nếu null thì dùng today
}
