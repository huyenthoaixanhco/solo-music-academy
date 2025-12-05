package com.solo.solo_music_academy.entity;

public enum StudentPackageStatus {
    ACTIVE,     // đang học
    PAUSED,     // tạm dừng (bảo lưu)
    STOPPED,    // dừng học hẳn, còn buổi thì hoàn tiền / bỏ
    COMPLETED   // đã học hết số buổi
}
