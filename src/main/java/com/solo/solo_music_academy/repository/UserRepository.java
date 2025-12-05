package com.solo.solo_music_academy.repository;

import com.solo.solo_music_academy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    @Query("""
           select distinct u
           from User u
           join u.roles r
           where r.name = 'ROLE_SUPPORT'
           """)
    List<User> findAllSupportUsers();
}
