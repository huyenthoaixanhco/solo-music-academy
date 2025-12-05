package com.solo.solo_music_academy.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ví dụ: ROLE_SUPER_ADMIN, ROLE_SUPPORT, ROLE_TEACHER, ROLE_STUDENT
    @Column(nullable = false, unique = true, length = 50)
    private String name;
}
