// src/main/java/com/solo/solo_music_academy/dto/SimpleTeacherResponse.java
package com.solo.solo_music_academy.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimpleTeacherResponse {
    private Long id;
    private String fullName;
}
