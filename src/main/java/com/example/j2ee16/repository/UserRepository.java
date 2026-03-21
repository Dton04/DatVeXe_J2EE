package com.example.j2ee16.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.example.j2ee16.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
}
