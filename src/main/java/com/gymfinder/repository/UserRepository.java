package com.gymfinder.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gymfinder.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    User findByEmail(String email);
}