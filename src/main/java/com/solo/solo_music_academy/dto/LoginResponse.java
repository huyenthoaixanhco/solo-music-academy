package com.solo.solo_music_academy.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String username;
    private String fullName;
    private Set<String> roles;   // ROLE_SUPER_ADMIN, ROLE_SUPPORT, ...
}
