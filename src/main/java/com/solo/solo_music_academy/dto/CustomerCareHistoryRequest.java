package com.solo.solo_music_academy.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerCareHistoryRequest {

    private Long studentId;    // id học viên
    private String careType;   // loại chăm sóc (Call, Chat, Meeting,...)
    private String channel;    // kênh (Zalo, FB, Phone,...)
    private String content;    // nội dung trao đổi
    private String result;     // kết quả buổi CSKH
    private Boolean important; // đánh dấu quan trọng
    private String nextCareTime; // thời gian CSKH tiếp theo, dạng ISO-8601 string
}
