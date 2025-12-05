package com.solo.solo_music_academy.config;

import com.solo.solo_music_academy.entity.Role;
import com.solo.solo_music_academy.entity.User;
import com.solo.solo_music_academy.repository.RoleRepository;
import com.solo.solo_music_academy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;

    @Override
    public void run(String... args) throws Exception {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 1. Tạo các role nếu chưa có
        Role superAdminRole = roleRepo.findByName("ROLE_SUPER_ADMIN")
                .orElseGet(() -> roleRepo.save(Role.builder().name("ROLE_SUPER_ADMIN").build()));
        Role supportRole = roleRepo.findByName("ROLE_SUPPORT")
                .orElseGet(() -> roleRepo.save(Role.builder().name("ROLE_SUPPORT").build()));
        Role teacherRole = roleRepo.findByName("ROLE_TEACHER")
                .orElseGet(() -> roleRepo.save(Role.builder().name("ROLE_TEACHER").build()));
        Role studentRole = roleRepo.findByName("ROLE_STUDENT")
                .orElseGet(() -> roleRepo.save(Role.builder().name("ROLE_STUDENT").build()));

        // 2. SUPER_ADMIN
        if (userRepo.findByUsername("solo_admin").isEmpty()) {
            User admin = User.builder()
                    .username("solo_admin")
                    .password(encoder.encode("123456"))  // tạm
                    .fullName("Solo Music Admin")
                    .email("admin@solo-music.com")
                    .status("ACTIVE")
                    .roles(new HashSet<>(Set.of(superAdminRole)))
                    .build();
            userRepo.save(admin);
        }

        // 3. SUPPORT
        if (userRepo.findByUsername("support_01").isEmpty()) {
            User support = User.builder()
                    .username("support_01")
                    .password(encoder.encode("123456"))
                    .fullName("CSKH 01")
                    .email("support01@solo-music.com")
                    .status("ACTIVE")
                    .roles(new HashSet<>(Set.of(supportRole)))
                    .build();
            userRepo.save(support);
        }

        // 4. TEACHER
        if (userRepo.findByUsername("teacher_01").isEmpty()) {
            User teacher = User.builder()
                    .username("teacher_01")
                    .password(encoder.encode("123456"))
                    .fullName("GV Piano 01")
                    .email("teacher01@solo-music.com")
                    .status("ACTIVE")
                    .roles(new HashSet<>(Set.of(teacherRole)))
                    .build();
            userRepo.save(teacher);
        }

        // 5. STUDENT
        if (userRepo.findByUsername("student_01").isEmpty()) {
            User student = User.builder()
                    .username("student_01")
                    .password(encoder.encode("123456"))
                    .fullName("Học viên Demo 01")
                    .email("student01@solo-music.com")
                    .status("ACTIVE")
                    .roles(new HashSet<>(Set.of(studentRole)))
                    .build();
            userRepo.save(student);
        }

        System.out.println("✅ Seed dữ liệu roles & users xong.");
    }
}
