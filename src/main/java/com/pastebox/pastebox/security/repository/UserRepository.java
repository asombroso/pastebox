package com.pastebox.pastebox.security.repository;

import com.pastebox.pastebox.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUsername(String name);
    Optional<User> findUserByCode(String code);
    boolean existsByUsername(String name);
}
